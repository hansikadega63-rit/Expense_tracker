package com.expensetracker.model;

import java.util.Date;

/**
 * Expense Model Class
 */
public class Expense {
    private int id;
    private double amount;
    private String category;
    private Date expenseDate;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    
    public Expense() {}
    
    public Expense(double amount, String category, Date expenseDate, String description) {
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.description = description;
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Date getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
