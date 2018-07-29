package com.ashchuk.cuckooapp.infrastructure;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final static String CUCKOO_APP_ENDPOINT = "localhost";
    public final static String USERS_FIREBASE_REFERENCE_NAME = "users";
    public final static String GUID_FIREBASE_KEY_NAME = "Guid";
    public final static String EMAIL_FIREBASE_KEY_NAME = "Email";
    public final static String USER_GUID_FLAG = "com.ashchuk.cuckooapp.infrastructure.USER_GUID";
    public static final String USER_STATUS_FLAG = "com.ashchuk.cuckooapp.infrastructure.USER_STATUS";

    public static List<AuthUI.IdpConfig> AUTH_PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());
}
