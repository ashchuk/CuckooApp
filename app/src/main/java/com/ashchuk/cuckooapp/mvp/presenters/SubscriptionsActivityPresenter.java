package com.ashchuk.cuckooapp.mvp.presenters;

import android.arch.lifecycle.ViewModelProviders;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserEntityCreator;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;
import com.ashchuk.cuckooapp.model.repositories.MessagesRepository;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.TodoItemsRepositiry;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.viewmodels.UsersViewModel;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;
import com.ashchuk.cuckooapp.services.FirebaseQueryService;
import com.ashchuk.cuckooapp.ui.activities.SubscriptionsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SubscriptionsActivityPresenter extends MvpPresenter<ISubscriptionsActivityView> {

    public SubscriptionsActivityPresenter() {
        CuckooApp.getAppComponent().inject(this);
        getViewState().CheckAuth();
    }

    public User InsertUserIntoDb(FirebaseUser firebaseUser) {
        User user = FirebaseUserToUserConverter
                .convert(firebaseUser);

        UserRepository.insertUser(user)
                .subscribeOn(Schedulers.io())
                .subscribe();

        return user;
    }

    public Observable<FirebaseUserEntity> GetFirebaseUser() {
        return FirebaseUserEntityCreator
                .create(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        return FirebaseUserEntityCreator
//                .createDummy(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void syncUserData(String userGuid, FirebaseQueryService queryService) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUserEntity entity = null;
                String key = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    entity = childSnapshot.getValue(FirebaseUserEntity.class);
                }

                if (entity == null || key == null)
                    return;

                Observable.concat(SubscriptionsRepository.insertSubscriptions(entity.Subscriptions),
                        MessagesRepository.insertMessages(entity.Messages),
                        TodoItemsRepositiry.insertTodoItems(entity.Todos))
                        .subscribeOn(Schedulers.io())
                        .subscribe((result) -> getViewState().fillSubscriptionsList(true));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        queryService.AddGetUserByGuidListener(listener, userGuid);
    }

}
