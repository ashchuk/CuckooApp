package com.ashchuk.cuckooapp.model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ashchuk.cuckooapp.model.entities.Subscription;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface SubscriptionDAO {
    @Query("SELECT * FROM Subscription")
    Single<List<Subscription>> getSubscriptions();

    @Query("SELECT * FROM Subscription WHERE userId = :userId")
    Single<List<Subscription>> getSubscriptionsByUserId(String userId);

    @Query("SELECT * FROM Subscription WHERE userId = :subscriptionId LIMIT 1")
    Single<Subscription> getSubscriptionById(String subscriptionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Subscription subscription);
}
