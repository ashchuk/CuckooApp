package com.ashchuk.cuckooapp.mvp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.repositories.UserRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<User>> users;
    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers();
        }

        return users;
    }

    private void loadUsers() {
        UserRepository.getUsers()
                .subscribe(result -> users.setValue(result));
    }
}