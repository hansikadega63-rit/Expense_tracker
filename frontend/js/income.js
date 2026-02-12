// Income Management JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Set today's date as default
    document.getElementById('incomeDate').valueAsDate = new Date();

    // Form submission
    document.getElementById('incomeForm').addEventListener('submit', handleAddIncome);

    // Load initial data
    const month = getCurrentMonth();
    const year = getCurrentYear();
    await loadMonthlyIncome(month, year);
});

async function handleAddIncome(e) {
    e.preventDefault();

    try {
        const amount = document.getElementById('incomeAmount').value;
        const source = document.getElementById('source').value;
        const date = document.getElementById('incomeDate').value;
        const notes = document.getElementById('notes').value;

        if (!amount || !source || !date) {
            showNotification('Please fill in all required fields', 'error');
            return;
        }

        const result = await incomeAPI.add(amount, source, date, notes);
        
        if (result.success) {
            showNotification('Income added successfully!', 'success');
            document.getElementById('incomeForm').reset();
            document.getElementById('incomeDate').valueAsDate = new Date();
            
            const month = getCurrentMonth();
            const year = getCurrentYear();
            await loadMonthlyIncome(month, year);
        } else {
            showNotification('Failed to add income', 'error');
        }
    } catch (error) {
        console.error('Error adding income:', error);
        showNotification('Error adding income', 'error');
    }
}

async function loadMonthlyIncome(month, year) {
    try {
        const incomeRecords = await incomeAPI.getByMonth(month, year);
        const totalIncome = incomeRecords.reduce((sum, i) => sum + parseFloat(i.amount), 0);

        document.getElementById('monthlyIncome').textContent = formatCurrency(totalIncome);
        updateIncomeTable(incomeRecords);
    } catch (error) {
        console.error('Error loading income:', error);
        showNotification('Error loading income data', 'error');
    }
}

function updateIncomeTable(incomeRecords) {
    const tbody = document.querySelector('#incomeTable tbody');
    tbody.innerHTML = '';

    incomeRecords.forEach(income => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${formatDate(income.incomeDate)}</td>
            <td><strong>${income.source}</strong></td>
            <td>${formatCurrency(income.amount)}</td>
            <td>${income.notes || '-'}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteIncome(${income.id})">Delete</button>
            </td>
        `;
    });

    if (incomeRecords.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No income recorded this month</td></tr>';
    }
}

async function deleteIncome(id) {
    if (confirm('Are you sure you want to delete this income record?')) {
        try {
            const result = await incomeAPI.delete(id);
            if (result.success) {
                showNotification('Income deleted successfully!', 'success');
                const month = getCurrentMonth();
                const year = getCurrentYear();
                await loadMonthlyIncome(month, year);
            } else {
                showNotification('Failed to delete income', 'error');
            }
        } catch (error) {
            console.error('Error deleting income:', error);
            showNotification('Error deleting income', 'error');
        }
    }
}
