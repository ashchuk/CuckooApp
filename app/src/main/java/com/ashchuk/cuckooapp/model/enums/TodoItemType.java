package com.ashchuk.cuckooapp.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum TodoItemType {
    ASSIGNED(1), TODO(2);

    private int value;
    private static Map map = new HashMap<>();

    TodoItemType(int value) {
        this.value = value;
    }

    static {
        for (TodoItemType todoItemType : TodoItemType.values()) {
            map.put(todoItemType.value, todoItemType);
        }
    }

    public static TodoItemType valueOf(int pageType) {
        return (TodoItemType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}