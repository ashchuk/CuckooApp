package com.ashchuk.cuckooapp;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.ashchuk.cuckooapp.di.components.AppComponent;
import com.ashchuk.cuckooapp.di.components.DaggerAppComponent;
import com.ashchuk.cuckooapp.di.modules.ContextModule;
import com.ashchuk.cuckooapp.model.db.CuckooAppDB;

public class CuckooApp extends Application {

    private static AppComponent sAppComponent;
    private CuckooAppDB database;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        database = Room.databaseBuilder(this, CuckooAppDB.class, "database")
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }


    @VisibleForTesting
    public static void setAppComponent(@NonNull AppComponent appComponent) {
        sAppComponent = appComponent;
    }

    public CuckooAppDB getDatabase() {
        return database;
    }

}
