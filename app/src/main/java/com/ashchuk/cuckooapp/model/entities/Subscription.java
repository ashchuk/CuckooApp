package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.ashchuk.cuckooapp.model.enums.UserStatus;

import java.util.Date;

@Entity
public class Subscription {
    @PrimaryKey
    public long id;
    public User user;
    public UserStatus status;
    public Date lastUpdatedDate;
}
