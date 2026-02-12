# Personal Expense Tracker & Budget Planner

## 📋 Project Overview

A comprehensive, full-stack web application for personal expense tracking, budget planning, and financial goal management. Built with a modern tech stack featuring Java servlets for the backend, vanilla JavaScript for the frontend, and MySQL for persistent data storage.

## ✨ Features

### Core Features
- ✅ **Expense Management (CRUD)** - Add, edit, delete, and view expenses with categories
- ✅ **Income Tracking** - Track income from multiple sources
- ✅ **Budget Planning** - Set monthly budgets per category with 80% and 100% alerts
- ✅ **Category Breakdown** - Automated categorization with predefined categories
- ✅ **Monthly Reports** - Comprehensive monthly summaries and analysis
- ✅ **Savings Goals** - Track and visualize savings progress with deadlines

### Bonus Features
- ✅ **Spending Visualization** - Interactive charts (Pie, Bar, Line charts)
- ✅ **Budget Alerts** - Real-time warnings when budget thresholds are reached
- ✅ **Search & Filter** - Filter by date, category, amount range
- ✅ **Expense Insights** - Daily average spending, category rankings
- ✅ **Savings Goal Tracker** - Visual progress bars with remaining amount calculations
- ✅ **Responsive Design** - Works on desktop and mobile devices

## 🛠 Tech Stack

### Frontend
- HTML5
- CSS3 (with modern flexbox & grid)
- Vanilla JavaScript (no frameworks)
- Chart.js for interactive visualizations

### Backend
- Java 11+
- Servlets (HTTP request handlers)
- JDBC for database connectivity
- Maven for build management
- Gson for JSON processing

### Database
- **Primary:** MySQL 8.0+
- **Fallback:** SQLite 3.44+

### Other Tools
- Tomcat 9+ (servlet container)
- Maven 3.6+ (dependency management)

## 📁 Project Structure

```
ExpenseTracker/
├── frontend/
│   ├── index.html                 # Dashboard
│   ├── add_expense.html           # Expense management
│   ├── income.html                # Income tracking
│   ├── budgets.html               # Budget management
│   ├── reports.html               # Monthly reports
│   ├── savings.html               # Savings goals
│   ├── css/
│   │   └── styles.css             # Responsive styling
│   └── js/
│       ├── api.js                 # API communication
│       ├── charts.js              # Chart utilities
│       ├── dashboard.js           # Dashboard logic
│       ├── expense.js             # Expense management
│       ├── income.js              # Income management
│       ├── budget.js              # Budget management
│       ├── reports.js             # Reports logic
│       └── savings.js             # Savings goals logic
├── backend/
│   ├── pom.xml                    # Maven configuration
│   └── src/main/java/com/expensetracker/
│       ├── servlet/               # API endpoints
│       │   ├── ExpenseServlet.java
│       │   ├── BudgetServlet.java
│       │   ├── IncomeServlet.java
│       │   ├── SavingsGoalServlet.java
│       │   └── DashboardServlet.java
│       ├── dao/                   # Data access objects
│       │   ├── ExpenseDAO.java
│       │   ├── BudgetDAO.java
│       │   ├── IncomeDAO.java
│       │   └── SavingsGoalDAO.java
│       ├── model/                 # Data models
│       │   ├── Expense.java
│       │   ├── Budget.java
│       │   ├── Income.java
│       │   ├── RecurringExpense.java
│       │   └── SavingsGoal.java
│       └── util/
│           └── DatabaseConnection.java
├── database/
│   ├── schema.sql                 # Database schema
│   └── seed_data.sql              # Sample data
├── README.md                       # This file
└── SETUP_GUIDE.md                 # Detailed setup instructions
```

## 🚀 Quick Start Guide

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- MySQL 8.0+ or SQLite
- Tomcat 9+ (optional, for running WAR file)
- Git

### Installation Steps

#### 1. Database Setup

**For MySQL:**
```bash
# Open MySQL command line
mysql -u root -p

# Create database
CREATE DATABASE expense_tracker;
USE expense_tracker;

# Import schema
SOURCE database/schema.sql;

# Import sample data
SOURCE database/seed_data.sql;
```

**For SQLite** (automatic):
- SQLite database will be created automatically on first run at `./database/expense_tracker.db`

#### 2. Configure Database Connection

Edit `backend/src/main/java/com/expensetracker/util/DatabaseConnection.java`:

**For MySQL:**
```java
private static final String DB_TYPE = "mysql";
private static final String MYSQL_HOST = "localhost";
private static final String MYSQL_USER = "root";
private static final String MYSQL_PASSWORD = "your_password"; // Change this
```

**For SQLite:**
```java
private static final String DB_TYPE = "sqlite";
```

#### 3. Build Backend

```bash
cd backend
mvn clean install
mvn tomcat7:run
```

The backend will be running at: `http://localhost:8080/ExpenseTracker`

#### 4. Run Frontend

- Copy frontend files to your web server, OR
- Open `frontend/index.html` directly in a browser (for SQLite only, if serving from same origin)

## 📊 Database Schema

### Tables

#### expenses
- id (INT PRIMARY KEY)
- amount (DECIMAL)
- category (VARCHAR)
- expense_date (DATE)
- description (VARCHAR)
- created_at, updated_at (TIMESTAMP)

#### budgets
- id (INT PRIMARY KEY)
- category (VARCHAR)
- amount (DECIMAL)
- month, year (INT)
- alert_threshold (DECIMAL - default 0.80)
- created_at, updated_at (TIMESTAMP)

#### income
- id (INT PRIMARY KEY)
- amount (DECIMAL)
- source (VARCHAR)
- income_date (DATE)
- notes (VARCHAR)
- created_at, updated_at (TIMESTAMP)

#### savings_goals
- id (INT PRIMARY KEY)
- name (VARCHAR)
- target_amount, current_amount (DECIMAL)
- deadline (DATE)
- priority (VARCHAR)
- description (VARCHAR)
- created_at, updated_at (TIMESTAMP)

#### recurring_expenses
- id (INT PRIMARY KEY)
- amount (DECIMAL)
- category, description (VARCHAR)
- start_date, end_date (DATE)
- frequency (VARCHAR)
- next_due_date, last_processed_date (DATE)
- is_active (BOOLEAN)
- created_at, updated_at (TIMESTAMP)

## 🔌 API Endpoints

### Expense Endpoints
```
GET  /api/expense?action=getAll
GET  /api/expense?action=getByCategory&category=Food
GET  /api/expense?action=getByMonth&month=2&year=2026
GET  /api/expense?action=getCategoryBreakdown&month=2&year=2026
GET  /api/expense?action=getMonthlySummary&year=2026
GET  /api/expense?action=getDailyAverage&month=2&year=2026
POST /api/expense?action=add (amount, category, date, description)
POST /api/expense?action=update (id, amount, category, date, description)
POST /api/expense?action=delete (id)
```

### Budget Endpoints
```
GET  /api/budget?action=getAll
GET  /api/budget?action=getByMonth&month=2&year=2026
GET  /api/budget?action=getAlerts&month=2&year=2026
POST /api/budget?action=add (category, amount, month, year)
POST /api/budget?action=update (id, amount)
POST /api/budget?action=delete (id)
```

### Income Endpoints
```
GET  /api/income?action=getAll
GET  /api/income?action=getByMonth&month=2&year=2026
GET  /api/income?action=getTotalIncome
GET  /api/income?action=getMonthlyTotal&month=2&year=2026
GET  /api/income?action=getSourcesSummary&month=2&year=2026
POST /api/income?action=add (amount, source, date, notes)
POST /api/income?action=update (id, amount, source, date, notes)
POST /api/income?action=delete (id)
```

### Savings Goal Endpoints
```
GET  /api/savingsgoal?action=getAll
GET  /api/savingsgoal?action=getStatus
POST /api/savingsgoal?action=add (name, targetAmount, currentAmount, deadline, priority, description)
POST /api/savingsgoal?action=update (id, name, targetAmount, currentAmount, deadline, priority, description)
POST /api/savingsgoal?action=updateProgress (id, currentAmount)
POST /api/savingsgoal?action=delete (id)
```

### Dashboard Endpoints
```
GET /api/dashboard?action=getDashboardData
GET /api/dashboard?action=getMonthlyReport&month=2&year=2026
GET /api/dashboard?action=getExpenseInsights
```

## 🎯 Categories

- Food
- Transport
- Shopping
- Rent
- Bills
- Entertainment
- Other

## 💡 Usage Examples

### Adding an Expense
1. Go to "Add Expense" page
2. Enter amount, select category, pick date
3. Add optional description
4. Click "Add Expense"

### Setting a Budget
1. Go to "Budgets" page
2. Select category, enter budget amount
3. Choose month and year
4. Click "Set Budget"

### Tracking Savings Goals
1. Go to "Savings Goals" page
2. Enter goal name, target amount, deadline
3. Set priority and add description
4. Click "Add Savings Goal"

### Viewing Reports
1. Go to "Reports" page
2. Select month and year
3. View charts and summaries
4. Export or print if needed

## ⚠️ Budget Alerts

- **80% Threshold:** Yellow warning displayed when 80% of budget is spent
- **100% Limit:** Red alert shown when budget is exceeded
- Alerts automatically update on dashboard

## 📊 Charts Included

1. **Pie Chart** - Expense distribution by category
2. **Bar Chart** - Monthly spending comparison
3. **Line Chart** - Spending trends over time
4. **Income Sources Chart** - Income breakdown by source
5. **Savings Progress Chart** - Goal achievement visualization

## 🔧 Configuration

### Change Database
Edit `DatabaseConnection.java`:
```java
private static final String DB_TYPE = "mysql"; // or "sqlite"
```

### Change Port
In `pom.xml`:
```xml
<port>8080</port>
```

### Adjust Budget Alert Threshold
In `BudgetDAO.java`, modify `alert_threshold` calculation

## 🧪 Testing

### Sample Data
Run `seed_data.sql` to populate with 30+ sample transactions

### Manual Test Cases

1. **Add Expense**
   - Input valid amount, category, date
   - Verify it appears in transactions list

2. **Budget Alert**
   - Set $100 budget for Food
   - Add expenses totaling $85+
   - Verify warning appears

3. **Monthly Report**
   - Switch months
   - Verify charts update
   - Check category breakdown accuracy

4. **Savings Goals**
   - Create goal with deadline
   - Update progress
   - Verify percentage calculation

## 🐛 Troubleshooting

### Database Connection Error
- Check MySQL is running: `mysql -u root -p`
- Verify username/password in DatabaseConnection.java
- Check database exists: `SHOW DATABASES;`

### Servlet Not Found (404)
- Ensure WAR is deployed correctly
- Check web.xml is in WEB-INF folder
- Verify servlet mapping URLs match

### Charts Not Loading
- Check browser console for JavaScript errors
- Verify Chart.js CDN is accessible
- Check API responses in Network tab

### CORS Issues
- Add CORS headers in servlet responses if needed
- Check frontend is accessing correct API URL

## 📝 License

This project is open source and available under the MIT License.

## 👨‍💻 Development Notes

### Technology Choices
- **Vanilla JS:** No framework overhead, lightweight
- **Servlets:** Simple, proven, no heavy frameworks
- **JDBC:** Direct database control, no ORM complexity
- **Chart.js:** Lightweight, responsive charts

### Performance Optimizations
- Indexed database columns for faster queries
- Pagination ready (can be added to DAOs)
- Client-side filtering to reduce network traffic
- Async/await for responsive UI

## 📞 Support

For issues, feature requests, or questions:
1. Check the troubleshooting section
2. Review browser console for errors
3. Check server logs for backend errors
4. Verify database is running and accessible

---

**Version:** 1.0.0  
**Last Updated:** February 2026  
**Status:** Production Ready ✅
