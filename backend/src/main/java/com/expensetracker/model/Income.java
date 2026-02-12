package com.expensetracker.model;

import java.util.Date;

/**
 * Income Model Class
 */
public class Income {
    private int id;
    private double amount;
    private String source;
    private Date incomeDate;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    
    public Income() {}
    
    public Income(double amount, String source, Date incomeDate, String notes) {
        this.amount = amount;
        this.source = source;
        this.incomeDate = incomeDate;
        this.notes = notes;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Date getIncomeDate() {
        return incomeDate;
    }
    
    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
