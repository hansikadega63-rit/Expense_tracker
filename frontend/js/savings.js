// Savings Goals Management JavaScript

document.addEventListener('DOMContentLoaded', async () => {
    // Form submission
    document.getElementById('savingsGoalForm').addEventListener('submit', handleAddGoal);

    // Load all goals
    await loadAllGoals();
});

async function handleAddGoal(e) {
    e.preventDefault();

    try {
        const name = document.getElementById('goalName').value;
        const targetAmount = document.getElementById('targetAmount').value;
        const currentAmount = document.getElementById('currentAmount').value;
        const deadline = document.getElementById('deadline').value;
        const priority = document.getElementById('priority').value;
        const description = document.getElementById('description').value;

        if (!name || !targetAmount || !deadline || !priority) {
            showNotification('Please fill in all required fields', 'error');
            return;
        }

        const result = await savingsGoalAPI.add(name, targetAmount, currentAmount, deadline, priority, description);
        
        if (result.success) {
            showNotification('Savings goal created successfully!', 'success');
            document.getElementById('savingsGoalForm').reset();
            await loadAllGoals();
        } else {
            showNotification('Failed to create savings goal', 'error');
        }
    } catch (error) {
        console.error('Error adding goal:', error);
        showNotification('Error creating savings goal', 'error');
    }
}

async function loadAllGoals() {
    try {
        const goals = await savingsGoalAPI.getAll();
        updateGoalsList(goals);
    } catch (error) {
        console.error('Error loading goals:', error);
        showNotification('Error loading savings goals', 'error');
    }
}

function updateGoalsList(goals) {
    const container = document.getElementById('savingsGoalsList');
    container.innerHTML = '';

    if (goals.length === 0) {
        container.innerHTML = '<p class="no-alerts">No savings goals yet. Create your first goal above!</p>';
        return;
    }

    goals.forEach(goal => {
        const progressPercent = (goal.currentAmount / goal.targetAmount * 100).toFixed(1);
        const remainingAmount = Math.max(0, goal.targetAmount - goal.currentAmount);
        const isCompleted = progressPercent >= 100;

        const priorityColor = {
            'HIGH': '#f56565',
            'MEDIUM': '#f6ad55',
            'LOW': '#48bb78'
        }[goal.priority] || '#667eea';

        const goalCard = document.createElement('div');
        goalCard.className = 'goal-card';
        goalCard.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                <h4 style="margin: 0;">${goal.name}</h4>
                <span style="background-color: ${priorityColor}; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600;">
                    ${goal.priority}
                </span>
            </div>
            
            ${goal.description ? `<p style="margin: 10px 0; color: #718096; font-size: 14px;">${goal.description}</p>` : ''}
            
            <div class="goal-progress">
                <div class="progress-bar">
                    <div class="progress-fill ${isCompleted ? '' : progressPercent >= 50 ? 'warning' : ''}" 
                         style="width: ${Math.min(progressPercent, 100)}%; background-color: ${isCompleted ? '#48bb78' : (progressPercent >= 50 ? '#f6ad55' : '#667eea')}"></div>
                </div>
                <p style="text-align: center; margin-top: 8px; font-weight: 600;">${progressPercent}% Complete</p>
            </div>
            
            <div class="goal-stats">
                <div class="goal-stat">
                    <label>Current</label>
                    <div class="value">${formatCurrency(goal.currentAmount)}</div>
                </div>
                <div class="goal-stat">
                    <label>Target</label>
                    <div class="value">${formatCurrency(goal.targetAmount)}</div>
                </div>
                <div class="goal-stat">
                    <label>Remaining</label>
                    <div class="value">${formatCurrency(remainingAmount)}</div>
                </div>
                <div class="goal-stat">
                    <label>Deadline</label>
                    <div class="value">${formatDate(goal.deadline)}</div>
                </div>
            </div>
            
            <div style="display: flex; gap: 10px; margin-top: 15px;">
                <button class="btn btn-sm btn-primary" onclick="openUpdateDialog(${goal.id})">Update</button>
                <button class="btn btn-sm btn-danger" onclick="deleteGoal(${goal.id})">Delete</button>
            </div>
        `;
        container.appendChild(goalCard);
    });
}

async function deleteGoal(id) {
    if (confirm('Are you sure you want to delete this savings goal?')) {
        try {
            const result = await savingsGoalAPI.delete(id);
            if (result.success) {
                showNotification('Savings goal deleted successfully!', 'success');
                await loadAllGoals();
            } else {
                showNotification('Failed to delete savings goal', 'error');
            }
        } catch (error) {
            console.error('Error deleting goal:', error);
            showNotification('Error deleting savings goal', 'error');
        }
    }
}

async function openUpdateDialog(goalId) {
    // Simple prompt for updating progress
    const newAmount = prompt('Enter new current amount:');
    if (newAmount !== null) {
        try {
            const result = await savingsGoalAPI.updateProgress(goalId, parseFloat(newAmount));
            if (result.success) {
                showNotification('Savings goal updated successfully!', 'success');
                await loadAllGoals();
            } else {
                showNotification('Failed to update savings goal', 'error');
            }
        } catch (error) {
            console.error('Error updating goal:', error);
            showNotification('Error updating savings goal', 'error');
        }
    }
}
