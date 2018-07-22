package com.ashchuk.cuckooapp.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityToUserConverter;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.mvp.presenters.SearchActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ISearchActivityView;
import com.ashchuk.cuckooapp.services.NotificationService;
import com.ashchuk.cuckooapp.ui.adapters.SubscriptionsListAdapter;
import com.ashchuk.cuckooapp.ui.adapters.UsersSearchListAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity
        extends MvpAppCompatActivity
        implements ISearchActivityView {

    @InjectPresenter
    SearchActivityPresenter searchActivityPresenter;

    private NotificationService notificationService;
    private ServiceConnection serviceConnection;

    private List<User> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.users_list);
        recyclerView.setAdapter(new UsersSearchListAdapter(testList));
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    testList.clear();
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        FirebaseUserEntity result = user.getValue(FirebaseUserEntity.class);
                        testList.add(FirebaseUserEntityToUserConverter
                                .convert(Objects.requireNonNull(result)));
                    }
                    Toast.makeText(SearchActivity.this, "TestList count == " + testList.size(), Toast.LENGTH_SHORT).show();
                    UsersSearchListAdapter adapter = (UsersSearchListAdapter) recyclerView.getAdapter();
                    adapter.UpdateList(testList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                notificationService = ((NotificationService.NotificationServiceBinder) binder).getService();
                notificationService.AddFirebaseListener(listener, "guid");
            }

            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(SearchActivity.this, "Service disconnected" + testList.size(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, serviceConnection, 0);
    }
}
