package com.example.quicklist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TodoDatabase {
    private static final String TAG = "TodoDatabase";
    private static TodoDatabase instance;
    private AppDatabase database;
    private TodoDao todoDao;

    private TodoDatabase(Context context) {
        database = AppDatabase.getInstance(context);
        todoDao = database.todoDao();
    }

    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new TodoDatabase(context.getApplicationContext());
        }
        return instance;
    }

    public void getTodos(final TodoListCallback callback) {
        new AsyncTask<Void, Void, List<Todo>>() {
            @Override
            protected List<Todo> doInBackground(Void... voids) {
                List<TodoEntity> entities = todoDao.getAllTodos();
                List<Todo> todos = new ArrayList<>();
                for (TodoEntity entity : entities) {
                    todos.add(entity.toTodo());
                }
                return todos;
            }

            @Override
            protected void onPostExecute(List<Todo> todos) {
                callback.onSuccess(todos);
            }
        }.execute();
    }

    // Get todo by ID (synchronous - runs on background automatically in Room)
    public void getTodoById(final long id, final TodoCallback callback) {
        new AsyncTask<Void, Void, Todo>() {
            @Override
            protected Todo doInBackground(Void... voids) {
                try {
                    TodoEntity entity = todoDao.getTodoById(id);
                    return entity != null ? entity.toTodo() : null;
                } catch (Exception e) {
                    Log.e(TAG, "Error getting todo by id", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Todo todo) {
                if (callback != null) {
                    callback.onSuccess(todo);
                }
            }
        }.execute();
    }

    public void getCategories(final CategoriesCallback callback) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                try {
                    List<String> categories = todoDao.getAllCategories();
                    return categories != null ? categories : new ArrayList<>();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting categories", e);
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<String> categories) {
                if (callback != null) {
                    callback.onSuccess(categories);
                }
            }
        }.execute();
    }

    public void addTodo(final Todo todo, final OperationCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    TodoEntity entity = TodoEntity.fromTodo(todo);
                    long id = todoDao.insertTodo(entity);
                    todo.setId(id);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error adding todo", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to add todo");
                    }
                }
            }
        }.execute();
    }

    public void updateTodo(final Todo todo, final OperationCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    TodoEntity entity = TodoEntity.fromTodo(todo);
                    todoDao.updateTodo(entity);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error updating todo", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to update todo");
                    }
                }
            }
        }.execute();
    }

    public void updateTodo(final Todo todo) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    TodoEntity entity = TodoEntity.fromTodo(todo);
                    todoDao.updateTodo(entity);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating todo", e);
                }
                return null;
            }
        }.execute();
    }

    public void deleteTodo(final long id, final OperationCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    todoDao.deleteTodoById(id);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting todo", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to delete todo");
                    }
                }
            }
        }.execute();
    }

    public interface TodoListCallback {
        void onSuccess(List<Todo> todos);
    }

    public interface TodoCallback {
        void onSuccess(Todo todo);
    }

    public interface CategoriesCallback {
        void onSuccess(List<String> categories);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }
}