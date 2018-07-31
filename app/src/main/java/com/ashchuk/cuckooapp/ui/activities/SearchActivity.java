package com.ashchuk.cuckooapp.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityToUserConverter;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.mvp.presenters.SearchActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ISearchActivityView;
import com.ashchuk.cuckooapp.services.FirebaseQueryService;
import com.ashchuk.cuckooapp.ui.adapters.UsersSearchListAdapter;
import com.ashchuk.cuckooapp.ui.helpers.SwipeToAddCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity
        extends MvpAppCompatActivity
        implements ISearchActivityView {

    @InjectPresenter
    SearchActivityPresenter searchActivityPresenter;

    private FirebaseQueryService queryService;
    private ServiceConnection serviceConnection;

    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.users_list);
        recyclerView.setAdapter(new UsersSearchListAdapter(users));
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SwipeToAddCallback handler = new SwipeToAddCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                UsersSearchListAdapter adapter = (UsersSearchListAdapter)
                        recyclerView.getAdapter();
                SearchActivityPresenter.InsertSubscriptionIntoDb(SearchActivity.this,
                        adapter.createSubscription(viewHolder.getAdapterPosition()));
                adapter.removeAt(viewHolder.getAdapterPosition());
                super.onSwiped(viewHolder, direction);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(handler);
        itemTouchHelper
                .attachToRecyclerView(recyclerView);


        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    users.clear();
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        FirebaseUserEntity result = user.getValue(FirebaseUserEntity.class);

                        if (result == null)
                            continue;

                        if (!result.Guid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            users.add(FirebaseUserEntityToUserConverter
                                    .convert(Objects.requireNonNull(result)));
                    }
                    UsersSearchListAdapter adapter = (UsersSearchListAdapter) recyclerView.getAdapter();

                    adapter.UpdateList(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                queryService = ((FirebaseQueryService.FirebaseQueryServiceBinder) binder).getService();
                queryService.AddGetAllUsersListener(listener);
            }

            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(SearchActivity.this, "Service disconnected" + users.size(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FirebaseQueryService.class);
        startService(intent);
        bindService(intent, serviceConnection, 0);
    }
}
