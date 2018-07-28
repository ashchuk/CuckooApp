package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.enums.UserStatus;

public class UserStatusToStringConverter {
    public static String toString(int value) {

        UserStatus currentStatus = UserStatus.valueOf(value);

        switch (currentStatus) {
            case HOME:
                return CuckooApp.getAppComponent().getContext().getString(R.string.home_label);
            case WORK:
                return CuckooApp.getAppComponent().getContext().getString(R.string.on_work_label);
            case WALK:
                return CuckooApp.getAppComponent().getContext().getString(R.string.walk_label);
            case DRIVE:
                return CuckooApp.getAppComponent().getContext().getString(R.string.drive_label);
            case LUNCH:
                return CuckooApp.getAppComponent().getContext().getString(R.string.lunch_label);
            case SLEEP:
                return CuckooApp.getAppComponent().getContext().getString(R.string.sleep_label);
            default:
                return "";
        }
    }
}
