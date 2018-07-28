package com.ashchuk.cuckooapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.databinding.ActivitySubscriptionsBinding;
import com.ashchuk.cuckooapp.databinding.ContentSubscriptionsBinding;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.presenters.SubscriptionsActivityPresenter;
import com.ashchuk.cuckooapp.mvp.viewmodels.UsersViewModel;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.ashchuk.cuckooapp.services.FirebaseUpdateService;
import com.ashchuk.cuckooapp.services.NotificationService;
import com.ashchuk.cuckooapp.ui.adapters.SubscriptionsListAdapter;
import com.ashchuk.cuckooapp.ui.helpers.SwipeToDeleteCallback;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ashchuk.cuckooapp.infrastructure.Constants.AUTH_PROVIDERS;
import static com.ashchuk.cuckooapp.infrastructure.Constants.USER_STATUS_FLAG;

public class SubscriptionsActivity
        extends MvpAppCompatActivity
        implements ISubscriptionsActivityView,
        NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    SubscriptionsActivityPresenter subscriptionsActivityPresenter;

    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySubscriptionsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_subscriptions);

        setSupportActionBar(binding.includeAppBarSubscriptions.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.includeAppBarSubscriptions.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        List<Subscription> list = new ArrayList<>(Arrays
                .asList(new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription(),
                        new Subscription(), new Subscription(), new Subscription()));

        binding.includeAppBarSubscriptions
                .includeContentSubscriptions
                .subscriptionsList.setAdapter(new SubscriptionsListAdapter(list,
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

        UsersViewModel model = ViewModelProviders.of(this).get(UsersViewModel.class);
        model.getUsers().observe(this, users ->
                Toast.makeText(this, Integer
                                .toString(users.size()),
                        Toast.LENGTH_SHORT).show());

//        FirebaseUpdateService.updateUserMessage(this,
//                FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                "8160fd9f-a3e8-4eb5-b78c-58dde56b2793",
//                "wololo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
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

                User user = SubscriptionsActivityPresenter
                        .InsertUserIntoDb(FirebaseAuth.getInstance().getCurrentUser());

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
