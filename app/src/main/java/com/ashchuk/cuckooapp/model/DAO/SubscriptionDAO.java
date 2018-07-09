package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.Subscription;

import java.util.List;

@Dao
public interface SubscriptionDAO {
    @Query("SELECT * FROM Subscription")
    List<Subscription> getSubscriptions();
}
