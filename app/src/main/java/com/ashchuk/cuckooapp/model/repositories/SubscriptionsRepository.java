package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionsRepository {

    public static Observable<List<Subscription>> getSubscriptions() {
        return CuckooApp.getDatabase().subscriptionDAO().getSubscriptions()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<List<Subscription>> getSubscriptionByUserId(String userId) {
        return CuckooApp.getDatabase().subscriptionDAO().getSubscriptionsByUserId(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }
    public static Observable<Subscription> getSubscriptionById(String subscriptionId) {
        return CuckooApp.getDatabase().subscriptionDAO().getSubscriptionById(subscriptionId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertSubscription(Subscription subscription) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase()
                        .subscriptionDAO()
                        .insert(subscription))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
