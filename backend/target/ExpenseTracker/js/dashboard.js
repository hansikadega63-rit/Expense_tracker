// Dashboard JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Set current month and year as default
    const now = new Date();
    document.getElementById('monthSelect').value = getCurrentMonth();
    document.getElementById('yearSelect').value = getCurrentYear();

    // Load initial data
    await loadDashboardData();

    // Event listeners
    document.getElementById('monthSelect').addEventListener('change', loadDashboardData);
    document.getElementById('yearSelect').addEventListener('change', loadDashboardData);
});

async function loadDashboardData() {
    try {
        const month = parseInt(document.getElementById('monthSelect').value);
        const year = parseInt(document.getElementById('yearSelect').value);

        // Fetch all required data
        const [expenses, income, budgets, categoryBreakdown] = await Promise.all([
            expenseAPI.getByMonth(month, year),
            incomeAPI.getByMonth(month, year),
            budgetAPI.getByMonth(month, year),
            expenseAPI.getCategoryBreakdown(month, year)
        ]);

        // Calculate totals
        const totalExpenses = expenses.reduce((sum, e) => sum + parseFloat(e.amount), 0);
        const totalIncome = income.reduce((sum, i) => sum + parseFloat(i.amount), 0);
        const savings = totalIncome - totalExpenses;
        const budgetedTotal = budgets.reduce((sum, b) => sum + parseFloat(b.amount), 0);
        const budgetPercentage = budgetedTotal > 0 ? (totalExpenses / budgetedTotal * 100).toFixed(1) : 0;

        // Update summary cards
        document.getElementById('totalIncome').textContent = formatCurrency(totalIncome);
        document.getElementById('totalExpenses').textContent = formatCurrency(totalExpenses);
        document.getElementById('totalSavings').textContent = formatCurrency(savings);
        document.getElementById('budgetPercentage').textContent = budgetPercentage + '%';

        // Update color based on budget status
        const budgetCard = document.querySelector('.budget-card');
        if (budgetPercentage >= 100) {
            budgetCard.style.borderLeftColor = '#f56565';
        } else if (budgetPercentage >= 80) {
            budgetCard.style.borderLeftColor = '#f6ad55';
        } else {
            budgetCard.style.borderLeftColor = '#48bb78';
        }

        // Create charts
        createCategoryChart('categoryChart', categoryBreakdown);
        
        const monthlySummary = await expenseAPI.getMonthlySummary(year);
        createMonthlyChart('monthlyChart', monthlySummary);

        // Update budget alerts
        await updateBudgetAlerts(month, year);

        // Update recent transactions
        updateRecentTransactions(expenses);

    } catch (error) {
        console.error('Error loading dashboard:', error);
        showNotification('Error loading dashboard data', 'error');
    }
}

async function updateBudgetAlerts(month, year) {
    try {
        const alerts = await budgetAPI.getAlerts(month, year);
        const alertsContainer = document.getElementById('budgetAlerts');
        
        if (alerts.length === 0) {
            alertsContainer.innerHTML = '<p class="no-alerts">No budget alerts</p>';
            return;
        }

        alertsContainer.innerHTML = '';
        alerts.forEach(alert => {
            const alertDiv = document.createElement('div');
            alertDiv.className = alert.isExceeded ? 'alert alert-danger' : 'alert';
            alertDiv.innerHTML = `
                <strong>${alert.category}</strong>
                <p>Spent: ${formatCurrency(alert.spent)} / Budget: ${formatCurrency(alert.budgeted)}</p>
                <p>${alert.percentage.toFixed(1)}% of budget used</p>
            `;
            alertsContainer.appendChild(alertDiv);
        });
    } catch (error) {
        console.error('Error loading budget alerts:', error);
    }
}

function updateRecentTransactions(expenses) {
    const tbody = document.querySelector('#transactionsTable tbody');
    tbody.innerHTML = '';

    // Show only last 5 transactions
    const recentExpenses = expenses.slice(0, 5);
    
    recentExpenses.forEach(expense => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${formatDate(expense.expenseDate)}</td>
            <td><strong>${expense.category}</strong></td>
            <td>${expense.description || '-'}</td>
            <td>${formatCurrency(expense.amount)}</td>
        `;
    });

    if (recentExpenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">No expenses this month</td></tr>';
    }
}
