package com.ashchuk.cuckooapp.api;

import com.ashchuk.cuckooapp.infrastructure.Constants;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ICuckooAppApi {
    @GET(Constants.CUCKOO_APP_ENDPOINT)
    Observable<List<String>> getTodos();
}



