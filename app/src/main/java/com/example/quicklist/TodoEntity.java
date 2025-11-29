package com.example.quicklist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todos")
public class TodoEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "amount")
    private int amount;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor
    public TodoEntity(String title, String description, String category, int amount) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Convert to Todo model
    public Todo toTodo() {
        Todo todo = new Todo(title, description, category, amount);
        todo.setId(id);
        todo.setCompleted(isCompleted);
        todo.setCreatedAt(createdAt);
        return todo;
    }

    // Create from Todo model
    public static TodoEntity fromTodo(Todo todo) {
        TodoEntity entity = new TodoEntity(
                todo.getTitle(),
                todo.getDescription(),
                todo.getCategory(),
                todo.getAmount()
        );
        entity.setId(todo.getId());
        entity.setCompleted(todo.isCompleted());
        entity.setCreatedAt(todo.getCreatedAt());
        return entity;
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