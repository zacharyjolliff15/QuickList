package com.example.quicklist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY created_at DESC")
    List<TodoEntity> getAllTodos();

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    TodoEntity getTodoById(long id);

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY created_at DESC")
    List<TodoEntity> getTodosByCategory(String category);

    @Query("SELECT DISTINCT category FROM todos")
    List<String> getAllCategories();

    @Insert
    long insertTodo(TodoEntity todo);

    @Update
    void updateTodo(TodoEntity todo);

    @Delete
    void deleteTodo(TodoEntity todo);

    @Query("DELETE FROM todos WHERE id = :id")
    void deleteTodoById(long id);

    @Query("DELETE FROM todos")
    void deleteAllTodos();

    @Query("SELECT COUNT(*) FROM todos")
    int getTodoCount();
}