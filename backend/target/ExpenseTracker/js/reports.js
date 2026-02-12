// Reports JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Set current month and year as default
    const now = new Date();
    document.getElementById('reportMonth').value = getCurrentMonth();
    document.getElementById('reportYear').value = getCurrentYear();

    // Load initial report
    await loadMonthlyReport();

    // Event listeners
    document.getElementById('reportMonth').addEventListener('change', loadMonthlyReport);
    document.getElementById('reportYear').addEventListener('change', loadMonthlyReport);
});

async function loadMonthlyReport() {
    try {
        const month = parseInt(document.getElementById('reportMonth').value);
        const year = parseInt(document.getElementById('reportYear').value);

        const [expenses, income, budgets, categoryBreakdown, incomeSources] = await Promise.all([
            expenseAPI.getByMonth(month, year),
            incomeAPI.getByMonth(month, year),
            budgetAPI.getByMonth(month, year),
            expenseAPI.getCategoryBreakdown(month, year),
            incomeAPI.getSourcesSummary(month, year)
        ]);

        // Calculate totals
        const totalExpenses = expenses.reduce((sum, e) => sum + parseFloat(e.amount), 0);
        const totalIncome = income.reduce((sum, i) => sum + parseFloat(i.amount), 0);
        const savings = totalIncome - totalExpenses;
        const savingsPercent = totalIncome > 0 ? (savings / totalIncome * 100).toFixed(1) : 0;

        // Update report summary
        document.getElementById('reportIncome').textContent = formatCurrency(totalIncome);
        document.getElementById('reportExpenses').textContent = formatCurrency(totalExpenses);
        document.getElementById('reportSavings').textContent = formatCurrency(savings);
        document.getElementById('reportSavingsPercent').textContent = savingsPercent + '%';

        // Create charts
        createCategoryChart('reportCategoryChart', categoryBreakdown);
        createIncomeSourcesChart('reportIncomeChart', incomeSources);

        // Update report table
        updateReportTable(categoryBreakdown, budgets, expenses);

    } catch (error) {
        console.error('Error loading report:', error);
        showNotification('Error loading report data', 'error');
    }
}

async function updateReportTable(categoryBreakdown, budgets, expenses) {
    const tbody = document.querySelector('#reportTable tbody');
    tbody.innerHTML = '';

    const budgetMap = {};
    budgets.forEach(b => {
        budgetMap[b.category] = b.amount;
    });

    for (const [category, amount] of Object.entries(categoryBreakdown)) {
        const budget = budgetMap[category] || 0;
        const percentage = budget > 0 ? (amount / budget * 100).toFixed(1) : 0;
        const status = percentage >= 100 ? '<span style="color: #f56565;">Exceeded</span>' : 
                      percentage >= 80 ? '<span style="color: #f6ad55;">Warning</span>' : 
                      '<span style="color: #48bb78;">OK</span>';

        const row = tbody.insertRow();
        row.innerHTML = `
            <td><strong>${category}</strong></td>
            <td>${formatCurrency(amount)}</td>
            <td>${budget > 0 ? formatCurrency(budget) : '-'}</td>
            <td>${budget > 0 ? percentage + '%' : '-'}</td>
            <td>${status}</td>
        `;
    }

    if (tbody.innerHTML === '') {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No expenses this month</td></tr>';
    }
}
