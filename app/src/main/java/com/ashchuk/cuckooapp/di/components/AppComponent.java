package com.ashchuk.cuckooapp.di.components;

import android.content.Context;

import com.ashchuk.cuckooapp.di.modules.ContextModule;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class})
public interface AppComponent {
    Context getContext();
//    CuckooAppService getCuckooAppService();

//    void inject(LoginPresenter loginPresenter);
//    void inject(SubscriptionsPresenter subscriptionsPresenter);
//    void inject(TodosPresenter todosPresenter);
//    void inject(AccountPresenter accountPresenter);
}
