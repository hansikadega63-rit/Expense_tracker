package com.expensetracker.dao;

import com.expensetracker.model.Income;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Income Data Access Object
 */
public class IncomeDAO {
    
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
     * Add a new income
     */
    public static int addIncome(Income income) throws SQLException {
        String sql = "INSERT INTO income (amount, source, income_date, notes) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDouble(1, income.getAmount());
            pstmt.setString(2, income.getSource());
            
            // Convert java.util.Date to TEXT format (YYYY-MM-DD)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(income.getIncomeDate()));
            
            pstmt.setString(4, income.getNotes());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    /**
     * Get all income records
     */
    public static List<Income> getAllIncome() throws SQLException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, source, income_date, notes FROM income ORDER BY income_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                incomes.add(buildIncome(rs));
            }
        }
        return incomes;
    }
    
    /**
     * Get income by date range
     */
    public static List<Income> getIncomeByDateRange(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, source, income_date, notes FROM income WHERE income_date BETWEEN ? AND ? ORDER BY income_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incomes.add(buildIncome(rs));
            }
        }
        return incomes;
    }
    
    /**
     * Get income for a specific month
     */
    public static List<Income> getIncomeByMonth(int month, int year) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, source, income_date, notes FROM income WHERE strftime('%m', income_date) = ? AND strftime('%Y', income_date) = ? ORDER BY income_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incomes.add(buildIncome(rs));
            }
        }
        return incomes;
    }
    
    /**
     * Get total income
     */
    public static double getTotalIncome() throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM income";
        
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
     * Get total income for a specific month
     */
    public static double getTotalIncomeByMonth(int month, int year) throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM income WHERE strftime('%m', income_date) = ? AND strftime('%Y', income_date) = ?";
        
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
     * Get income by source
     */
    public static List<Income> getIncomeBySource(String source) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, source, income_date, notes FROM income WHERE source = ? ORDER BY income_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, source);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incomes.add(buildIncome(rs));
            }
        }
        return incomes;
    }
    
    /**
     * Get income sources summary
     */
    public static Map<String, Double> getIncomeSourcesSummary(int month, int year) throws SQLException {
        Map<String, Double> summary = new LinkedHashMap<>();
        String sql = "SELECT source, SUM(amount) as total FROM income WHERE strftime('%m', income_date) = ? AND strftime('%Y', income_date) = ? GROUP BY source";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                summary.put(rs.getString("source"), rs.getDouble("total"));
            }
        }
        return summary;
    }
    
    /**
     * Update income
     */
    public static boolean updateIncome(Income income) throws SQLException {
        String sql = "UPDATE income SET amount = ?, source = ?, income_date = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, income.getAmount());
            pstmt.setString(2, income.getSource());
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(income.getIncomeDate()));
            
            pstmt.setString(4, income.getNotes());
            pstmt.setInt(5, income.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete income
     */
    public static boolean deleteIncome(int id) throws SQLException {
        String sql = "DELETE FROM income WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Build Income object from ResultSet
     */
    private static Income buildIncome(ResultSet rs) throws SQLException {
        Income income = new Income();
        income.setId(rs.getInt("id"));
        income.setAmount(rs.getDouble("amount"));
        income.setSource(rs.getString("source"));
        String dateStr = rs.getString("income_date");
        java.util.Date date = parseDate(dateStr);
        if (date != null) {
            income.setIncomeDate(date);
        }
        income.setNotes(rs.getString("notes"));
        return income;
    }
}
