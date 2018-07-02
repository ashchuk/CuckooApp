package com.ashchuk.cuckooapp;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.ashchuk.cuckooapp.di.components.AppComponent;
import com.ashchuk.cuckooapp.di.components.DaggerAppComponent;
import com.ashchuk.cuckooapp.di.modules.ContextModule;

public class CuckooApp extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }


    @VisibleForTesting
    public static void setAppComponent(@NonNull AppComponent appComponent) {
        sAppComponent = appComponent;
    }

}
