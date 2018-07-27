package com.ashchuk.cuckooapp.mvp.presenters;

import android.content.Context;

import com.arellomobile.mvp.MvpPresenter;
import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.infrastructure.helpers.FirebaseUserToUserConverter;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.ashchuk.cuckooapp.mvp.views.ISearchActivityView;
import com.ashchuk.cuckooapp.services.FirebaseUpdateService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.schedulers.Schedulers;

public class SearchActivityPresenter extends MvpPresenter<ISearchActivityView> {

    public SearchActivityPresenter() {
    }

    public static void InsertSubscriptionIntoDb(Context context, Subscription subscription) {
        SubscriptionsRepository.insertSubscription(subscription)
                .subscribeOn(Schedulers.io())
                .subscribe(integer -> FirebaseUpdateService
                        .addUserSubsription(context, subscription.id, subscription.userId));

    }
}
