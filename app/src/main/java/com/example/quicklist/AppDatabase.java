package com.example.quicklist;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

@Database(entities = {TodoEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TodoDao todoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "todo_database"
                    )
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    // Callback to add sample data on first creation
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Insert sample data in background thread
            new Thread(() -> {
                TodoDao dao = instance.todoDao();

                dao.insertTodo(new TodoEntity("Buy groceries", "Milk, eggs, bread", "Shopping", 3));
                dao.insertTodo(new TodoEntity("Finish project report", "Complete the Q4 analysis", "Work", 1));
                dao.insertTodo(new TodoEntity("Gym workout", "Cardio and weights", "Health", 1));
                dao.insertTodo(new TodoEntity("Call dentist", "Schedule appointment", "Health", 1));
                dao.insertTodo(new TodoEntity("Read book", "Finish chapter 5", "Personal", 1));
            }).start();
        }
    };
}