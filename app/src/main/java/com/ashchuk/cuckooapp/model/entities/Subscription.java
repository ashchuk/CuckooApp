package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Subscription {
    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public Integer status;
    public Date lastUpdateDate;
}
