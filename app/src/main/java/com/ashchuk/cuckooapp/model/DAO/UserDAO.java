package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM User")
    List<User> getUsers();
}
