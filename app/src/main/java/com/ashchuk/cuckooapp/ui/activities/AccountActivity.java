package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.mvp.presenters.AccountActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.IAccountActivityView;

public class AccountActivity
        extends MvpAppCompatActivity
        implements IAccountActivityView {

    @InjectPresenter
    AccountActivityPresenter accountActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }
}
