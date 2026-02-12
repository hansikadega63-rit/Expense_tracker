package com.expensetracker.dao;

import com.expensetracker.model.SavingsGoal;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Savings Goal Data Access Object
 */
public class SavingsGoalDAO {
    
    /**
     * Parse date string from database
     */
    private static java.util.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (Exception e1) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(dateStr);
            } catch (Exception e2) {
                System.err.println("Failed to parse date: " + dateStr);
                return null;
            }
        }
    }
    
    /**
     * Add a new savings goal
     */
    public static int addSavingsGoal(SavingsGoal goal) throws SQLException {
        String sql = "INSERT INTO savings_goals (name, target_amount, current_amount, deadline, priority, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, goal.getName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.setDouble(3, goal.getCurrentAmount());
            
            // Convert date to string format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(4, sdf.format(goal.getDeadline()));
            
            pstmt.setString(5, goal.getPriority());
            pstmt.setString(6, goal.getDescription());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    /**
     * Get all savings goals
     */
    public static List<SavingsGoal> getAllGoals() throws SQLException {
        List<SavingsGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals ORDER BY priority DESC, deadline ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                goals.add(buildGoal(rs));
            }
        }
        return goals;
    }
    
    /**
     * Get savings goal by ID
     */
    public static SavingsGoal getGoalById(int id) throws SQLException {
        String sql = "SELECT * FROM savings_goals WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildGoal(rs);
            }
        }
        return null;
    }
    
    /**
     * Update savings goal
     */
    public static boolean updateGoal(SavingsGoal goal) throws SQLException {
        String sql = "UPDATE savings_goals SET name = ?, target_amount = ?, current_amount = ?, deadline = ?, priority = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, goal.getName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.setDouble(3, goal.getCurrentAmount());
            
            // Convert date to string format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(4, sdf.format(goal.getDeadline()));
            
            pstmt.setString(5, goal.getPriority());
            pstmt.setString(6, goal.getDescription());
            pstmt.setInt(7, goal.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete savings goal
     */
    public static boolean deleteGoal(int id) throws SQLException {
        String sql = "DELETE FROM savings_goals WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update savings goal progress
     */
    public static boolean updateGoalProgress(int id, double currentAmount) throws SQLException {
        String sql = "UPDATE savings_goals SET current_amount = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, currentAmount);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get total savings progress
     */
    public static double getTotalSavingsProgress() throws SQLException {
        String sql = "SELECT SUM(current_amount) as total FROM savings_goals";
        
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
     * Get total savings target
     */
    public static double getTotalSavingsTarget() throws SQLException {
        String sql = "SELECT SUM(target_amount) as total FROM savings_goals";
        
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
     * Get goals by priority
     */
    public static List<SavingsGoal> getGoalsByPriority(String priority) throws SQLException {
        List<SavingsGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals WHERE priority = ? ORDER BY deadline ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, priority);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                goals.add(buildGoal(rs));
            }
        }
        return goals;
    }
    
    /**
     * Get goals status
     */
    public static List<Map<String, Object>> getGoalsStatus() throws SQLException {
        List<Map<String, Object>> status = new ArrayList<>();
        List<SavingsGoal> goals = getAllGoals();
        
        for (SavingsGoal goal : goals) {
            Map<String, Object> goalStatus = new HashMap<>();
            goalStatus.put("id", goal.getId());
            goalStatus.put("name", goal.getName());
            goalStatus.put("targetAmount", goal.getTargetAmount());
            goalStatus.put("currentAmount", goal.getCurrentAmount());
            goalStatus.put("progressPercentage", goal.getProgressPercentage());
            goalStatus.put("remainingAmount", goal.getRemainingAmount());
            goalStatus.put("deadline", goal.getDeadline());
            goalStatus.put("priority", goal.getPriority());
            status.add(goalStatus);
        }
        
        return status;
    }
    
    /**
     * Build SavingsGoal object from ResultSet
     */
    private static SavingsGoal buildGoal(ResultSet rs) throws SQLException {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(rs.getInt("id"));
        goal.setName(rs.getString("name"));
        goal.setTargetAmount(rs.getDouble("target_amount"));
        goal.setCurrentAmount(rs.getDouble("current_amount"));
        
        // Parse deadline date string
        String deadlineStr = rs.getString("deadline");
        java.util.Date deadline = parseDate(deadlineStr);
        if (deadline != null) {
            goal.setDeadline(deadline);
        }
        
        goal.setPriority(rs.getString("priority"));
        goal.setDescription(rs.getString("description"));
        
        // Parse timestamps
        String createdAtStr = rs.getString("created_at");
        java.util.Date createdAt = parseDate(createdAtStr);
        if (createdAt != null) {
            goal.setCreatedAt(new java.sql.Timestamp(createdAt.getTime()));
        }
        
        String updatedAtStr = rs.getString("updated_at");
        java.util.Date updatedAt = parseDate(updatedAtStr);
        if (updatedAt != null) {
            goal.setUpdatedAt(new java.sql.Timestamp(updatedAt.getTime()));
        }
        
        return goal;
    }
}
