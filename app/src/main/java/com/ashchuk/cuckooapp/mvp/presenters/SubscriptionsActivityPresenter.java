package com.ashchuk.cuckooapp.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityCreator;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SubscriptionsActivityPresenter extends MvpPresenter<ISubscriptionsActivityView> {

    public SubscriptionsActivityPresenter(){
        CuckooApp.getAppComponent().inject(this);
        getViewState().CheckAuth();
    }

    public static User InsertUserIntoDb(FirebaseUser firebaseUser){
        User user = FirebaseUserToUserConverter
                .convert(firebaseUser);

        UserRepository.insertUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return user;
    }

    public static void UpdateFirebaseUser(){
        FirebaseUserEntityCreator
                .create(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entity -> FirebaseDatabase.getInstance()
                        .getReference().child("users").push().setValue(entity));
    }

}
