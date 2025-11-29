package com.example.quicklist;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class AddEditTodoActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etAmount;
    private Spinner categorySpinner;
    private Button btnSave;
    private Todo currentTodo;
    private boolean isEditMode = false;
    private TodoDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_todo);

        // Initialize database
        database = TodoDatabase.getInstance(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnSave = findViewById(R.id.btnSave);

        // Check if editing existing todo
        long todoId = getIntent().getLongExtra("TODO_ID", -1);
        if (todoId != -1) {
            isEditMode = true;
            currentTodo = database.getTodoById(todoId);
            if (currentTodo != null) {
                populateFields();
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Task");
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Task");
            }
        }

        // Setup category spinner
        setupCategorySpinner();

        // Save button click
        btnSave.setOnClickListener(v -> saveTodo());
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Work");
        categories.add("Personal");
        categories.add("Shopping");
        categories.add("Health");
        categories.add("Other");

        // Add existing custom categories
        List<String> existingCategories = database.getCategories();
        for (String cat : existingCategories) {
            if (!categories.contains(cat)) {
                categories.add(cat);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void populateFields() {
        etTitle.setText(currentTodo.getTitle());
        etDescription.setText(currentTodo.getDescription());
        etAmount.setText(String.valueOf(currentTodo.getAmount()));

        // Set category spinner selection
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
        int position = adapter.getPosition(currentTodo.getCategory());
        if (position >= 0) {
            categorySpinner.setSelection(position);
        }
    }

    private void saveTodo() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        // Validation
        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        int amount = 1;
        if (!amountStr.isEmpty()) {
            try {
                amount = Integer.parseInt(amountStr);
                if (amount < 1) {
                    amount = 1;
                }
            } catch (NumberFormatException e) {
                amount = 1;
            }
        }

        if (isEditMode && currentTodo != null) {
            // Update existing todo
            currentTodo.setTitle(title);
            currentTodo.setDescription(description);
            currentTodo.setAmount(amount);
            currentTodo.setCategory(category);

            database.updateTodo(currentTodo, new TodoDatabase.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddEditTodoActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddEditTodoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new todo
            Todo newTodo = new Todo(title, description, category, amount);
            database.addTodo(newTodo, new TodoDatabase.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddEditTodoActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddEditTodoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}