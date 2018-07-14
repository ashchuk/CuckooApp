package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.TodoItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class TodoItemsRepositiry {

    public static Observable<List<TodoItem>> getTodoItems() {
        return CuckooApp.getDatabase().todoItemDAO().getTodoItems()
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<List<TodoItem>> getTodoItemsByUserId(String userId) {
        return CuckooApp.getDatabase().todoItemDAO().getTodoItemsByUserId(userId)
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertTodoItem(TodoItem todoItem) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase()
                        .todoItemDAO()
                        .insert(todoItem));
    }

}
