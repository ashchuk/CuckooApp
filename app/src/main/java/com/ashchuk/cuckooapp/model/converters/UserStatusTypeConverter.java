package com.ashchuk.cuckooapp.model.converters;

import android.arch.persistence.room.TypeConverter;

import com.ashchuk.cuckooapp.model.enums.UserStatus;

public class UserStatusTypeConverter {
    @TypeConverter
    public static UserStatus toStatus(Integer value) {
        return value == null ? null : UserStatus.values()[value];
    }

    @TypeConverter
    public static Integer toInteger(UserStatus status) {
        return status == null ? null : status.getValue();
    }
}
