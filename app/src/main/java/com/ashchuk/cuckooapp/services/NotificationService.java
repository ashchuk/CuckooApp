package com.ashchuk.cuckooapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.ui.activities.SubscriptionsActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

// startForeground fail after upgrade to Android 8.1
// https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1

public class NotificationService extends Service {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private NotificationServiceBinder binder = new NotificationServiceBinder();

    public NotificationService() {
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

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent showTaskIntent = new Intent(getApplicationContext(), SubscriptionsActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channelId = createNotificationChannel();
        else
            channelId = "";

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//        https://developer.android.com/training/notify-user/custom-notification

            MediaSessionCompat mMediaSession = new MediaSessionCompat(getApplicationContext(), "cuckoo");
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.fui_ic_googleg_color_24dp)
                    .addAction(R.drawable.ic_menu_share, "Share", contentIntent)
                    .addAction(R.drawable.ic_menu_camera, "Camera", contentIntent)
                    .addAction(R.drawable.ic_menu_send, "Send", contentIntent)
                    .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(1 /* #1: pause button */)
                            .setMediaSession(mMediaSession.getSessionToken()))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Cuckooo app is still working")
                    .build();

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
            attachDatabaseReadListener();

            startForeground(101, notification);

        return START_STICKY;
    }

    public void createNewNotification(){
        Intent showTaskIntent = new Intent(getApplicationContext(), SubscriptionsActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channelId = createNotificationChannel();
        else
            channelId = "";

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        MediaSessionCompat mMediaSession = new MediaSessionCompat(getApplicationContext(), "cuckoo");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.ic_menu_share)
                .addAction(R.drawable.ic_menu_share, "Share", contentIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0 /* #1: pause button */)
                        .setMediaSession(mMediaSession.getSessionToken()))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("New message")
                .build();

        startForeground(101, notification);


    }

    public void AddFirebaseListener(ValueEventListener listener, String guid){
        Query query = mUsersDatabaseReference.orderByChild("Guid"); //.equalTo(guid)
        query.addListenerForSingleValueEvent(listener);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "CuckooApp";
        String channelName = "CuckooApp Service";
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    public class NotificationServiceBinder extends Binder {
        public NotificationService getService(){ return NotificationService.this; }
    }
}
