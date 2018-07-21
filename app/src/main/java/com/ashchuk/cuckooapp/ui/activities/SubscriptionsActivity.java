package com.ashchuk.cuckooapp.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityCreator;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.presenters.SubscriptionsActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.ashchuk.cuckooapp.services.NotificationService;
import com.ashchuk.cuckooapp.ui.adapters.SubscriptionsAdapter;
import com.ashchuk.cuckooapp.ui.helpers.SwipeToDeleteCallback;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionsActivity
        extends MvpAppCompatActivity
        implements ISubscriptionsActivityView,
        NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    SubscriptionsActivityPresenter subscriptionsActivityPresenter;

    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<AuthUI.IdpConfig> mAuthProviders = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    private NotificationService notificationService;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                onSignedInInitialize(user.getDisplayName());
                Intent service = new Intent(this, NotificationService.class);
                startService(service);
            } else {
                onSignedOutCleanup();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.logo)
                                .setAvailableProviders(mAuthProviders)
                                .build(),
                        RC_SIGN_IN);
            }
        };

        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                notificationService = ((NotificationService.NotificationServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        };

        List<Subscription> list = new ArrayList<>(Arrays
                .asList(new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription()));

        RecyclerView recyclerView = findViewById(R.id.subscriptions_list);
        recyclerView.setAdapter(new SubscriptionsAdapter(list,
                (v, subscription) -> {
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(SubscriptionsActivity.this));

        SwipeToDeleteCallback handler = new SwipeToDeleteCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                SubscriptionsAdapter adapter = (SubscriptionsAdapter) recyclerView.getAdapter();
                adapter.removeAt(viewHolder.getAdapterPosition());
                super.onSwiped(viewHolder, direction);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(handler);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, serviceConnection, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                User user = FirebaseUserToUserConverter
                        .convert(FirebaseAuth.getInstance().getCurrentUser());

                UserRepository.insertUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            Toast.makeText(this,
                                    "User successfully inserted into DB",
                                    Toast.LENGTH_SHORT).show();
                        });

                UserRepository.getUsers()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(users -> {
                            Toast.makeText(this,
                                    "There is " +
                                            Integer.toString(users.size()) +
                                            " users in DB", Toast.LENGTH_SHORT).show();
                        });


                Toast.makeText(this, "Welcome back, " + user.DisplayName, Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscriptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.add_item:
                notificationService.createNewNotification();

                FirebaseUserEntityCreator
                        .create(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(entity -> mUsersDatabaseReference.push().setValue(entity));


                // get user by id
//                Query query = mUsersDatabaseReference.orderByChild("id").equalTo(1);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            // dataSnapshot is the "issue" node with all children with id 0
//                            for (DataSnapshot user : dataSnapshot.getChildren()) {
//                                Object test = user.getValue();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();

        if (id == R.id.nav_account) {
            intent.setClass(this, AccountActivity.class);
        } else if (id == R.id.nav_search) {
            intent.setClass(this, SearchActivity.class);
        } else if (id == R.id.nav_todos) {
            intent.setClass(this, TodoListActivity.class);
        } else if (id == R.id.nav_subscriptions) {
            intent.setClass(this, SubscriptionsActivity.class);
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this);
        }

        if (intent.getComponent() != null)
            startActivity(intent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onSignedInInitialize(String username) {
        //attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        //detachDatabaseReadListener();
    }

}
