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

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                TodoDao dao = instance.todoDao();

                dao.insertTodo(new TodoEntity("Buy groceries!", "Milk, eggs, bread", "Shopping", 3));
                dao.insertTodo(new TodoEntity("Finish project report", "Complete the Q4 analysis", "Work", 1));
                dao.insertTodo(new TodoEntity("Morning run", "", "Fitness", 2));
                dao.insertTodo(new TodoEntity("Call dentist", "Schedule appointment", "Dental", 1));
                dao.insertTodo(new TodoEntity("Read book", "Finish chapter 5", "Reading", 1));
                dao.insertTodo(new TodoEntity("Pay utilities", "", "Finance", 1));
                dao.insertTodo(new TodoEntity("Study for exam", "Chapters 3-7", "Education", 2));
                dao.insertTodo(new TodoEntity("Book flight", "", "Travel", 1));
                dao.insertTodo(new TodoEntity("Fix leaky faucet", "Buy washers", "Home", 2));
                dao.insertTodo(new TodoEntity("Clean garage", "", "Cleaning", 2));
                dao.insertTodo(new TodoEntity("Meetup with friends", "Coffee at 7pm", "Social", 1));
                dao.insertTodo(new TodoEntity("Call mom", "", "Family", 1));
                dao.insertTodo(new TodoEntity("Update resume", "Add recent internship", "Career", 1));
                dao.insertTodo(new TodoEntity("Install software update", "", "Technology", 1));
                dao.insertTodo(new TodoEntity("Paint model kit", "Finish wings", "Hobby", 2));
                dao.insertTodo(new TodoEntity("Oil change", "", "Maintenance", 1));
                dao.insertTodo(new TodoEntity("Plant new herbs", "Basil, thyme", "Gardening", 1));
                dao.insertTodo(new TodoEntity("Vet appointment", "", "Pets", 1));
                dao.insertTodo(new TodoEntity("Review contract", "Check clauses A-D", "Legal", 2));
                dao.insertTodo(new TodoEntity("Renew insurance", "", "Insurance", 1));
                dao.insertTodo(new TodoEntity("Prepare portfolio", "Select 10 best projects", "Portfolio", 2));
                dao.insertTodo(new TodoEntity("Brainstorm logo ideas", "", "Creativity", 2));
                dao.insertTodo(new TodoEntity("Laundry", "", "Chores", 1));
                dao.insertTodo(new TodoEntity("Doctor checkup", "Annual physical", "Appointments", 1));
                dao.insertTodo(new TodoEntity("RSVP for conference", "", "Events", 1));
                dao.insertTodo(new TodoEntity("Volunteer signup", "Community center", "Volunteering", 1));
                dao.insertTodo(new TodoEntity("Read devotional", "", "Spiritual", 1));
                dao.insertTodo(new TodoEntity("Meditation session", "20 minutes", "Wellness", 1));
                dao.insertTodo(new TodoEntity("Create monthly budget", "", "Budgeting", 1));
                dao.insertTodo(new TodoEntity("Donate old clothes", "Drop at shelter", "Donations", 1));
            }).start();
        }
    };

}