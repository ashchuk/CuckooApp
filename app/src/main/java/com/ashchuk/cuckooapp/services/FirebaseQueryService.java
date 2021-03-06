package com.ashchuk.cuckooapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.ashchuk.cuckooapp.infrastructure.Constants.EMAIL_FIREBASE_KEY_NAME;
import static com.ashchuk.cuckooapp.infrastructure.Constants.GUID_FIREBASE_KEY_NAME;
import static com.ashchuk.cuckooapp.infrastructure.Constants.USERS_FIREBASE_REFERENCE_NAME;

public class FirebaseQueryService extends Service {

    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseQueryService.FirebaseQueryServiceBinder binder = new FirebaseQueryService.FirebaseQueryServiceBinder();

    public FirebaseQueryService() {
    }

    public void AddGetAllUsersListener(ValueEventListener listener) {
        Query query = mUsersDatabaseReference.orderByChild(GUID_FIREBASE_KEY_NAME);
        query.addListenerForSingleValueEvent(listener);
    }

    public void AddGetUserByGuidListener(ValueEventListener listener, String guid) {
        Query query = mUsersDatabaseReference.orderByChild(GUID_FIREBASE_KEY_NAME).equalTo(guid).limitToFirst(1);
        query.addListenerForSingleValueEvent(listener);
    }

    public void AddGetUserByEmailListener(ValueEventListener listener, String email) {
        Query query = mUsersDatabaseReference.orderByChild(EMAIL_FIREBASE_KEY_NAME).equalTo(email).limitToFirst(1);
        query.addListenerForSingleValueEvent(listener);
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(USERS_FIREBASE_REFERENCE_NAME);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class FirebaseQueryServiceBinder extends Binder {
        public FirebaseQueryService getService() {
            return FirebaseQueryService.this;
        }
    }

}
