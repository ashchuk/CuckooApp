package com.ashchuk.cuckooapp.mvp.presenters;

import android.content.Context;

import com.arellomobile.mvp.MvpPresenter;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.mvp.views.ISearchActivityView;
import com.ashchuk.cuckooapp.services.FirebaseUpdateService;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.schedulers.Schedulers;

public class SearchActivityPresenter extends MvpPresenter<ISearchActivityView> {

    public SearchActivityPresenter() {
    }

    public static void InsertSubscriptionIntoDb(Context context, Subscription subscription) {
        SubscriptionsRepository.insertSubscription(subscription)
                .subscribeOn(Schedulers.io())
                .subscribe(integer -> FirebaseUpdateService
                        .addUserSubscription(context, subscription.id,
                                FirebaseAuth.getInstance().getCurrentUser().getUid()));

    }
}
