package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.Message;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface MessageDAO {
    @Query("SELECT * FROM Message")
    Single<List<Message>> getMessages();

    @Query("SELECT * FROM Message WHERE userId = :userId")
    Single<List<Message>> getMessagesByUserId(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert (Message message);
}
