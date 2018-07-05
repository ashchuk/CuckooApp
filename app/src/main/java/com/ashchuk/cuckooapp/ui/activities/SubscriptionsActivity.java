package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.mvp.presenters.SubscriptionsActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ISubscriptionsActivityView;

public class SubscriptionsActivity
        extends MvpAppCompatActivity
        implements ISubscriptionsActivityView {

    @InjectPresenter
    SubscriptionsActivityPresenter subscriptionsActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
    }
}
