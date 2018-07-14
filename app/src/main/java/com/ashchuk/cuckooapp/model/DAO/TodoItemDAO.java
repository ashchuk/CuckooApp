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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TodoItem todoItem);
}
