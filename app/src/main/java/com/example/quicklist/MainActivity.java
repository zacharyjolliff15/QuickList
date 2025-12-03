package com.example.quicklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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
    private EditText searchBar;
    private List<Todo> allTodos;
    private String selectedCategory = "All";
    private String searchQuery = "";
    private TodoDatabase database;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preference
        preferences = getSharedPreferences("QuickListPrefs", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize database
        database = TodoDatabase.getInstance(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        categorySpinner = findViewById(R.id.categorySpinner);
        searchBar = findViewById(R.id.searchBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTodos = new ArrayList<>();
        adapter = new TodoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Load todos
        loadTodos();

        // Setup category spinner
        setupCategorySpinner();

        // Setup search bar
        setupSearchBar();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dark_theme) {
            toggleDarkMode();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.action_export) {
            exportList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleDarkMode() {
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dark_mode", !isDarkMode);
        editor.apply();

        // Restart activity to apply theme
        recreate();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About QuickList")
                .setMessage("QuickList - Simple Task Manager\n\n" +
                        "Version 1.0\n\n" +
                        "A simple and efficient todo list app to help you stay organized.\n\n" +
                        "Features:\n" +
                        "• Create and manage tasks\n" +
                        "• Organize by categories\n" +
                        "• Search and filter\n" +
                        "• Track quantities\n" +
                        "• Dark mode support")
                .setPositiveButton("OK", null)
                .show();
    }

    private void exportList() {
        if (allTodos.isEmpty()) {
            Toast.makeText(this, "No tasks to export", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("QuickList - My Tasks\n\n");

        for (Todo todo : allTodos) {
            content.append(todo.isCompleted() ? "☑ " : "☐ ");
            content.append(todo.getTitle());
            if (!todo.getDescription().isEmpty()) {
                content.append("\n  ").append(todo.getDescription());
            }
            content.append("\n  Category: ").append(todo.getCategory());
            content.append(" | Qty: ").append(todo.getAmount());
            content.append("\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My QuickList Tasks");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        startActivity(Intent.createChooser(shareIntent, "Share tasks via"));
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                updateTodoList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        List<Todo> filtered = new ArrayList<>();

        for (Todo todo : allTodos) {
            // Apply category filter
            boolean matchesCategory = selectedCategory.equals("All") ||
                    todo.getCategory().equals(selectedCategory);

            // Apply search filter
            boolean matchesSearch = searchQuery.isEmpty() ||
                    todo.getTitle().toLowerCase().contains(searchQuery) ||
                    todo.getDescription().toLowerCase().contains(searchQuery);

            if (matchesCategory && matchesSearch) {
                filtered.add(todo);
            }
        }

        return filtered;
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