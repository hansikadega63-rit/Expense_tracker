// Expense Management JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Set today's date as default
    document.getElementById('date').valueAsDate = new Date();

    // Form submission
    document.getElementById('expenseForm').addEventListener('submit', handleAddExpense);

    // Load all expenses
    await loadAllExpenses();
});

async function handleAddExpense(e) {
    e.preventDefault();

    try {
        const amount = document.getElementById('amount').value;
        const category = document.getElementById('category').value;
        const date = document.getElementById('date').value;
        const description = document.getElementById('description').value;

        if (!amount || !category || !date) {
            showNotification('Please fill in all required fields', 'error');
            return;
        }

        const result = await expenseAPI.add(amount, category, date, description);
        
        if (result.success) {
            showNotification('Expense added successfully!', 'success');
            document.getElementById('expenseForm').reset();
            document.getElementById('date').valueAsDate = new Date();
            await loadAllExpenses();
        } else {
            showNotification('Failed to add expense', 'error');
        }
    } catch (error) {
        console.error('Error adding expense:', error);
        showNotification('Error adding expense', 'error');
    }
}

async function loadAllExpenses() {
    try {
        const expenses = await expenseAPI.getAll();
        updateExpensesTable(expenses);
    } catch (error) {
        console.error('Error loading expenses:', error);
        showNotification('Error loading expenses', 'error');
    }
}

function updateExpensesTable(expenses) {
    const tbody = document.querySelector('#expensesTable tbody');
    tbody.innerHTML = '';

    expenses.forEach(expense => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${formatDate(expense.expenseDate)}</td>
            <td><strong>${expense.category}</strong></td>
            <td>${expense.description || '-'}</td>
            <td>${formatCurrency(expense.amount)}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteExpense(${expense.id})">Delete</button>
            </td>
        `;
    });

    if (expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No expenses recorded</td></tr>';
    }
}

async function deleteExpense(id) {
    if (confirm('Are you sure you want to delete this expense?')) {
        try {
            const result = await expenseAPI.delete(id);
            if (result.success) {
                showNotification('Expense deleted successfully!', 'success');
                await loadAllExpenses();
            } else {
                showNotification('Failed to delete expense', 'error');
            }
        } catch (error) {
            console.error('Error deleting expense:', error);
            showNotification('Error deleting expense', 'error');
        }
    }
}
