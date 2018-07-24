package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.net.Uri;

import com.ashchuk.cuckooapp.model.enums.UserStatus;

import io.reactivex.annotations.NonNull;

@Entity
public class User {

    public User(){ }

    @PrimaryKey
    @android.support.annotation.NonNull
    public String Guid;
    public String DisplayName;
    public String Email;
    public UserStatus Status;
    public String PhoneNumber;
    public String Gps;
}
