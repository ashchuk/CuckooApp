package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.ashchuk.cuckooapp.model.enums.UserStatus;

import java.util.Date;

import android.support.annotation.NonNull;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    public String Guid;
    public String DisplayName;
    public String Email;
    public UserStatus Status;
    public String PhoneNumber;
    public String Gps;
    public Date LastUpdateDate;
}
