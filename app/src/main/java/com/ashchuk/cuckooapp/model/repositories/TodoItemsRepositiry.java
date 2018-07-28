package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TodoItemsRepositiry {

    public static Observable<List<TodoItem>> getTodoItems() {
        return CuckooApp.getDatabase().todoItemDAO().getTodoItems()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<List<TodoItem>> getTodoItemsByUserId(String userId) {
        return CuckooApp.getDatabase().todoItemDAO().getTodoItemsByUserId(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<TodoItem> getTodoItemById(String todoItemId) {
        return CuckooApp.getDatabase().todoItemDAO().getTodoItemById(todoItemId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertTodoItem(TodoItem todoItem) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase()
                        .todoItemDAO()
                        .insert(todoItem))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<List<Long>> insertTodoItems(List<TodoItem> todoItems) {
        return Observable
                .fromCallable(() -> CuckooApp.getDatabase()
                        .todoItemDAO()
                        .insertList(todoItems == null ? new ArrayList<>() : todoItems))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
