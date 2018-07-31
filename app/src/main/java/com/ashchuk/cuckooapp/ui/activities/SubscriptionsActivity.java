package com.ashchuk.cuckooapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.databinding.ActivitySubscriptionsBinding;
import com.ashchuk.cuckooapp.databinding.ContentSubscriptionsBinding;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityToUserConverter;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.presenters.SubscriptionsActivityPresenter;
import com.ashchuk.cuckooapp.mvp.viewmodels.UsersViewModel;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.ashchuk.cuckooapp.services.FirebaseQueryService;
import com.ashchuk.cuckooapp.services.FirebaseUpdateService;
import com.ashchuk.cuckooapp.services.NotificationService;
import com.ashchuk.cuckooapp.ui.adapters.SubscriptionsListAdapter;
import com.ashchuk.cuckooapp.ui.adapters.UsersSearchListAdapter;
import com.ashchuk.cuckooapp.ui.helpers.SwipeToDeleteCallback;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ashchuk.cuckooapp.infrastructure.Constants.AUTH_PROVIDERS;
import static com.ashchuk.cuckooapp.infrastructure.Constants.USERS_FIREBASE_REFERENCE_NAME;
import static com.ashchuk.cuckooapp.infrastructure.Constants.USER_STATUS_FLAG;

public class SubscriptionsActivity
        extends MvpAppCompatActivity
        implements ISubscriptionsActivityView,
        NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    SubscriptionsActivityPresenter subscriptionsActivityPresenter;

    private FirebaseQueryService queryService;
    private ServiceConnection serviceConnection;

    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fillSubscriptionsList(true);
        }
    };

    private ActivitySubscriptionsBinding binding;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscriptions);

        setSupportActionBar(binding.includeAppBarSubscriptions.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.includeAppBarSubscriptions.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.includeAppBarSubscriptions
                .includeContentSubscriptions
                .subscriptionsList.setAdapter(new SubscriptionsListAdapter(new ArrayList<>(),
                (v, subscription) -> {

                }));

        binding.includeAppBarSubscriptions
                .includeContentSubscriptions
                .subscriptionsList
                .setLayoutManager(new LinearLayoutManager(SubscriptionsActivity.this));


        binding.includeAppBarSubscriptions
                .fabStatusHome.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.HOME,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        binding.includeAppBarSubscriptions
                .fabStatusWork.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.WORK,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        binding.includeAppBarSubscriptions
                .fabStatusWalk.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.WALK,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        binding.includeAppBarSubscriptions
                .fabStatusCar.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.DRIVE,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        binding.includeAppBarSubscriptions
                .fabStatusLunch.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.LUNCH,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        binding.includeAppBarSubscriptions
                .fabStatusSleep.setOnClickListener(view -> FirebaseUpdateService
                .changeUserStatus(this, UserStatus.SLEEP,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fillSubscriptionsList(true);

            UserRepository.getUserByUserId(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).subscribe(user -> {
                        if (user != null) {
                            currentUser = user;
                            TextView tvUsername = binding.navView.getHeaderView(0)
                                    .findViewById(R.id.tv_username);
                            TextView tvEmail = binding.navView.getHeaderView(0)
                                    .findViewById(R.id.tv_email);
                            tvUsername.setText(currentUser.DisplayName);
                            tvEmail.setText(currentUser.Email);
                        }
                    }
            );
        }

        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                queryService = ((FirebaseQueryService.FirebaseQueryServiceBinder) binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    @Override
    public void fillSubscriptionsList(boolean cleanup) {
        UsersViewModel model = ViewModelProviders.of(this).get(UsersViewModel.class);

        if (cleanup)
            model.cleanDataset();

        model.getUserSubscriptions(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .observe(this, subscriptions -> {
                            SubscriptionsListAdapter adapter =
                                    (SubscriptionsListAdapter) binding.includeAppBarSubscriptions
                                            .includeContentSubscriptions
                                            .subscriptionsList.getAdapter();
                            if (subscriptions == null) {
                                Subscription subscriptionFirst = new Subscription();
                                subscriptionFirst.DisplayName = "There is no active subscriptions";
                                subscriptionFirst.status = UserStatus.HOME.getValue();
                                subscriptionFirst.lastUpdateDate = new Date();

                                Subscription subscriptionSecond = new Subscription();
                                subscriptionFirst.DisplayName = "Add some new subscriptions!";
                                subscriptionFirst.status = UserStatus.WALK.getValue();
                                subscriptionSecond.lastUpdateDate = new Date();

                                List<Subscription> dummy = new ArrayList<>(Arrays
                                        .asList(subscriptionFirst, subscriptionSecond));
                                adapter.updateSubscriptions(dummy);
                            }
                            if (subscriptions.size() == 0) {
                                Subscription subscriptionFirst = new Subscription();
                                subscriptionFirst.DisplayName = "There is no active subscriptions";
                                subscriptionFirst.status = UserStatus.HOME.getValue();
                                subscriptionFirst.lastUpdateDate = new Date();

                                Subscription subscriptionSecond = new Subscription();
                                subscriptionSecond.DisplayName = "Add some new subscriptions!";
                                subscriptionSecond.status = UserStatus.WALK.getValue();
                                subscriptionSecond.lastUpdateDate = new Date();

                                List<Subscription> dummy = new ArrayList<>(Arrays
                                        .asList(subscriptionFirst, subscriptionSecond));
                                adapter.updateSubscriptions(dummy);
                            } else {
                                adapter.updateSubscriptions(subscriptions);
                            }
                        }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        LocalBroadcastManager.getInstance(CuckooApp.getAppComponent().getContext())
                .registerReceiver(mMessageReceiver,
                        new IntentFilter(getString(R.string.broadcast_update)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                currentUser = subscriptionsActivityPresenter
                        .InsertUserIntoDb(FirebaseAuth.getInstance().getCurrentUser());

                if (currentUser != null) {
                    TextView tvUsername = binding.navView.getHeaderView(0).findViewById(R.id.tv_username);
                    TextView tvEmail = binding.navView.getHeaderView(0).findViewById(R.id.tv_email);
                    tvUsername.setText(currentUser.DisplayName);
                    tvEmail.setText(currentUser.Email);
                }

                checkFirebaseUserExists(currentUser);
                fillSubscriptionsList(false);

                Toast.makeText(this, "Welcome back, " + currentUser.DisplayName, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void checkFirebaseUserExists(User user) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    return;

                subscriptionsActivityPresenter.GetFirebaseUser()
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(firebaseUserEntity -> FirebaseDatabase.getInstance()
                                .getReference().child(USERS_FIREBASE_REFERENCE_NAME)
                                .push().setValue(firebaseUserEntity));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        queryService.AddGetUserByGuidListener(listener, user.Guid);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();

//        if (id == R.id.nav_account) {
//            intent.setClass(this, AccountActivity.class);
        if (id == R.id.nav_search) {
            intent.setClass(this, SearchActivity.class);
//        } else if (id == R.id.nav_todos) {
//            intent.setClass(this, TodoListActivity.class);
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this);
        }

        if (intent.getComponent() != null)
            startActivity(intent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscriptions, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView =
//                (SearchView) searchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                subscriptionsActivityPresenter
                        .syncUserData(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                queryService);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void CheckAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent service = new Intent(this, NotificationService.class);
                getIntent().putExtra(USER_STATUS_FLAG, user.getUid());
                startService(service);
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.logo)
                                .setAvailableProviders(AUTH_PROVIDERS)
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }
}
