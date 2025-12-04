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
    private boolean spinnerReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_todo);

        database = TodoDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnSave = findViewById(R.id.btnSave);

        long todoId = getIntent().getLongExtra("TODO_ID", -1);
        if (todoId != -1) {
            isEditMode = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Task");
            }

            database.getTodoById(todoId, new TodoDatabase.TodoCallback() {
                @Override
                public void onSuccess(Todo todo) {
                    currentTodo = todo;
                    if (currentTodo != null) {
                        // Setup spinner first, then populate fields
                        setupCategorySpinner();
                    }
                }
            });
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Task");
            }
            setupCategorySpinner();
        }

        btnSave.setOnClickListener(v -> saveTodo());
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Work");
        categories.add("Personal");
        categories.add("Shopping");
        categories.add("Health");
        categories.add("Other");

        database.getCategories(new TodoDatabase.CategoriesCallback() {
            @Override
            public void onSuccess(List<String> existingCategories) {
                for (String cat : existingCategories) {
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AddEditTodoActivity.this,
                        android.R.layout.simple_spinner_item,
                        categories
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                spinnerReady = true;

                if (isEditMode && currentTodo != null) {
                    populateFields();
                }
            }
        });
    }

    private void populateFields() {
        if (!spinnerReady) {
            return;
        }

        etTitle.setText(currentTodo.getTitle());
        etDescription.setText(currentTodo.getDescription());
        etAmount.setText(String.valueOf(currentTodo.getAmount()));

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(currentTodo.getCategory());
            if (position >= 0) {
                categorySpinner.setSelection(position);
            }
        }
    }

    private void saveTodo() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

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