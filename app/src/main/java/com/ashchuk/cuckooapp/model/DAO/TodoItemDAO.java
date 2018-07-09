package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.TodoItem;

import java.util.List;

@Dao
public interface TodoItemDAO {
    @Query("SELECT * FROM TodoItem")
    List<TodoItem> getTodoItems();
}
