package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.enums.UserStatus;

public class UserStatusToImgSrcConverter {
    public static int fromUserStatus(UserStatus userStatus) {

        switch (userStatus) {
            case HOME:
                return R.drawable.home_icon;
            case WORK:
                return R.drawable.work_icon;
            case WALK:
                return R.drawable.walk_icon;
            case DRIVE:
                return R.drawable.car_icon;
            case LUNCH:
                return R.drawable.food_icon;
            case SLEEP:
                return R.drawable.sleep_icon;
            default:
                return R.drawable.home_icon;
        }
    }
}