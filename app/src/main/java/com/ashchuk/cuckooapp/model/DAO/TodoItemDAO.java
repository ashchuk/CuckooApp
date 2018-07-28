package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.TodoItem;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface TodoItemDAO {
    @Query("SELECT * FROM TodoItem")
    Single<List<TodoItem>> getTodoItems();

    @Query("SELECT * FROM TodoItem WHERE userId = :userId")
    Single<List<TodoItem>> getTodoItemsByUserId(String userId);

    @Query("SELECT * FROM TodoItem WHERE id = :todoItemId LIMIT 1")
    Single<TodoItem> getTodoItemById(String todoItemId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TodoItem todoItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long>  insertList(List<TodoItem> todoItems);
}
