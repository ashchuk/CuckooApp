package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.entities.TodoItem;
import com.ashchuk.cuckooapp.mvp.presenters.TodoListActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ITodoListActivityView;
import com.ashchuk.cuckooapp.ui.adapters.TodoListAdapter;
import com.ashchuk.cuckooapp.ui.helpers.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TodoListActivity
        extends MvpAppCompatActivity
        implements ITodoListActivityView {

    @InjectPresenter
    TodoListActivityPresenter todoListActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);


        List<TodoItem> list = new ArrayList<>(Arrays
                .asList(new TodoItem(), new TodoItem(), new TodoItem(),
                        new TodoItem(), new TodoItem(), new TodoItem(),
                        new TodoItem(), new TodoItem(), new TodoItem(),
                        new TodoItem(), new TodoItem(), new TodoItem()));

        RecyclerView recyclerView = findViewById(R.id.subscriptions_list);
        recyclerView.setAdapter(new TodoListAdapter(list));
        recyclerView.setLayoutManager(new LinearLayoutManager(TodoListActivity.this));

        SwipeToDeleteCallback handler = new SwipeToDeleteCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                TodoListAdapter adapter = (TodoListAdapter) recyclerView.getAdapter();
                adapter.removeAt(viewHolder.getAdapterPosition());
                super.onSwiped(viewHolder, direction);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(handler);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
