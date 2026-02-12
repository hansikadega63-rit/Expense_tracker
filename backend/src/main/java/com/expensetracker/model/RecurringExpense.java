package com.expensetracker.model;

import java.util.Date;

/**
 * Recurring Expense Model Class
 */
public class RecurringExpense {
    private int id;
    private double amount;
    private String category;
    private String description;
    private Date startDate;
    private Date endDate;
    private String frequency;
    private Date nextDueDate;
    private Date lastProcessedDate;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    
    public RecurringExpense() {}
    
    public RecurringExpense(double amount, String category, String description, 
                           Date startDate, String frequency, Date nextDueDate) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.startDate = startDate;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.isActive = true;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public Date getNextDueDate() {
        return nextDueDate;
    }
    
    public void setNextDueDate(Date nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
    
    public Date getLastProcessedDate() {
        return lastProcessedDate;
    }
    
    public void setLastProcessedDate(Date lastProcessedDate) {
        this.lastProcessedDate = lastProcessedDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
