package com.ashchuk.cuckooapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.net.Uri;

@Entity
public class User {
    @PrimaryKey
    public long id;
    public String displayName;
    public String uuid;
    public String email;
    public String phoneNumber;
    public Uri photoUri;
//    public Location location;
}
