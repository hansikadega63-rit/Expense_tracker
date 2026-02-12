// Budget Management JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Set current year
    document.getElementById('budgetYear').value = getCurrentYear();

    // Form submission
    document.getElementById('budgetForm').addEventListener('submit', handleAddBudget);

    // Load all budgets
    await loadAllBudgets();
});

async function handleAddBudget(e) {
    e.preventDefault();

    try {
        const category = document.getElementById('budgetCategory').value;
        const amount = document.getElementById('budgetAmount').value;
        const month = parseInt(document.getElementById('budgetMonth').value);
        const year = parseInt(document.getElementById('budgetYear').value);

        if (!category || !amount || !month) {
            showNotification('Please fill in all required fields', 'error');
            return;
        }

        const result = await budgetAPI.add(category, amount, month, year);
        
        if (result.success) {
            showNotification('Budget set successfully!', 'success');
            document.getElementById('budgetForm').reset();
            document.getElementById('budgetYear').value = getCurrentYear();
            await loadAllBudgets();
        } else {
            showNotification('Failed to set budget', 'error');
        }
    } catch (error) {
        console.error('Error adding budget:', error);
        showNotification('Error setting budget', 'error');
    }
}

async function loadAllBudgets() {
    try {
        const budgets = await budgetAPI.getAll();
        
        // Get expenses for each month to calculate usage
        const budgetsByMonth = {};
        for (const budget of budgets) {
            const key = `${budget.month}-${budget.year}`;
            if (!budgetsByMonth[key]) {
                budgetsByMonth[key] = [];
            }
            budgetsByMonth[key].push(budget);
        }

        updateBudgetsList(budgetsByMonth);
    } catch (error) {
        console.error('Error loading budgets:', error);
        showNotification('Error loading budgets', 'error');
    }
}

async function updateBudgetsList(budgetsByMonth) {
    const container = document.getElementById('budgetsList');
    container.innerHTML = '';

    for (const [monthYear, budgets] of Object.entries(budgetsByMonth)) {
        const [month, year] = monthYear.split('-');
        const monthName = new Date(year, month - 1).toLocaleString('en-US', { month: 'long', year: 'numeric' });
        
        const monthSection = document.createElement('div');
        monthSection.style.marginBottom = '30px';
        
        const monthTitle = document.createElement('h4');
        monthTitle.textContent = monthName;
        monthSection.appendChild(monthTitle);

        const bugetGrid = document.createElement('div');
        bugetGrid.className = 'budgets-container';

        for (const budget of budgets) {
            try {
                // Get spending for this category
                const expenses = await expenseAPI.getByMonth(parseInt(month), parseInt(year));
                const spending = expenses
                    .filter(e => e.category === budget.category)
                    .reduce((sum, e) => sum + parseFloat(e.amount), 0);

                const percentage = (spending / budget.amount) * 100;
                const progressColor = percentage >= 100 ? 'danger' : percentage >= 80 ? 'warning' : '';

                const budgetItem = document.createElement('div');
                budgetItem.className = 'budget-item';
                budgetItem.innerHTML = `
                    <h4>${budget.category}</h4>
                    <div class="progress-bar">
                        <div class="progress-fill ${progressColor}" style="width: ${Math.min(percentage, 100)}%"></div>
                    </div>
                    <div class="budget-info">
                        <p>Spending: <strong>${formatCurrency(spending)}</strong> / Budget: <strong>${formatCurrency(budget.amount)}</strong></p>
                        <p>Used: <strong>${percentage.toFixed(1)}%</strong></p>
                        ${percentage >= 100 ? '<p style="color: #f56565;">Budget Exceeded!</p>' : ''}
                        ${percentage >= 80 && percentage < 100 ? '<p style="color: #f6ad55;">Approaching Limit</p>' : ''}
                    </div>
                    <button class="btn btn-sm btn-danger" onclick="deleteBudget(${budget.id})">Delete</button>
                `;
                bugetGrid.appendChild(budgetItem);
            } catch (error) {
                console.error('Error calculating budget usage:', error);
            }
        }

        monthSection.appendChild(bugetGrid);
        container.appendChild(monthSection);
    }

    if (container.innerHTML === '') {
        container.innerHTML = '<p class="no-alerts">No budgets set yet</p>';
    }
}

async function deleteBudget(id) {
    if (confirm('Are you sure you want to delete this budget?')) {
        try {
            const result = await budgetAPI.delete(id);
            if (result.success) {
                showNotification('Budget deleted successfully!', 'success');
                await loadAllBudgets();
            } else {
                showNotification('Failed to delete budget', 'error');
            }
        } catch (error) {
            console.error('Error deleting budget:', error);
            showNotification('Error deleting budget', 'error');
        }
    }
}
