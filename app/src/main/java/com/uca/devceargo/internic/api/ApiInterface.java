package com.uca.devceargo.internic.api;

/*
 * Created by Mario Arce on 16/10/2017.
 */
import com.uca.devceargo.internic.entities.AccessToken;
import com.uca.devceargo.internic.entities.Comment;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.News;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.TypeNews;
import com.uca.devceargo.internic.TypeUser;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.entities.UserCoperative;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("typesUsers")
    Call<List<TypeUser>> getTypesUsers();

    @GET("userscooperatives")
    Call<List<UserCoperative>> getUserCooperative(@Query("filter") String filter);

    @GET("Users/{id}")
    Call<User> getUser(@Path("id") String id,  @Header("Authorization") String authorization);

    @GET("typesNews")
    Call<List<TypeNews>> getTypesNews();

    @GET("comments")
    Call<List<Comment>> getCommentUser(@Query("filter") String filter);

    @POST("users")
    Call<List<User>> createUser(@Body User user);

    @POST("Users/login")
    Call<AccessToken> login(@Body User user);

    @POST("news")
    Call<News> postNews(@Body News news);

    @POST("routes/savefullroute")
    Call<Route> postRoute(@Body Route route);
}