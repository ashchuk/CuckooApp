package com.ashchuk.cuckooapp.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ashchuk.cuckooapp.model.entities.Message;
import com.ashchuk.cuckooapp.model.entities.TodoItem;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.TodoItemsRepositiry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;

public class FirebaseUpdateService extends IntentService {
    private static final String UPDATE_STATUS = "com.ashchuk.cuckooapp.services.action.UPDATE_STATUS";
    private static final String ADD_SUBSCRIPTION = "com.ashchuk.cuckooapp.services.action.ADD_SUBSCRIPTION";
    private static final String REMOVE_SUBSCRIPTION = "com.ashchuk.cuckooapp.services.action.REMOVE_SUBSCRIPTION";
    private static final String UPDATE_MESSAGE = "com.ashchuk.cuckooapp.services.action.UPDATE_MESSAGE";
    private static final String ADD_TODO = "com.ashchuk.cuckooapp.services.action.ADD_TODO";
    private static final String REMOVE_TODO = "com.ashchuk.cuckooapp.services.action.REMOVE_TODO";
    private static final String MARK_DONE_TODO = "com.ashchuk.cuckooapp.services.action.MARK_DONE_TODO";
    private static final String SET_GPS = "com.ashchuk.cuckooapp.services.action.SET_GPS";

    private static final String EXTRA_USER_GUID = "com.ashchuk.cuckooapp.services.extra.USER_GUID";
    private static final String EXTRA_CREATOR_GUID = "com.ashchuk.cuckooapp.services.extra.CREATOR_GUID";
    private static final String EXTRA_TODO_GUID = "com.ashchuk.cuckooapp.services.extra.TODO_GUID";
    private static final String EXTRA_MESSAGE_GUID = "com.ashchuk.cuckooapp.services.extra.MESSAGE_GUID";
    private static final String EXTRA_MESSAGE_TEXT = "com.ashchuk.cuckooapp.services.extra.MESSAGE_TEXT";
    private static final String EXTRA_SUBSCRIPTION_GUID = "com.ashchuk.cuckooapp.services.extra.SUBSCRIPTION_GUID";
    private static final String EXTRA_STATUS = "com.ashchuk.cuckooapp.services.extra.STATUS";
    private static final String EXTRA_GPS = "com.ashchuk.cuckooapp.services.extra.GPS";


    public FirebaseUpdateService() {
        super("FirebaseUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (UPDATE_STATUS.equals(action)) {
                UserStatus userStatus = UserStatus
                        .valueOf(intent.getIntExtra(EXTRA_STATUS, UserStatus.HOME.getValue()));
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                handleStatusUpdating(userStatus, userGuid);
            } else if (ADD_SUBSCRIPTION.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String subscriptionGuid = intent.getStringExtra(EXTRA_SUBSCRIPTION_GUID);
                handleSubscriptionAdd(userGuid, subscriptionGuid);
            } else if (REMOVE_SUBSCRIPTION.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String subscriptionGuid = intent.getStringExtra(EXTRA_SUBSCRIPTION_GUID);
                handleSubscriptionRemove(userGuid, subscriptionGuid);
            } else if (UPDATE_MESSAGE.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String creatorGuid = intent.getStringExtra(EXTRA_CREATOR_GUID);
                String messageGuid = intent.getStringExtra(EXTRA_MESSAGE_GUID);
                String messageText = intent.getStringExtra(EXTRA_MESSAGE_TEXT);
                handleMessageUpdating(userGuid, creatorGuid, messageGuid, messageText);
            } else if (ADD_TODO.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String todoGuid = intent.getStringExtra(EXTRA_TODO_GUID);
                String creatorGuid = intent.getStringExtra(EXTRA_CREATOR_GUID);
                handleTodoAdd(userGuid, creatorGuid, todoGuid);
            } else if (REMOVE_TODO.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String todoGuid = intent.getStringExtra(EXTRA_TODO_GUID);
                handleTodoRemove(userGuid, todoGuid);
            } else if (MARK_DONE_TODO.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String todoGuid = intent.getStringExtra(EXTRA_TODO_GUID);
                handleTodoDone(userGuid, todoGuid);
            } else if (SET_GPS.equals(action)) {
                String userGuid = intent.getStringExtra(EXTRA_USER_GUID);
                String gps = intent.getStringExtra(EXTRA_GPS);
                handleGpsSet(userGuid, gps);
            }
        }
    }

    public static PendingIntent createChangeUserStatusPendingtIntent(Context context, UserStatus userStatus, String guid) {
        Intent intent = new Intent(context, FirebaseUpdateService.class);
        intent.setAction(UPDATE_STATUS);
        intent.putExtra(EXTRA_STATUS, userStatus.getValue());
        intent.putExtra(EXTRA_USER_GUID, guid);
        return PendingIntent.getService(
                context,
                userStatus.getValue(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void changeUserStatus(Context context, UserStatus userStatus, String guid) {
        Intent intent = new Intent(context, FirebaseUpdateService.class);
        intent.setAction(UPDATE_STATUS);
        intent.putExtra(EXTRA_STATUS, userStatus.getValue());
        intent.putExtra(EXTRA_USER_GUID, guid);
        context.startService(intent);
    }

    public static void addUserSubscription(Context context, String subscriptionGuid,
                                           String currentUserGuid) {
        Intent intent = new Intent(context, FirebaseUpdateService.class);
        intent.setAction(ADD_SUBSCRIPTION);
        intent.putExtra(EXTRA_USER_GUID, currentUserGuid);
        intent.putExtra(EXTRA_SUBSCRIPTION_GUID, subscriptionGuid);
        context.startService(intent);
    }

    private void handleTodoDone(String userGuid, String todoGuid) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                for (TodoItem todoItem : entity.Todos) {
                    if (todoItem.userId.equals(userGuid) && todoItem.id.equals(todoGuid))
                        todoItem.isDone = true;
                }

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(key)
                        .setValue(entity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleGpsSet(String userGuid, String gps) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                if (key == null)
                    return;

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("users/" + key + "/Gps", gps);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleTodoRemove(String userGuid, String todoGuid) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                FirebaseUserEntity finalEntity = entity;
                String finalKey = key;
                TodoItemsRepositiry.getTodoItemById(todoGuid)
                        .subscribeOn(Schedulers.io())
                        .subscribe(todoItem -> {

                            finalEntity.Todos.remove(todoItem);

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .child(finalKey)
                                    .setValue(finalEntity);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleTodoAdd(String userGuid, String creatorGuid, String todoGuid) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                FirebaseUserEntity finalEntity = entity;
                String finalKey = key;
                TodoItemsRepositiry.getTodoItemById(todoGuid)
                        .subscribeOn(Schedulers.io())
                        .subscribe(todoItem -> {

                            finalEntity.Todos.add(todoItem);

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .child(finalKey)
                                    .setValue(finalEntity);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleSubscriptionAdd(String userGuid, String subscriptionGuid) {
        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                FirebaseUserEntity finalEntity = entity;
                String finalKey = key;
                SubscriptionsRepository.getSubscriptionById(subscriptionGuid)
                        .subscribeOn(Schedulers.io())
                        .subscribe(subscription -> {

                            if (finalEntity.Subscriptions == null)
                                finalEntity.Subscriptions = new ArrayList<>();

                            finalEntity.Subscriptions.add(subscription);

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .child(finalKey)
                                    .setValue(finalEntity);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleSubscriptionRemove(String userGuid, String subscriptionGuid) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                FirebaseUserEntity finalEntity = entity;
                String finalKey = key;
                SubscriptionsRepository.getSubscriptionById(subscriptionGuid)
                        .subscribeOn(Schedulers.io())
                        .subscribe(subscription -> {

                            if (finalEntity.Subscriptions != null)
                                finalEntity.Subscriptions.remove(subscription);

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .child(finalKey)
                                    .setValue(finalEntity);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleMessageUpdating(String userGuid, String creatorGuid, String messageGuid, String messageText) {
        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                for (Message message : entity.Messages) {
                    if (message.creatorId.equals(creatorGuid) && message.id.equals(messageGuid))
                        message.messageText = messageText;
                }

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(key)
                        .setValue(entity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleStatusUpdating(UserStatus userStatus, String userGuid) {
        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(userGuid)
                .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                entity.LastUpdateDate = new Date();
                entity.Status = userStatus.getValue();

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(key)
                        .setValue(entity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
