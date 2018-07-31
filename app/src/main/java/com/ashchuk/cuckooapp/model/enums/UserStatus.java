package com.ashchuk.cuckooapp.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserStatus {
    HOME(1), WORK(2), LUNCH(3), DRIVE(4), WALK(5), SLEEP(6);

    private int value;
    private static Map map = new HashMap<>();

    UserStatus(int value) {
        this.value = value;
    }

    static {
        for (UserStatus userStatus : UserStatus.values()) {
            map.put(userStatus.value, userStatus);
        }
    }

    public static UserStatus valueOf(int pageType) {
        return (UserStatus) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
