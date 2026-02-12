// Chart.js - Chart management utilities

let charts = {};

// =============================================
// Category Pie Chart
// =============================================

function createCategoryChart(elementId, data) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const labels = Object.keys(data);
    const values = Object.values(data);
    
    const colors = [
        'rgba(102, 126, 234, 0.8)',
        'rgba(118, 75, 162, 0.8)',
        'rgba(244, 63, 94, 0.8)',
        'rgba(255, 159, 64, 0.8)',
        'rgba(255, 193, 7, 0.8)',
        'rgba(76, 175, 80, 0.8)',
        'rgba(33, 150, 243, 0.8)'
    ];

    charts[elementId] = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.slice(0, labels.length),
                borderColor: '#fff',
                borderWidth: 2,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        font: {
                            size: 12
                        },
                        padding: 15,
                        boxWidth: 12
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = formatCurrency(context.parsed);
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed / total) * 100).toFixed(1);
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Monthly Bar Chart
// =============================================

function createMonthlyChart(elementId, data) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const labels = Object.keys(data);
    const values = Object.values(data);

    charts[elementId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Monthly Spending',
                data: values,
                backgroundColor: 'rgba(102, 126, 234, 0.8)',
                borderColor: 'rgba(102, 126, 234, 1)',
                borderWidth: 1,
                borderRadius: 4,
                hoverBackgroundColor: 'rgba(102, 126, 234, 1)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            indexAxis: 'x',
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Amount: ' + formatCurrency(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(0);
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Trend Line Chart
// =============================================

function createTrendChart(elementId, data) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const labels = Object.keys(data);
    const values = Object.values(data);

    charts[elementId] = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Spending Trend',
                data: values,
                borderColor: 'rgba(102, 126, 234, 1)',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4,
                pointRadius: 4,
                pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Amount: ' + formatCurrency(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(0);
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Income Sources Chart
// =============================================

function createIncomeSourcesChart(elementId, data) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const labels = Object.keys(data);
    const values = Object.values(data);
    
    const colors = [
        'rgba(76, 175, 80, 0.8)',
        'rgba(33, 150, 243, 0.8)',
        'rgba(255, 193, 7, 0.8)',
        'rgba(255, 87, 34, 0.8)'
    ];

    charts[elementId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Income by Source',
                data: values,
                backgroundColor: colors.slice(0, labels.length),
                borderColor: colors.slice(0, labels.length),
                borderWidth: 1,
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            indexAxis: 'y',
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Amount: ' + formatCurrency(context.parsed.x);
                        }
                    }
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(0);
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Category Comparison Bar Chart
// =============================================

function createCategoryComparisonChart(elementId, spending, budgets) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const categories = Object.keys(spending);
    const spendingValues = Object.values(spending);
    const budgetValues = categories.map(cat => budgets[cat] || 0);

    charts[elementId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: categories,
            datasets: [
                {
                    label: 'Spending',
                    data: spendingValues,
                    backgroundColor: 'rgba(244, 63, 94, 0.8)',
                    borderColor: 'rgba(244, 63, 94, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Budget',
                    data: budgetValues,
                    backgroundColor: 'rgba(76, 175, 80, 0.8)',
                    borderColor: 'rgba(76, 175, 80, 1)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': ' + formatCurrency(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toFixed(0);
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Savings Progress Chart
// =============================================

function createSavingsProgressChart(elementId, goals) {
    const ctx = document.getElementById(elementId);
    if (!ctx) return;

    if (charts[elementId]) {
        charts[elementId].destroy();
    }

    const labels = goals.map(g => g.name);
    const percentages = goals.map(g => (g.progressPercentage > 100 ? 100 : g.progressPercentage));
    
    const colors = goals.map(g => {
        if (g.progressPercentage >= 100) return 'rgba(76, 175, 80, 0.8)';
        if (g.progressPercentage >= 50) return 'rgba(255, 193, 7, 0.8)';
        return 'rgba(244, 63, 94, 0.8)';
    });

    charts[elementId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Progress %',
                data: percentages,
                backgroundColor: colors,
                borderColor: colors.map(c => c.replace('0.8', '1')),
                borderWidth: 1,
                borderRadius: 4
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                x: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.parsed.x.toFixed(1) + '%';
                        }
                    }
                }
            }
        }
    });
}

// =============================================
// Destroy All Charts
// =============================================

function destroyAllCharts() {
    Object.keys(charts).forEach(key => {
        if (charts[key]) {
            charts[key].destroy();
        }
    });
    charts = {};
}
