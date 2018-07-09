package com.ashchuk.cuckooapp.model.converters;

import android.arch.persistence.room.TypeConverter;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.model.entities.User;

public class UserTypeConverter {
    @TypeConverter
    public static User toUser(String uuid) {
        return uuid == null ? null : new User();
    }

    @TypeConverter
    public static String toUuid(User user) {
        return user == null ? null : user.uuid;
    }
}
