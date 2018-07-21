package com.ashchuk.cuckooapp.model.converters;

import android.arch.persistence.room.TypeConverter;

import com.ashchuk.cuckooapp.model.enums.TodoItemType;

public class TodoItemTypeConverter {
    @TypeConverter
    public static TodoItemType toTodoType(Integer value) {
        return value == null ? null : TodoItemType.values()[value];
    }

    @TypeConverter
    public static Integer toInteger(TodoItemType todoItemType) {
        return todoItemType == null ? null : todoItemType.getValue();
    }
}
