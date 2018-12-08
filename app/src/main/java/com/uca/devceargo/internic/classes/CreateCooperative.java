package com.uca.devceargo.internic.classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.AccessToken;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.entities.UserCooperative;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCooperative {

    private Context context;
    private int cooperativeID;
    private int userID;
    private String userPassword;

    public CreateCooperative(Context context) {
        this.context = context;
    }

    public void userRegister(User user, Cooperative cooperative){
        userPassword = user.getPassword();
        Call<User> call = Api.instance().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.body() != null) {
                    System.out.println("register éxito al crear el usuario ");
                    response.body().setPassword(userPassword);
                    userID = response.body().getId();
                    loginRequest(response.body(), cooperative);

                }else{
                    System.out.println("register nulos al registar usuario");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                System.out.println("register error al crear el usuario "+t.getMessage());
            }
        });
    }

    private void loginRequest(User userReg, Cooperative cooperative) {

        User user = new User();
        user.setEmail(userReg.getEmail());
        user.setPassword(userReg.getPassword());
        // create call
        Call<AccessToken> call = Api.instance().login(user);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, final Response<AccessToken> response) {
                if (response.body() != null) {
                    System.out.println("register Usuario logeado "+response.body().getId());
                    cooperativeRegister(cooperative);
                }else{
                    System.out.println("register Nulos al logear");
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                System.out.println("register Error al logear "+t.getMessage());
            }
        });

    }

    public void cooperativeRegister(Cooperative cooperative){

        Call<Cooperative> call = Api.instance().postCooperative(cooperative);
        call.enqueue(new Callback<Cooperative>() {
            @Override
            public void onResponse(Call<Cooperative> call, Response<Cooperative> response) {
                if(response.body() != null){
                    System.out.println("register cooperativa creada");
                    cooperativeID = response.body().getId();
                    userCooperativeRegister();
                }else{
                    System.out.println("register nulos al crear la ");
                }
            }

            @Override
            public void onFailure(Call<Cooperative> call, Throwable t) {
                System.out.println("register error al subir la cooperativa ");
            }
        });
    }

    private void userCooperativeRegister(){
        UserCooperative cooperative = new UserCooperative();
        cooperative.setCooperativeID(cooperativeID);
        cooperative.setUserID(userID);

        System.out.println("register id usuario "+userID);
        System.out.println("register id cooperative "+cooperativeID);

        Call<UserCooperative> call = Api.instance().postUserCooperative(cooperative);
        call.enqueue(new Callback<UserCooperative>() {
            @Override
            public void onResponse(Call<UserCooperative> call, Response<UserCooperative> response) {
                if(response.body() != null){
                    context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                }else{
                    System.out.println("register vino nula la userCooperative");
                }
            }

            @Override
            public void onFailure(Call<UserCooperative> call, Throwable t) {
                System.out.println("register error al subir la UserCooperativa ");
            }
        });

    }


}
