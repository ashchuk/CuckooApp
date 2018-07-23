package com.ashchuk.cuckooapp.infrastructure.helpers;

import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.firebase.FirebaseUserEntity;

public class FirebaseUserEntityToUserConverter {

    public static User convert(FirebaseUserEntity firebaseUserEntity){

        User user = new User();
        user.Guid = firebaseUserEntity.Guid;
        user.DisplayName = firebaseUserEntity.DisplayName;
        user.Email = firebaseUserEntity.Email;
        user.PhoneNumber = firebaseUserEntity.PhoneNumber;

        return user;
    }

}
