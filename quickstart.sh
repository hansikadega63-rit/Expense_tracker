#!/bin/bash

# Expense Tracker - Quick Start Script for Linux/Mac
# This script helps setup and run the Expense Tracker application

echo ""
echo "===================================================="
echo " Personal Expense Tracker & Budget Planner"
echo " Quick Start Script"
echo "===================================================="
echo ""

# Check Java installation
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher"
    exit 1
fi
echo "[OK] Java is installed"

# Check Maven installation
echo "Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi
echo "[OK] Maven is installed"

# Display menu
show_menu() {
    echo ""
    echo "===================================================="
    echo " What would you like to do?"
    echo "===================================================="
    echo "1. Build the project (Maven clean install)"
    echo "2. Run the application (Tomcat server)"
    echo "3. Build and Run (both steps)"
    echo "4. View Project Structure"
    echo "5. Database Setup Instructions"
    echo "6. Exit"
    echo ""
}

# Build function
build_project() {
    echo ""
    echo "===================================================="
    echo " Building Project with Maven"
    echo "===================================================="
    cd backend
    echo "Running: mvn clean install"
    mvn clean install
    if [ $? -ne 0 ]; then
        echo "Build failed!"
        read -p "Press Enter to continue..."
        cd ..
        return 1
    fi
    echo "Build completed successfully!"
    cd ..
    return 0
}

# Run function
run_project() {
    echo ""
    echo "===================================================="
    echo " Running Application on Tomcat"
    echo "===================================================="
    cd backend
    echo "Running: mvn tomcat7:run"
    echo ""
    echo "The application will be available at:"
    echo "  http://localhost:8080/ExpenseTracker/"
    echo ""
    echo "Press CTRL+C to stop the server"
    echo ""
    mvn tomcat7:run
    cd ..
}

# Structure function
show_structure() {
    echo ""
    echo "===================================================="
    echo " Project Structure"
    echo "===================================================="
    echo ""
    echo "ExpenseTracker/"
    echo " ├── frontend/                  (HTML, CSS, JavaScript)"
    echo " │   ├── index.html             (Dashboard)"
    echo " │   ├── add_expense.html       (Expense Management)"
    echo " │   ├── income.html            (Income Tracking)"
    echo " │   ├── budgets.html           (Budget Planning)"
    echo " │   ├── reports.html           (Monthly Reports)"
    echo " │   ├── savings.html           (Savings Goals)"
    echo " │   ├── css/styles.css         (Responsive Styling)"
    echo " │   └── js/                    (JavaScript Modules)"
    echo " │"
    echo " ├── backend/                   (Java Backend)"
    echo " │   ├── pom.xml                (Maven Configuration)"
    echo " │   └── src/main/java/...      (Source Code)"
    echo " │       ├── servlet/           (API Endpoints)"
    echo " │       ├── dao/               (Database Access)"
    echo " │       ├── model/             (Data Models)"
    echo " │       └── util/              (Utilities)"
    echo " │"
    echo " ├── database/                  (Database Scripts)"
    echo " │   ├── schema.sql             (MySQL Schema)"
    echo " │   ├── schema_sqlite.sql      (SQLite Schema)"
    echo " │   └── seed_data.sql          (Sample Data)"
    echo " │"
    echo " ├── README.md                  (Project Documentation)"
    echo " └── SETUP_GUIDE.txt            (Setup Instructions)"
    echo ""
    echo "===================================================="
    echo " Features"
    echo "===================================================="
    echo ""
    echo "CORE FEATURES:"
    echo " - Expense Management (CRUD operations)"
    echo " - Income Tracking"
    echo " - Budget Planning with Alerts (80% & 100%)"
    echo " - Monthly Reports and Analysis"
    echo " - Savings Goals Tracking"
    echo ""
    echo "BONUS FEATURES:"
    echo " - Interactive Charts (Pie, Bar, Line)"
    echo " - Responsive Design (Desktop & Mobile)"
    echo " - Search and Filter Capabilities"
    echo " - Expense Insights and Analytics"
    echo " - Category Breakdown Analysis"
    echo ""
    read -p "Press Enter to continue..."
}

# Database setup function
show_database_setup() {
    echo ""
    echo "===================================================="
    echo " Database Setup Instructions"
    echo "===================================================="
    echo ""
    echo "FOR MYSQL:"
    echo "--------"
    echo "1. Install MySQL Server (if not already installed)"
    echo "   macOS: brew install mysql"
    echo "   Linux: sudo apt-get install mysql-server"
    echo ""
    echo "2. Start MySQL:"
    echo "   macOS: brew services start mysql"
    echo "   Linux: sudo systemctl start mysql"
    echo ""
    echo "3. Login to MySQL:"
    echo "   mysql -u root -p"
    echo ""
    echo "4. Create database:"
    echo "   CREATE DATABASE expense_tracker;"
    echo "   USE expense_tracker;"
    echo "   SOURCE database/schema.sql;"
    echo "   SOURCE database/seed_data.sql;"
    echo "   EXIT;"
    echo ""
    echo "5. Update database configuration:"
    echo "   Edit: backend/src/main/java/com/expensetracker/util/DatabaseConnection.java"
    echo "   Set: DB_TYPE = \"mysql\""
    echo "   Set: MYSQL_USER = \"root\""
    echo "   Set: MYSQL_PASSWORD = \"your_password\""
    echo ""
    echo "FOR SQLITE:"
    echo "---------"
    echo "1. SQLite will be created automatically"
    echo ""
    echo "2. Update database configuration:"
    echo "   Edit: backend/src/main/java/com/expensetracker/util/DatabaseConnection.java"
    echo "   Set: DB_TYPE = \"sqlite\""
    echo ""
    read -p "Press Enter to continue..."
}

# Main loop
while true; do
    show_menu
    read -p "Enter your choice (1-6): " choice
    
    case $choice in
        1)
            build_project
            ;;
        2)
            run_project
            ;;
        3)
            build_project
            if [ $? -eq 0 ]; then
                run_project
            fi
            ;;
        4)
            show_structure
            ;;
        5)
            show_database_setup
            ;;
        6)
            echo ""
            echo "Thank you for using Expense Tracker!"
            echo ""
            exit 0
            ;;
        *)
            echo "Invalid choice. Please try again."
            ;;
    esac
done
