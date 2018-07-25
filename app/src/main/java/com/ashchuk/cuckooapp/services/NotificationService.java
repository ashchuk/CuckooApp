package com.ashchuk.cuckooapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.ashchuk.cuckooapp.infrastructure.Constants.USER_GUID_FLAG;
import static com.ashchuk.cuckooapp.infrastructure.Constants.USER_STATUS_FLAG;

// startForeground fail after upgrade to Android 8.1
// https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1

public class NotificationService extends Service {

    private DatabaseReference mUserReference;
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

                    Integer icon = R.drawable.home_icon;

                    if (UserStatus.valueOf(user.Status) == UserStatus.HOME) {
                        icon = R.drawable.home_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.WORK) {
                        icon = R.drawable.work_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.LUNCH) {
                        icon = R.drawable.food_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.DRIVE) {
                        icon = R.drawable.car_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.WALK) {
                        icon = R.drawable.walk_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.SLEEP) {
                        icon = R.drawable.sleep_icon;
                    }

                    startForeground(101, CreateNotification(icon));
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    FirebaseUserEntity user = dataSnapshot.getValue(FirebaseUserEntity.class);

                    Integer icon = R.drawable.home_icon;

                    if (UserStatus.valueOf(user.Status) == UserStatus.HOME) {
                        icon = R.drawable.home_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.WORK) {
                        icon = R.drawable.work_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.LUNCH) {
                        icon = R.drawable.food_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.DRIVE) {
                        icon = R.drawable.car_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.WALK) {
                        icon = R.drawable.walk_icon;
                    } else if (UserStatus.valueOf(user.Status) == UserStatus.SLEEP) {
                        icon = R.drawable.sleep_icon;
                    }

                    startForeground(101, CreateNotification(icon));
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mUserReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUserReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private Notification CreateNotification(Integer currentIcon) {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channelId = createNotificationChannel();
        else
            channelId = "";

        MediaSessionCompat mMediaSession = new MediaSessionCompat(getApplicationContext(), "cuckoo");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSound(alarmSound)
                .setSmallIcon(currentIcon)
                .addAction(R.drawable.home_icon, "Home",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.HOME,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addAction(R.drawable.work_icon, "Work",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.WORK,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addAction(R.drawable.food_icon, "Lunch",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.LUNCH,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addAction(R.drawable.walk_icon, "Walking",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.WALK,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addAction(R.drawable.car_icon, "Driving",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.DRIVE,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addAction(R.drawable.sleep_icon, "Sleep",
                        FirebaseUpdateService
                                .createChangeUserStatusPendingtIntent(getApplicationContext(),
                                        UserStatus.SLEEP,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3 /* #1: pause button */)
                        .setMediaSession(mMediaSession.getSessionToken()))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("You can set your current status here")
                .build();

        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Integer currentIcon = intent.getIntExtra(USER_STATUS_FLAG, R.drawable.home_icon);
        String userGuid = intent.getStringExtra(USER_GUID_FLAG);
        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1)
                .getRef();
        attachDatabaseReadListener();
        startForeground(101, CreateNotification(currentIcon));
        return START_STICKY;
    }

    public void AddFirebaseListener(ValueEventListener listener, String guid) {
        Query query = mUserReference.orderByChild("Guid"); //.equalTo(guid)
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
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class NotificationServiceBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }
}
