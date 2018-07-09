package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class TodoItem {
    @PrimaryKey
    public long id;
    public User user;
    public String message;
    public Date creationDate;
    public boolean isDone;
}
