package com.ashchuk.cuckooapp.di.modules;

import com.ashchuk.cuckooapp.api.ICuckooAppApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {RetrofitModule.class})
public class ApiModule {
    @Provides
    @Singleton
    public ICuckooAppApi provideIBakingAppAPI(Retrofit retrofit) { return retrofit.create(ICuckooAppApi.class); }
}