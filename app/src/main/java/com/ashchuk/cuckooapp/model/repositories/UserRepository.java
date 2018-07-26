package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserRepository {

    public static Observable<List<User>> getUsers() {
        return CuckooApp.getDatabase().userDAO().getUsers()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }


    public static Observable<User> getUserByUserId(String userId) {
        return CuckooApp.getDatabase().userDAO().getUserByUuid(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertUser(User user) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase().userDAO().insert(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
