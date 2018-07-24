package com.ashchuk.cuckooapp.infrastructure;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final static String CUCKOO_APP_ENDPOINT = "localhost";

    public static List<AuthUI.IdpConfig> AUTH_PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());
}
