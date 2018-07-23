package com.ashchuk.cuckooapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUpdateService extends IntentService {
    private static final String UPDATE_STATUS = "com.ashchuk.cuckooapp.services.action.UPDATE_STATUS";
    private static final String EXTRA_STATUS = "com.ashchuk.cuckooapp.services.extra.STATUS";
    private static final String USER_GUID = "com.ashchuk.cuckooapp.services.extra.USER_GUID";

    public FirebaseUpdateService() {
        super("FirebaseUpdateService");
    }

    public static void changeUserStatus(Context context, UserStatus userStatus, String guid) {
        Intent intent = new Intent(context, FirebaseUpdateService.class);
        intent.setAction(UPDATE_STATUS);
        intent.putExtra(EXTRA_STATUS, userStatus.getValue());
        intent.putExtra(USER_GUID, guid);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (UPDATE_STATUS.equals(action)) {
                UserStatus userStatus = UserStatus
                        .valueOf(intent.getIntExtra(EXTRA_STATUS, UserStatus.HOME.getValue()));
                String guid = intent.getStringExtra(USER_GUID);
                handleStatusUpdating(userStatus, guid);
            }
        }
    }

    private void handleStatusUpdating(UserStatus userStatus, String guid) {
        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .orderByChild("Guid")
                .equalTo(guid)
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
                childUpdates.put("users/" + key + "/Status", userStatus.getValue());

                FirebaseDatabase.getInstance()
                        .getReference()
                        .updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
