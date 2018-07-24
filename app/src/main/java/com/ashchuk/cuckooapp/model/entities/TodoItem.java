package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class TodoItem {

    public TodoItem(){ }

    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public String message;
    public Date creationDate;
    public boolean isDone;
}
