package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Expense Data Access Object
 */
public class ExpenseDAO {
    
    /**
     * Parse a date string from SQLite (can be TEXT format 'YYYY-MM-DD' or timestamp)
     */
    private static java.util.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        try {
            // Try format: yyyy-MM-dd
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (Exception e1) {
            try {
                // Try format: yyyy-MM-dd HH:mm:ss
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(dateStr);
            } catch (Exception e2) {
                System.err.println("Error parsing date: " + dateStr);
                return null;
            }
        }
    }
    
    /**
     * Add a new expense
     */
    public static int addExpense(Expense expense) throws SQLException {
        String sql = "INSERT INTO expenses (amount, category, expense_date, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setString(2, expense.getCategory());
            
            // Convert java.util.Date to TEXT format (YYYY-MM-DD)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(expense.getExpenseDate()));
            
            pstmt.setString(4, expense.getDescription());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    /**
     * Get all expenses
     */
    public static List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, expense_date, description FROM expenses ORDER BY expense_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                String dateStr = rs.getString("expense_date");
                java.util.Date date = parseDate(dateStr);
                if (date != null) {
                    expense.setExpenseDate(date);
                }
                expense.setDescription(rs.getString("description"));
                expenses.add(expense);
            }
        }
        return expenses;
    }
    
    /**
     * Get expenses by category
     */
    public static List<Expense> getExpensesByCategory(String category) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, expense_date, description FROM expenses WHERE category = ? ORDER BY expense_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                String dateStr = rs.getString("expense_date");
                java.util.Date date = parseDate(dateStr);
                if (date != null) {
                    expense.setExpenseDate(date);
                }
                expense.setDescription(rs.getString("description"));
                expenses.add(expense);
            }
        }
        return expenses;
    }
    
    /**
     * Get expenses by date range
     */
    public static List<Expense> getExpensesByDateRange(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, expense_date, description FROM expenses WHERE expense_date BETWEEN ? AND ? ORDER BY expense_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                String dateStr = rs.getString("expense_date");
                java.util.Date date = parseDate(dateStr);
                if (date != null) {
                    expense.setExpenseDate(date);
                }
                expense.setDescription(rs.getString("description"));
                expenses.add(expense);
            }
        }
        return expenses;
    }
    
    /**
     * Get expenses for a specific month and year
     */
    public static List<Expense> getExpensesByMonth(int month, int year) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, expense_date, description FROM expenses WHERE strftime('%m', expense_date) = ? AND strftime('%Y', expense_date) = ? ORDER BY expense_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                String dateStr = rs.getString("expense_date");
                java.util.Date date = parseDate(dateStr);
                if (date != null) {
                    expense.setExpenseDate(date);
                }
                expense.setDescription(rs.getString("description"));
                expenses.add(expense);
            }
        }
        return expenses;
    }
    
    /**
     * Get total expenses by category
     */
    public static double getTotalByCategory(String category) throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE category = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }
    
    /**
     * Get category summary for current month
     */
    public static Map<String, Double> getCategoryBreakdown(int month, int year) throws SQLException {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        String sql = "SELECT category, SUM(amount) as total FROM expenses WHERE strftime('%m', expense_date) = ? AND strftime('%Y', expense_date) = ? GROUP BY category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                breakdown.put(rs.getString("category"), rs.getDouble("total"));
            }
        }
        return breakdown;
    }
    
    /**
     * Get monthly summary
     */
    public static Map<String, Double> getMonthlySummary(int year) throws SQLException {
        Map<String, Double> summary = new LinkedHashMap<>();
        String sql = "SELECT strftime('%m', expense_date) as month, SUM(amount) as total FROM expenses WHERE strftime('%Y', expense_date) = ? GROUP BY strftime('%m', expense_date) ORDER BY month";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String monthKey = "Month " + rs.getInt("month");
                summary.put(monthKey, rs.getDouble("total"));
            }
        }
        return summary;
    }
    
    /**
     * Get total expenses
     */
    public static double getTotalExpenses() throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM expenses";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }
    
    /**
     * Get total expenses for a specific month
     */
    public static double getTotalExpensesByMonth(int month, int year) throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE strftime('%m', expense_date) = ? AND strftime('%Y', expense_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }
    
    /**
     * Update an expense
     */
    public static boolean updateExpense(Expense expense) throws SQLException {
        String sql = "UPDATE expenses SET amount = ?, category = ?, expense_date = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setString(2, expense.getCategory());
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(expense.getExpenseDate()));
            
            pstmt.setString(4, expense.getDescription());
            pstmt.setInt(5, expense.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete an expense
     */
    public static boolean deleteExpense(int id) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get daily average expense
     */
    public static double getDailyAverageExpense(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT DATE(expense_date)) as days, SUM(amount) as total FROM expenses WHERE strftime('%m', expense_date) = ? AND strftime('%Y', expense_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int days = rs.getInt("days");
                double total = rs.getDouble("total");
                return days > 0 ? total / days : 0;
            }
        }
        return 0;
    }
}
