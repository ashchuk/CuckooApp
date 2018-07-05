package com.ashchuk.cuckooapp.di.components;

import android.content.Context;

import com.ashchuk.cuckooapp.di.modules.ContextModule;
import com.ashchuk.cuckooapp.mvp.presenters.AccountActivityPresenter;
import com.ashchuk.cuckooapp.mvp.presenters.LoginActivityPresenter;
import com.ashchuk.cuckooapp.mvp.presenters.SearchActivityPresenter;
import com.ashchuk.cuckooapp.mvp.presenters.SubscriptionsActivityPresenter;
import com.ashchuk.cuckooapp.mvp.presenters.TodoListActivityPresenter;
import com.ashchuk.cuckooapp.mvp.presenters.UserDetailActivityPresenter;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class})
public interface AppComponent {
    Context getContext();
//    CuckooAppService getCuckooAppService();

    void inject(LoginActivityPresenter loginActivityPresenter);
    void inject(SubscriptionsActivityPresenter subscriptionsActivityPresenter);
    void inject(TodoListActivityPresenter todoListActivityPresenter);
    void inject(AccountActivityPresenter accountActivityPresenter);
    void inject(UserDetailActivityPresenter userDetailActivityPresenter);
    void inject(SearchActivityPresenter searchActivityPresenter);
}
