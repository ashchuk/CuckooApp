package com.ashchuk.cuckooapp.mvp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.repositories.SubscriptionsRepository;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<Subscription>> subscriptions;

    public void cleanDataset(){
        subscriptions = null;
    }

    public LiveData<List<Subscription>> getUserSubscriptions(String userGuid) {
        if (subscriptions == null) {
            subscriptions = new MutableLiveData<>();
            loadSubscriptions(userGuid);
        }

        return subscriptions;
    }

    private void loadSubscriptions(String userGuid) {
        SubscriptionsRepository
                .getSubscriptionByUserId(userGuid)
                .subscribe(result -> subscriptions.setValue(result));
//        SubscriptionsRepository
//                .getSubscriptions()
//                .subscribe(result -> subscriptions.setValue(result));
    }
}