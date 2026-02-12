package com.expensetracker.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Database Initializer - Creates tables and initializes database on first run
 */
public class DatabaseInitializer {
    
    public static void initialize() {
        try {
            // Ensure database directory exists
            File dbDir = new File("database");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            // Check if database already exists
            String dbPath = "database/expense_tracker.db";
            boolean isNewDb = !new File(dbPath).exists();
            
            // Get connection (this creates the file if it doesn't exist)
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                if (isNewDb) {
                    System.out.println("Initializing new SQLite database...");
                    
                    // Create tables
                    createTables(stmt);
                    
                    // Seed sample data
                    seedData(stmt);
                    
                    System.out.println("Database initialized successfully!");
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTables(Statement stmt) throws Exception {
        // Drop existing tables
        String[] dropStatements = {
            "DROP TABLE IF EXISTS savings_goals",
            "DROP TABLE IF EXISTS recurring_expenses",
            "DROP TABLE IF EXISTS income",
            "DROP TABLE IF EXISTS budgets",
            "DROP TABLE IF EXISTS expenses"
        };
        
        for (String sql : dropStatements) {
            try {
                stmt.execute(sql);
            } catch (Exception e) {
                // Table might not exist, that's ok
            }
        }
        
        // Expenses Table
        stmt.execute("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "category TEXT NOT NULL," +
                "expense_date TEXT NOT NULL," +
                "description TEXT," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        stmt.execute("CREATE INDEX idx_category ON expenses(category)");
        stmt.execute("CREATE INDEX idx_expense_date ON expenses(expense_date)");
        stmt.execute("CREATE INDEX idx_created_at ON expenses(created_at)");
        
        // Budgets Table
        stmt.execute("CREATE TABLE budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "month INTEGER NOT NULL," +
                "year INTEGER NOT NULL," +
                "alert_threshold REAL DEFAULT 0.80," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        stmt.execute("CREATE UNIQUE INDEX unique_budget ON budgets(category, month, year)");
        stmt.execute("CREATE INDEX idx_budget_category ON budgets(category)");
        stmt.execute("CREATE INDEX idx_budget_month_year ON budgets(month, year)");
        
        // Income Table
        stmt.execute("CREATE TABLE income (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "source TEXT NOT NULL," +
                "income_date TEXT NOT NULL," +
                "notes TEXT," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        stmt.execute("CREATE INDEX idx_income_date ON income(income_date)");
        stmt.execute("CREATE INDEX idx_source ON income(source)");
        
        // Recurring Expenses Table
        stmt.execute("CREATE TABLE recurring_expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "category TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "start_date TEXT NOT NULL," +
                "end_date TEXT," +
                "frequency TEXT NOT NULL," +
                "next_due_date TEXT NOT NULL," +
                "last_processed_date TEXT," +
                "is_active BOOLEAN DEFAULT 1," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        stmt.execute("CREATE INDEX idx_recurring_frequency ON recurring_expenses(frequency)");
        stmt.execute("CREATE INDEX idx_recurring_next_due ON recurring_expenses(next_due_date)");
        stmt.execute("CREATE INDEX idx_recurring_active ON recurring_expenses(is_active)");
        
        // Savings Goals Table
        stmt.execute("CREATE TABLE savings_goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "target_amount REAL NOT NULL," +
                "current_amount REAL DEFAULT 0," +
                "deadline DATE," +
                "priority TEXT DEFAULT 'MEDIUM'," +
                "description TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
        
        stmt.execute("CREATE INDEX idx_savings_priority ON savings_goals(priority)");
    }
    
    private static void seedData(Statement stmt) throws Exception {
        System.out.println("Seeding sample data...");
        
        // Insert Sample Expenses (Current Month: February 2026)
        // Note: created_at and updated_at will use CURRENT_TIMESTAMP automatically
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(45.50, 'Food', '2026-02-01', 'Grocery shopping at Walmart')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(12.99, 'Entertainment', '2026-02-02', 'Movie ticket')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(85.00, 'Transport', '2026-02-03', 'Gas refill')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(150.00, 'Rent', '2026-02-01', 'Monthly rent payment')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(55.00, 'Bills', '2026-02-04', 'Internet bill')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(30.00, 'Food', '2026-02-05', 'Restaurant dinner')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(120.00, 'Shopping', '2026-02-06', 'New shoes')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(25.00, 'Entertainment', '2026-02-07', 'Game purchase')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(35.50, 'Food', '2026-02-08', 'Lunch with friends')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(60.00, 'Transport', '2026-02-09', 'Uber rides')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(45.00, 'Bills', '2026-02-10', 'Electricity bill')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(18.99, 'Food', '2026-02-11', 'Coffee and snacks')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(200.00, 'Shopping', '2026-02-12', 'Winter jacket')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(40.00, 'Entertainment', '2026-02-13', 'Concert tickets')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(55.00, 'Food', '2026-02-14', 'Valentine dinner')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(80.00, 'Transport', '2026-02-15', 'Car maintenance')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(35.00, 'Other', '2026-02-16', 'Haircut')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(90.00, 'Shopping', '2026-02-17', 'Clothing items')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(22.50, 'Food', '2026-02-18', 'Groceries')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(50.00, 'Bills', '2026-02-19', 'Phone bill')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(15.00, 'Food', '2026-02-20', 'Fast food')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(70.00, 'Transport', '2026-02-21', 'Public transport pass')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(45.00, 'Entertainment', '2026-02-22', 'Streaming subscription')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(65.00, 'Shopping', '2026-02-23', 'Accessories')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(28.00, 'Food', '2026-02-24', 'Restaurant lunch')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(100.00, 'Rent', '2026-02-25', 'Utilities')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(35.00, 'Entertainment', '2026-02-26', 'Books')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(52.00, 'Food', '2026-02-27', 'Dinner out')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(75.00, 'Bills', '2026-02-28', 'Insurance')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(40.00, 'Shopping', '2026-02-28', 'Electronics')");
        
        // Insert January expenses
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(50.00, 'Food', '2026-01-05', 'Grocery shopping')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(100.00, 'Transport', '2026-01-10', 'Gas')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(150.00, 'Rent', '2026-01-01', 'Monthly rent')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(45.00, 'Bills', '2026-01-15', 'Internet')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(35.00, 'Food', '2026-01-20', 'Restaurant')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(120.00, 'Shopping', '2026-01-25', 'Clothing')");
        
        // Insert December expenses
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(60.00, 'Food', '2025-12-05', 'Holiday groceries')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(80.00, 'Entertainment', '2025-12-10', 'Holiday party')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(150.00, 'Rent', '2025-12-01', 'Monthly rent')");
        stmt.execute("INSERT INTO expenses (amount, category, expense_date, description) VALUES " +
                "(70.00, 'Shopping', '2025-12-20', 'Gifts')");
        
        // Insert Sample Income (Current Month)
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(3000.00, 'Salary', '2026-02-01', 'Monthly salary from employer')");
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(500.00, 'Freelance', '2026-02-15', 'Freelance project payment')");
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(150.00, 'Bonus', '2026-02-28', 'Performance bonus')");
        
        // Insert January Income
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(3000.00, 'Salary', '2026-01-01', 'Monthly salary')");
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(300.00, 'Freelance', '2026-01-20', 'Side project')");
        
        // Insert December Income
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(3000.00, 'Salary', '2025-12-01', 'Monthly salary')");
        stmt.execute("INSERT INTO income (amount, source, income_date, notes) VALUES " +
                "(500.00, 'Bonus', '2025-12-15', 'Year-end bonus')");
        
        // Insert Sample Budgets (February 2026)
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Food', 300.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Transport', 200.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Shopping', 400.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Entertainment', 150.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Bills', 200.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Rent', 1000.00, 2, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Other', 100.00, 2, 2026, 0.80)");
        
        // Insert January Budgets
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Food', 300.00, 1, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Transport', 200.00, 1, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Shopping', 400.00, 1, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Entertainment', 150.00, 1, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Bills', 200.00, 1, 2026, 0.80)");
        stmt.execute("INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES " +
                "('Rent', 1000.00, 1, 2026, 0.80)");
        
        // Insert Sample Recurring Expenses
        stmt.execute("INSERT INTO recurring_expenses (amount, category, description, start_date, frequency, next_due_date, is_active) VALUES " +
                "(1000.00, 'Rent', 'Monthly rent payment', '2026-01-01', 'MONTHLY', '2026-03-01', 1)");
        stmt.execute("INSERT INTO recurring_expenses (amount, category, description, start_date, frequency, next_due_date, is_active) VALUES " +
                "(50.00, 'Bills', 'Internet subscription', '2026-01-01', 'MONTHLY', '2026-03-01', 1)");
        stmt.execute("INSERT INTO recurring_expenses (amount, category, description, start_date, frequency, next_due_date, is_active) VALUES " +
                "(14.99, 'Entertainment', 'Netflix subscription', '2026-02-01', 'MONTHLY', '2026-03-01', 1)");
        stmt.execute("INSERT INTO recurring_expenses (amount, category, description, start_date, frequency, next_due_date, is_active) VALUES " +
                "(9.99, 'Entertainment', 'Spotify subscription', '2026-02-01', 'MONTHLY', '2026-03-01', 1)");
        
        System.out.println("Sample data seeded successfully!");
    }
}
