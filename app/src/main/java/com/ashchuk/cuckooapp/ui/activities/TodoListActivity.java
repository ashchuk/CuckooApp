package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.mvp.presenters.TodoListActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ITodoListActivityView;

public class TodoListActivity
        extends MvpAppCompatActivity
        implements ITodoListActivityView{

    @InjectPresenter
    TodoListActivityPresenter todoListActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
    }
}
