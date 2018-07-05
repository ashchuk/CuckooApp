package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.mvp.views.ILoginActivityView;

public class LoginActivity
        extends MvpAppCompatActivity
        implements ILoginActivityView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
