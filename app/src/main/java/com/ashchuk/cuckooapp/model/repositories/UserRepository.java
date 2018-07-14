package com.ashchuk.cuckooapp.model.repositories;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class UserRepository {

    public static Observable<List<User>> getUsers() {
        return CuckooApp.getDatabase().userDAO().getUsers()
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }


    public static Observable<User> getUserByUserId(String userId) {
        return CuckooApp.getDatabase().userDAO().getUserByUuid(userId)
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS);
    }

    public static Observable<Integer> insertUser(User user) {
        return Observable
                .fromCallable(() -> (int) CuckooApp.getDatabase().userDAO().insert(user));
    }

}
