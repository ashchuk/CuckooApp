package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.enums.UserStatus;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM User")
    Single<List<User>> getUsers();

    @Query("SELECT * FROM User WHERE Guid = :uuid LIMIT 1")
    Single<User> getUserByUuid(String uuid);

    @Query("UPDATE User SET Status = :userStatus WHERE Guid = :guid")
    long update(UserStatus userStatus, String guid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

}
