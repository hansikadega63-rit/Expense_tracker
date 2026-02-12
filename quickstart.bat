@echo off
REM Expense Tracker - Quick Start for Windows
REM This script helps setup and run the Expense Tracker application

setlocal enabledelayedexpansion

echo.
echo ====================================================
echo  Personal Expense Tracker & Budget Planner
echo  Quick Start Script
echo ====================================================
echo.

REM Check Java installation
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or higher and add it to PATH
    pause
    exit /b 1
)
echo [OK] Java is installed

REM Check Maven installation
echo Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6+ and add it to PATH
    pause
    exit /b 1
)
echo [OK] Maven is installed

REM Menu
:MENU
echo.
echo ====================================================
echo  What would you like to do?
echo ====================================================
echo 1. Build the project (Maven clean install)
echo 2. Run the application (Tomcat server)
echo 3. Build and Run (both steps)
echo 4. View Project Structure
echo 5. Exit
echo.

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto BUILD
if "%choice%"=="2" goto RUN
if "%choice%"=="3" goto BUILDRUN
if "%choice%"=="4" goto STRUCTURE
if "%choice%"=="5" goto EXIT

echo Invalid choice. Please try again.
goto MENU

:BUILD
echo.
echo ====================================================
echo  Building Project with Maven
echo ====================================================
cd backend
echo Running: mvn clean install
call mvn clean install
if errorlevel 1 (
    echo Build failed!
    pause
    cd ..
    goto MENU
)
echo Build completed successfully!
cd ..
pause
goto MENU

:RUN
echo.
echo ====================================================
echo  Running Application on Tomcat
echo ====================================================
cd backend
echo Running: mvn tomcat7:run
echo.
echo The application will be available at:
echo   http://localhost:8080/ExpenseTracker/
echo.
echo Press CTRL+C to stop the server
echo.
call mvn tomcat7:run
cd ..
goto MENU

:BUILDRUN
echo.
echo ====================================================
echo  Building and Running Application
echo ====================================================
cd backend
echo Step 1: Building...
call mvn clean install
if errorlevel 1 (
    echo Build failed!
    pause
    cd ..
    goto MENU
)
echo.
echo Step 2: Running...
echo The application will be available at:
echo   http://localhost:8080/ExpenseTracker/
echo.
echo Press CTRL+C to stop the server
echo.
call mvn tomcat7:run
cd ..
goto MENU

:STRUCTURE
echo.
echo ====================================================
echo  Project Structure
echo ====================================================
echo.
echo ExpenseTracker/
echo  ├── frontend/                  (HTML, CSS, JavaScript)
echo  │   ├── index.html             (Dashboard)
echo  │   ├── add_expense.html       (Expense Management)
echo  │   ├── income.html            (Income Tracking)
echo  │   ├── budgets.html           (Budget Planning)
echo  │   ├── reports.html           (Monthly Reports)
echo  │   ├── savings.html           (Savings Goals)
echo  │   ├── css/styles.css         (Responsive Styling)
echo  │   └── js/                    (JavaScript Modules)
echo  │
echo  ├── backend/                   (Java Backend)
echo  │   ├── pom.xml                (Maven Configuration)
echo  │   └── src/main/java/...      (Source Code)
echo  │       ├── servlet/           (API Endpoints)
echo  │       ├── dao/               (Database Access)
echo  │       ├── model/             (Data Models)
echo  │       └── util/              (Utilities)
echo  │
echo  ├── database/                  (Database Scripts)
echo  │   ├── schema.sql             (MySQL Schema)
echo  │   ├── schema_sqlite.sql      (SQLite Schema)
echo  │   └── seed_data.sql          (Sample Data)
echo  │
echo  ├── README.md                  (Project Documentation)
echo  └── SETUP_GUIDE.txt            (Setup Instructions)
echo.
echo ====================================================
echo  Features
echo ====================================================
echo.
echo CORE FEATURES:
echo  - Expense Management (CRUD operations)
echo  - Income Tracking
echo  - Budget Planning with Alerts (80% & 100%)
echo  - Monthly Reports and Analysis
echo  - Savings Goals Tracking
echo.
echo BONUS FEATURES:
echo  - Interactive Charts (Pie, Bar, Line)
echo  - Responsive Design (Desktop & Mobile)
echo  - Search and Filter Capabilities
echo  - Expense Insights and Analytics
echo  - Category Breakdown Analysis
echo.
pause
goto MENU

:EXIT
echo.
echo Thank you for using Expense Tracker!
echo.
exit /b 0
