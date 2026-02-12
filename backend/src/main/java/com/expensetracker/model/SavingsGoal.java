package com.expensetracker.model;

import java.util.Date;

/**
 * Savings Goal Model Class
 */
public class SavingsGoal {
    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private Date deadline;
    private String priority;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    
    public SavingsGoal() {}
    
    public SavingsGoal(String name, double targetAmount, Date deadline, String priority) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.priority = priority;
        this.currentAmount = 0;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public double getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public Date getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
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
    
    /**
     * Get progress percentage
     */
    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }
    
    /**
     * Get remaining amount
     */
    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentAmount);
    }
}
