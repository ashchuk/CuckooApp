package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserToUserConverter {

    public static User convert(FirebaseUser firebaseUser) {

        User user = new User();
        user.Guid = firebaseUser.getUid();
        user.DisplayName = firebaseUser.getDisplayName();
        user.Email = firebaseUser.getEmail();
        user.PhoneNumber = firebaseUser.getPhoneNumber();
        user.Status = UserStatus.HOME;

        return user;
    }

}
