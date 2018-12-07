package com.uca.devceargo.internic.api;

/*
 * Created by Mario Arce on 16/10/2017.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private final static String URL = "https://internic-api.herokuapp.com/api/";
    public static String getBase() {
        return URL;
    }

    public static ApiInterface instance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.getBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        System.out.println("MESSAGE: "+ URL);
        return retrofit.create(ApiInterface.class);
    }

}
