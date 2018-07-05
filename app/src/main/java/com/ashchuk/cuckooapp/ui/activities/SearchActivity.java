package com.ashchuk.cuckooapp.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.mvp.presenters.SearchActivityPresenter;
import com.ashchuk.cuckooapp.mvp.views.ISearchActivityView;

public class SearchActivity
        extends MvpAppCompatActivity
        implements ISearchActivityView {

    @InjectPresenter
    SearchActivityPresenter searchActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
