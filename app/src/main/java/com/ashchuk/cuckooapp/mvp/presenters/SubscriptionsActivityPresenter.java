package com.ashchuk.cuckooapp.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityCreator;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SubscriptionsActivityPresenter extends MvpPresenter<ISubscriptionsActivityView> {

    public SubscriptionsActivityPresenter() {
        CuckooApp.getAppComponent().inject(this);
        getViewState().CheckAuth();
    }

    public static User InsertUserIntoDb(FirebaseUser firebaseUser) {
        User user = FirebaseUserToUserConverter
                .convert(firebaseUser);

        UserRepository.insertUser(user)
                .switchMap(data -> UpdateFirebaseUser())
                .subscribeOn(Schedulers.io())
                .subscribe((userEntity) -> FirebaseDatabase.getInstance()
                        .getReference().child("users")
                        .push().setValue(userEntity));

        return user;
    }

    public static Observable<FirebaseUserEntity> UpdateFirebaseUser() {
        return FirebaseUserEntityCreator
                .create(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
