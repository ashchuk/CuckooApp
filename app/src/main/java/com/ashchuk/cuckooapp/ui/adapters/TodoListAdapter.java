package com.ashchuk.cuckooapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.entities.TodoItem;
import com.ashchuk.cuckooapp.ui.viewholders.TodoItemViewHolder;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoItemViewHolder> {

    private List<TodoItem> todos;

    public TodoListAdapter(List<TodoItem> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new TodoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemViewHolder holder, int position) {
//        TextView textView = holder.itemView.findViewById(R.id.textView);
//        textView.setText("Some text");
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void removeAt(int position) {
        todos.remove(position);
        notifyItemRemoved(position);
    }
}