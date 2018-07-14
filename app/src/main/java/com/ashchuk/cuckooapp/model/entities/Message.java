package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Message {

    public Message(){ }

    @PrimaryKey
    public long id;
    public String creatorId;
    public String userId;
    public String messageText;
    public Date creationDate;
}
