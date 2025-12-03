package com.example.quicklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnTodoClickListener {
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private FloatingActionButton fabAdd;
    private Spinner categorySpinner;
    private List<Todo> allTodos;
    private String selectedCategory = "All";
    private TodoDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        database = TodoDatabase.getInstance(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTodos = new ArrayList<>();
        adapter = new TodoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Load todos
        loadTodos();

        // Setup category spinner
        setupCategorySpinner();

        // FAB click listener
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodos();
    }

    private void loadTodos() {
        database.getTodos(new TodoDatabase.TodoListCallback() {
            @Override
            public void onSuccess(List<Todo> todos) {
                allTodos = todos;
                updateTodoList();
                setupCategorySpinner();
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All");

        database.getCategories(new TodoDatabase.CategoriesCallback() {
            @Override
            public void onSuccess(List<String> dbCategories) {
                categories.addAll(dbCategories);

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        categories
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(spinnerAdapter);

                // Set selection to current filter
                int position = categories.indexOf(selectedCategory);
                if (position >= 0) {
                    categorySpinner.setSelection(position);
                }

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategory = categories.get(position);
                        updateTodoList();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private List<Todo> getFilteredTodos() {
        if (selectedCategory.equals("All")) {
            return new ArrayList<>(allTodos);
        } else {
            List<Todo> filtered = new ArrayList<>();
            for (Todo todo : allTodos) {
                if (todo.getCategory().equals(selectedCategory)) {
                    filtered.add(todo);
                }
            }
            return filtered;
        }
    }

    private void updateTodoList() {
        adapter.updateTodos(getFilteredTodos());
    }

    @Override
    public void onTodoClick(Todo todo) {
        Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
        intent.putExtra("TODO_ID", todo.getId());
        startActivity(intent);
    }

    @Override
    public void onTodoCheckedChanged(Todo todo, boolean isChecked) {
        todo.setCompleted(isChecked);
        database.updateTodo(todo);
    }

    @Override
    public void onTodoDelete(Todo todo) {
        database.deleteTodo(todo.getId(), new TodoDatabase.OperationCallback() {
            @Override
            public void onSuccess() {
                loadTodos();
                Toast.makeText(MainActivity.this, "Todo deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error deleting todo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}