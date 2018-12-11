package com.uca.devceargo.internic.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.tumblr.remember.Remember;
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
    private ProgressDialog progressDialog;

    public CreateCooperative(Context context) {
        this.context = context;
    }

    public void userRegister(User user, Cooperative cooperative, ProgressDialog progress){
        this.progressDialog = progress;
        this.progressDialog.setMessage("Registrando datos de usuario ...");
        userPassword = user.getPassword();
        Call<User> call = Api.instance().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.body() != null) {
                    Gson gson = new Gson();
                    String userJson = gson.toJson(response.body());
                    Remember.putString("userData", userJson, (Boolean success) -> {
                        if (success) {
                            System.out.println("Éxito al guardar los datos del usuario");
                        }
                    });
                    response.body().setPassword(userPassword);
                    userID = response.body().getId();
                    loginRequest(response.body(), cooperative);

                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                progressDialog.dismiss();
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
            public void onResponse(@NonNull Call<AccessToken> call,@NonNull Response<AccessToken> response) {
                if (response.body() != null) {
                    Remember.putString("accessToken", response.body().getId(), (Boolean success) -> {
                        if (success) {
                            cooperativeRegister(cooperative);
                        }
                    });
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call,@NonNull Throwable throwable) {
                progressDialog.dismiss();
            }
        });

    }

    private void cooperativeRegister(Cooperative cooperative){
        progressDialog.setMessage("Registrando los datos de la cooperativa ...");
        Call<Cooperative> call = Api.instance().postCooperative(cooperative);
        call.enqueue(new Callback<Cooperative>() {
            @Override
            public void onResponse(@NonNull Call<Cooperative> call, @NonNull Response<Cooperative> response) {
                if(response.body() != null){
                    System.out.println("register cooperativa creada");
                    cooperativeID = response.body().getId();
                    userCooperativeRegister();
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Cooperative> call,@NonNull Throwable throwable) {
                progressDialog.dismiss();
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
            public void onResponse(@NonNull Call<UserCooperative> call,@NonNull Response<UserCooperative> response) {
                if(response.body() != null){
                    progressDialog.dismiss();
                    context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserCooperative> call,@NonNull Throwable throwable) {
                progressDialog.dismiss();
            }
        });

    }


}
