// API Configuration
const API_BASE_URL = '/ExpenseTracker/api';

// ===============================
// API Helper Functions
// ===============================

async function apiCall(endpoint, action, data = null, method = 'GET') {
    try {
        let url = `${API_BASE_URL}/${endpoint}?action=${action}`;
        
        if (method === 'GET' && data) {
            const params = new URLSearchParams(data);
            url += '&' + params.toString();
        }

        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        if (method === 'POST' && data) {
            const params = new URLSearchParams();
            params.append('action', action);
            Object.keys(data).forEach(key => {
                params.append(key, data[key]);
            });
            options.body = params.toString();
            options.headers['Content-Type'] = 'application/x-www-form-urlencoded';
        }

        const response = await fetch(url, options);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ===============================
// Expense API Calls
// ===============================

const expenseAPI = {
    add: async (amount, category, date, description) => {
        return apiCall('expense', 'add', {
            amount, category, date, description
        }, 'POST');
    },
    
    getAll: async () => {
        return apiCall('expense', 'getAll');
    },
    
    getByCategory: async (category) => {
        return apiCall('expense', 'getByCategory', { category });
    },
    
    getByMonth: async (month, year) => {
        return apiCall('expense', 'getByMonth', { month, year });
    },
    
    getCategoryBreakdown: async (month, year) => {
        return apiCall('expense', 'getCategoryBreakdown', { month, year });
    },
    
    getMonthlySummary: async (year) => {
        return apiCall('expense', 'getMonthlySummary', { year });
    },
    
    getTotalExpenses: async () => {
        return apiCall('expense', 'getTotalExpenses');
    },
    
    getDailyAverage: async (month, year) => {
        return apiCall('expense', 'getDailyAverage', { month, year });
    },
    
    update: async (id, amount, category, date, description) => {
        return apiCall('expense', 'update', {
            id, amount, category, date, description
        }, 'POST');
    },
    
    delete: async (id) => {
        return apiCall('expense', 'delete', { id }, 'POST');
    }
};

// ===============================
// Budget API Calls
// ===============================

const budgetAPI = {
    add: async (category, amount, month, year) => {
        return apiCall('budget', 'add', {
            category, amount, month, year
        }, 'POST');
    },
    
    getAll: async () => {
        return apiCall('budget', 'getAll');
    },
    
    getByMonth: async (month, year) => {
        return apiCall('budget', 'getByMonth', { month, year });
    },
    
    getAlerts: async (month, year) => {
        return apiCall('budget', 'getAlerts', { month, year });
    },
    
    update: async (id, amount) => {
        return apiCall('budget', 'update', { id, amount }, 'POST');
    },
    
    delete: async (id) => {
        return apiCall('budget', 'delete', { id }, 'POST');
    }
};

// ===============================
// Income API Calls
// ===============================

const incomeAPI = {
    add: async (amount, source, date, notes) => {
        return apiCall('income', 'add', {
            amount, source, date, notes
        }, 'POST');
    },
    
    getAll: async () => {
        return apiCall('income', 'getAll');
    },
    
    getByMonth: async (month, year) => {
        return apiCall('income', 'getByMonth', { month, year });
    },
    
    getTotalIncome: async () => {
        return apiCall('income', 'getTotalIncome');
    },
    
    getMonthlyTotal: async (month, year) => {
        return apiCall('income', 'getMonthlyTotal', { month, year });
    },
    
    getSourcesSummary: async (month, year) => {
        return apiCall('income', 'getSourcesSummary', { month, year });
    },
    
    update: async (id, amount, source, date, notes) => {
        return apiCall('income', 'update', {
            id, amount, source, date, notes
        }, 'POST');
    },
    
    delete: async (id) => {
        return apiCall('income', 'delete', { id }, 'POST');
    }
};

// ===============================
// Savings Goal API Calls
// ===============================

const savingsGoalAPI = {
    add: async (name, targetAmount, currentAmount, deadline, priority, description) => {
        return apiCall('savingsgoal', 'add', {
            name, targetAmount, currentAmount, deadline, priority, description
        }, 'POST');
    },
    
    getAll: async () => {
        return apiCall('savingsgoal', 'getAll');
    },
    
    getStatus: async () => {
        return apiCall('savingsgoal', 'getStatus');
    },
    
    update: async (id, name, targetAmount, currentAmount, deadline, priority, description) => {
        return apiCall('savingsgoal', 'update', {
            id, name, targetAmount, currentAmount, deadline, priority, description
        }, 'POST');
    },
    
    updateProgress: async (id, currentAmount) => {
        return apiCall('savingsgoal', 'updateProgress', {
            id, currentAmount
        }, 'POST');
    },
    
    delete: async (id) => {
        return apiCall('savingsgoal', 'delete', { id }, 'POST');
    }
};

// ===============================
// Dashboard API Calls
// ===============================

const dashboardAPI = {
    getDashboardData: async () => {
        return apiCall('dashboard', 'getDashboardData');
    },
    
    getMonthlyReport: async (month, year) => {
        return apiCall('dashboard', 'getMonthlyReport', { month, year });
    },
    
    getExpenseInsights: async () => {
        return apiCall('dashboard', 'getExpenseInsights');
    }
};

// ===============================
// Utility Functions
// ===============================

function formatCurrency(value) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(value);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getCurrentMonth() {
    return new Date().getMonth() + 1;
}

function getCurrentYear() {
    return new Date().getFullYear();
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background-color: ${type === 'success' ? '#48bb78' : '#f56565'};
        color: white;
        border-radius: 6px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add animation styles
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
