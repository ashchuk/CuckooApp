package com.ashchuk.cuckooapp.model.converters;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriTypeConverter {
    @TypeConverter
    public static Uri toUri(String value) {
        return value == null ? null : Uri.parse(value);
    }

    @TypeConverter
    public static String toString(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
