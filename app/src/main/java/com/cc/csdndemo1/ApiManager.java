package com.cc.csdndemo1;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by HASEE on 2017/6/1 16:53
 */

public class ApiManager {
    private static ApiManager instance;
    private PointApi pointApi;

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public PointApi pointApiService() {
        if (pointApi == null) {
            pointApi = new Retrofit.Builder()
                    .baseUrl("http://172.16.108.22:10004/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PointApi.class);
        }
        return pointApi;
    }
}
