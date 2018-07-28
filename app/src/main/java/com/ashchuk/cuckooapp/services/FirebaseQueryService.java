package com.ashchuk.cuckooapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryService extends Service {

    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseQueryService.FirebaseQueryServiceBinder binder = new FirebaseQueryService.FirebaseQueryServiceBinder();

    public FirebaseQueryService() {
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FirebaseUserEntity user = dataSnapshot.getValue(FirebaseUserEntity.class);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    FirebaseUserEntity user = dataSnapshot.getValue(FirebaseUserEntity.class);
                    Toast.makeText(getApplicationContext(),
                            "Changed user name is " + user.DisplayName,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mUsersDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void AddGetAllUsersListener(ValueEventListener listener) {
        Query query = mUsersDatabaseReference.orderByChild("Guid");
        query.addListenerForSingleValueEvent(listener);
    }

    public void AddGetUserByGuidListener(ValueEventListener listener, String guid) {
        Query query = mUsersDatabaseReference.orderByChild("Guid").equalTo(guid).limitToFirst(1);
        query.addListenerForSingleValueEvent(listener);
    }

    public void AddGetUserByEmailListener(ValueEventListener listener, String email) {
        Query query = mUsersDatabaseReference.orderByChild("Email").equalTo(email).limitToFirst(1);
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
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        attachDatabaseReadListener();
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
