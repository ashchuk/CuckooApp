package com.ashchuk.cuckooapp.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.ashchuk.cuckooapp.model.DAO.*;
import com.ashchuk.cuckooapp.model.converters.*;
import com.ashchuk.cuckooapp.model.entities.*;

@Database(entities = {
        Message.class,
        Subscription.class,
        TodoItem.class,
        User.class}, version = 1)
@TypeConverters({DateTypeConverter.class,
        UserTypeConverter.class,
        TodoItemTypeConverter.class,
        UserStatusTypeConverter.class,
        UriTypeConverter.class})
public abstract class CuckooAppDB extends RoomDatabase {
    public abstract MessageDAO messageDAO();

    public abstract SubscriptionDAO subscriptionDAO();

    public abstract UserDAO userDAO();

    public abstract TodoItemDAO todoItemDAO();
}