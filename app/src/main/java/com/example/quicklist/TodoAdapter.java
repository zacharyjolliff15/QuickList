package com.example.quicklist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos;
    private OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(Todo todo);
        void onTodoCheckedChanged(Todo todo, boolean isChecked);
        void onTodoDelete(Todo todo);
    }

    public TodoAdapter(List<Todo> todos, OnTodoClickListener listener) {
        this.todos = todos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void updateTodos(List<Todo> newTodos) {
        this.todos = newTodos;
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvCategory;
        private TextView tvAmount;
        private ImageButton btnDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Todo todo) {
            checkBox.setChecked(todo.isCompleted());
            tvTitle.setText(todo.getTitle());
            tvDescription.setText(todo.getDescription());
            tvCategory.setText(todo.getCategory());
            tvAmount.setText("Qty: " + todo.getAmount());

            // Strike through if completed
            if (todo.isCompleted()) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDescription.setPaintFlags(tvDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvDescription.setPaintFlags(tvDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Hide description if empty
            if (todo.getDescription().isEmpty()) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setVisibility(View.VISIBLE);
            }

            // Click listeners
            itemView.setOnClickListener(v -> listener.onTodoClick(todo));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onTodoCheckedChanged(todo, isChecked);
            });

            btnDelete.setOnClickListener(v -> listener.onTodoDelete(todo));
        }
    }
}