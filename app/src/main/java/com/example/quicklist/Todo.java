package com.example.quicklist;

public class Todo {
    private long id;
    private String title;
    private String description;
    private String category;
    private int amount;
    private boolean isCompleted;
    private long createdAt;

    public Todo(String title, String description, String category, int amount) {
        this.id = System.currentTimeMillis();
        this.title = title;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}