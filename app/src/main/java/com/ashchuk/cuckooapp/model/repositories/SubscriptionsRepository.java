package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SubscriptionsRepository {

    public static Observable<List<Subscription>> getSubscriptions() {
        return CuckooApp.getDatabase().subscriptionDAO().getSubscriptions()
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<List<Subscription>> getSubscriptionByUserId(String userId) {
        return CuckooApp.getDatabase().subscriptionDAO().getSubscriptionsByUserId(userId)
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertSubscription(Subscription subscription) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase()
                        .subscriptionDAO()
                        .insert(subscription));
    }

}
