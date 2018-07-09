package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.Message;

import java.util.List;

@Dao
public interface MessageDAO {
    @Query("SELECT * FROM Message")
    List<Message> getMessages();
}
