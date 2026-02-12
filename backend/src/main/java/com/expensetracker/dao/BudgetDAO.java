package com.expensetracker.dao;

import com.expensetracker.model.Budget;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Budget Data Access Object
 */
public class BudgetDAO {
    
    /**
     * Add a new budget
     */
    public static int addBudget(Budget budget) throws SQLException {
        String sql = "INSERT INTO budgets (category, amount, month, year, alert_threshold) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, budget.getCategory());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setInt(3, budget.getMonth());
            pstmt.setInt(4, budget.getYear());
            pstmt.setDouble(5, budget.getAlertThreshold());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    /**
     * Get all budgets
     */
    public static List<Budget> getAllBudgets() throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets ORDER BY year DESC, month DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                budgets.add(buildBudget(rs));
            }
        }
        return budgets;
    }
    
    /**
     * Get budgets for a specific month
     */
    public static List<Budget> getBudgetsByMonth(int month, int year) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE month = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(buildBudget(rs));
            }
        }
        return budgets;
    }
    
    /**
     * Get budget by category and month
     */
    public static Budget getBudgetByCategoryAndMonth(String category, int month, int year) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE category = ? AND month = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildBudget(rs);
            }
        }
        return null;
    }
    
    /**
     * Update a budget
     */
    public static boolean updateBudget(Budget budget) throws SQLException {
        String sql = "UPDATE budgets SET amount = ?, alert_threshold = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, budget.getAmount());
            pstmt.setDouble(2, budget.getAlertThreshold());
            pstmt.setInt(3, budget.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete a budget
     */
    public static boolean deleteBudget(int id) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get budget status for a category (percentage used)
     */
    public static double getBudgetUsagePercentage(String category, int month, int year) throws SQLException {
        Budget budget = getBudgetByCategoryAndMonth(category, month, year);
        if (budget == null) return 0;
        
        double spent = ExpenseDAO.getTotalByCategory(category);
        return (spent / budget.getAmount()) * 100;
    }
    
    /**
     * Check if budget alert threshold exceeded
     */
    public static boolean isBudgetAlertTriggered(String category, int month, int year) throws SQLException {
        Budget budget = getBudgetByCategoryAndMonth(category, month, year);
        if (budget == null) return false;
        
        double percentage = getBudgetUsagePercentage(category, month, year);
        return percentage >= (budget.getAlertThreshold() * 100);
    }
    
    /**
     * Check if budget exceeded
     */
    public static boolean isBudgetExceeded(String category, int month, int year) throws SQLException {
        Budget budget = getBudgetByCategoryAndMonth(category, month, year);
        if (budget == null) return false;
        
        double percentage = getBudgetUsagePercentage(category, month, year);
        return percentage >= 100;
    }
    
    /**
     * Get all categories with budget alerts for a month
     */
    public static List<Map<String, Object>> getBudgetAlerts(int month, int year) throws SQLException {
        List<Map<String, Object>> alerts = new ArrayList<>();
        List<Budget> budgets = getBudgetsByMonth(month, year);
        
        for (Budget budget : budgets) {
            double spent = ExpenseDAO.getTotalByCategory(budget.getCategory());
            double percentage = (spent / budget.getAmount()) * 100;
            
            if (percentage >= (budget.getAlertThreshold() * 100)) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("category", budget.getCategory());
                alert.put("budgeted", budget.getAmount());
                alert.put("spent", spent);
                alert.put("percentage", percentage);
                alert.put("threshold", budget.getAlertThreshold() * 100);
                alert.put("isExceeded", percentage >= 100);
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
    
    /**
     * Parse date string from database
     */
    private static java.util.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (Exception e1) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(dateStr);
            } catch (Exception e2) {
                System.err.println("Failed to parse date: " + dateStr);
                return null;
            }
        }
    }
    
    /**
     * Build Budget object from ResultSet
     */
    private static Budget buildBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setCategory(rs.getString("category"));
        budget.setAmount(rs.getDouble("amount"));
        budget.setMonth(rs.getInt("month"));
        budget.setYear(rs.getInt("year"));
        budget.setAlertThreshold(rs.getDouble("alert_threshold"));
        
        // Parse timestamps
        String createdAtStr = rs.getString("created_at");
        java.util.Date createdAt = parseDate(createdAtStr);
        if (createdAt != null) {
            budget.setCreatedAt(new java.sql.Timestamp(createdAt.getTime()));
        }
        
        String updatedAtStr = rs.getString("updated_at");
        java.util.Date updatedAt = parseDate(updatedAtStr);
        if (updatedAt != null) {
            budget.setUpdatedAt(new java.sql.Timestamp(updatedAt.getTime()));
        }
        
        return budget;
    }
}
