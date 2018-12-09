package com.uca.devceargo.internic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.tumblr.remember.Remember;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.TypeUser;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.adapters.TypeUserAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.AccessToken;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.fragments.TypeUserFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity {
    private static final int FOCUSABLE = 2905;
    private static final int NOT_FOCUSABLE = 5299;
    public static final String USER_ID = "userID";
    private int band;
    private AlertDialog typeUser;
    private RecyclerView recyclerView;
    private LoginActivity activity;
    private ViewSwitcher viewSwitcher;
    private EditText email;
    private EditText password;
    private EditText email2;
    private EditText password2;
    private Button signIn;
    private Button signIn2;
    private Button signUp;
    private Button signUp2;
    private TypeUserFragment typeUserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Full screen in login layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initViews();
        activity = this;
        band = NOT_FOCUSABLE;
        typeUser = buildDialog();

        setFocusEditTexts();
        //Define actions of buttons SIGN IN
        setOnClickListenerSignIn(signIn);
        setOnClickListenerSignIn(signIn2);
        //Define actions of buttons SIGN UP
        setOnClickListenerSignUp(signUp);
        setOnClickListenerSignUp(signUp2);

        KeyboardVisibilityEvent.setEventListener(this, (boolean isOpen) -> {
            if(isOpen){
                viewSwitcher.showNext();
                password.setText(password2.getText().toString());
                email.setText(email2.getText().toString());
                if(band == FOCUSABLE){
                    email2.requestFocus();
                    band = NOT_FOCUSABLE;
                }else if(band == NOT_FOCUSABLE){
                    password2.requestFocus();
                }
            }else{
                email.setText(email2.getText().toString());
                email.setSelection(email2.getSelectionStart());
                password.setText(password2.getText().toString());
                password.setSelection(password2.getSelectionStart());
                viewSwitcher.showPrevious();
            }
        });
    }

    private void initViews(){

        viewSwitcher = findViewById(R.id.view_switcher);
        email = findViewById(R.id.email);
        email2 = findViewById(R.id.email2);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        signIn = findViewById(R.id.sign_in);
        signIn2 = findViewById(R.id.sign_in2);
        signUp = findViewById(R.id.sign_up);
        signUp2 = findViewById(R.id.sign_up2);
    }


    public void setFocusEditTexts(){
        email.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if(hasFocus){
                band = FOCUSABLE;
            }
        });
    }

    private void setOnClickListenerSignIn(final Button signIn){
        signIn.setOnClickListener((View v) ->
            logIn());
    }

    public void setOnClickListenerSignUp(final Button signUp){
        signUp.setOnClickListener((View v) -> {
            getTypeUsers();
            typeUser.show();
            typeUser.setCancelable(false);
        });
    }

    private void logIn(){
        /*if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            if(email2.toString().isEmpty() || password2.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),R.string.empty_fields_message, Toast.LENGTH_LONG).show();
            }else{
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);
                typeUserFragment = showFragment();
                loginRequest(email2.getText().toString(), password2.getText().toString());
            }
        }else{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);
            typeUserFragment = showFragment();
            loginRequest(email.getText().toString(), password.getText().toString());
        }*/
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    private void loginRequest(String email, String pwd) {
        User user = new User();

        String[] emailParts = email.split("@");
        if(emailParts.length > 1){
            user.setEmail(email);
        }else{
            user.setUserName(email);
        }
        user.setPassword(pwd);
        user.setTtl(31556926);

        Call<AccessToken> call = Api.instance().login(user);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.body() != null) {

                    try {
                        Remember.putString("accessToken", response.body().getId(), (Boolean success) -> {
                            if (success) {
                                getUserData(response.body().getUserId());
                            }
                        });
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        if(typeUserFragment != null){
                            typeUserFragment.dismiss();
                        }
                    }
                }else{
                    sendMessageInSnackbar(response.code());
                    Log.i(getString(R.string.message),new ApiMessage().sendMessageOfResponseAPI(response.code(),
                            getApplicationContext()));
                    if(typeUserFragment != null){
                        typeUserFragment.dismiss();
                    }
                    password.setText("");
                    password2.setText("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call,@NonNull Throwable throwable) {
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                Log.e(getString(R.string.error_message_api),throwable.getMessage());
                if(typeUserFragment != null){
                    typeUserFragment.dismiss();
                }
            }
        });

    }

    private void getUserData(int userID){

        Call<User> call = Api.instance().getUser(String.valueOf(userID),
                Remember.getString(getString(R.string.key_access_token),""));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.body() != null){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Gson gson = new Gson();
                    String userJson = gson.toJson(response.body());
                    Remember.putString("userData", userJson, (Boolean success) -> {
                        if (success) {
                            Log.i(getString(R.string.message),"usuario guardado");
                        }
                    });
                    intent.putExtra(USER_ID,response.body());
                    startActivity(intent);
                }else{
                    sendMessageInSnackbar(response.code());
                    Log.i(getString(R.string.message),response.message());
                }
                if(typeUserFragment != null){
                    typeUserFragment.dismiss();
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                Log.e(getString(R.string.error_message_api),throwable.getMessage());
                if(typeUserFragment != null){
                    typeUserFragment.dismiss();
                }
            }
        });
    }

    @SuppressLint("InflateParams")
    private AlertDialog buildDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_types_users, null);

        recyclerView = view.findViewById(R.id.list_types_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getTypeUsers();

        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) -> {

        });

        builder.setView(view);
        builder.setTitle("Tipo de cuenta a registrar");

        return builder.create();
    }

    private TypeUserFragment showFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        TypeUserFragment newFragment = new TypeUserFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
        return newFragment;
    }

    public void showRegisterActivity(int id){
        if(id != 2){
            Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
            intent.putExtra("typeUserId", id);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(), CooperativeRegister.class);
            intent.putExtra("typeUserId", id);
            startActivity(intent);
        }
    }

    private void getTypeUsers(){
        recyclerView.setAdapter(new ProgressAdapter());
        Call<List<TypeUser>> call = Api.instance().getTypesUsers();
        call.enqueue(new Callback<List<TypeUser>>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<List<TypeUser>> call, @NonNull Response<List<TypeUser>> response) {

                if(response.body() != null && response.code() == ApiMessage.CODE_OK){
                    recyclerView.setAdapter(new TypeUserAdapter(response.body(),activity));
                    Log.i(getString(R.string.message),new ApiMessage().sendMessageOfResponseAPI(response.code(),
                            getApplicationContext()));
                }else{
                    sendMessageInSnackbar(response.code());
                    Log.i(getString(R.string.error_message_api),new ApiMessage().sendMessageOfResponseAPI(response.code(),
                            getApplicationContext()));
                }
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onFailure(@NonNull Call<List<TypeUser>> call, @NonNull Throwable throwable) {
                sendMessageInSnackbar(throwable.hashCode());
                Log.i(getString(R.string.message),new ApiMessage().sendMessageOfResponseAPI(throwable.hashCode(),getApplicationContext()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(typeUserFragment != null){
            if(typeUserFragment.isVisible()){
                Toast.makeText(getApplicationContext(),"Espere a que finalice la accion actual", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessageInSnackbar(int code){
        View contextView = findViewById(android.R.id.content);
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getApplicationContext());
        Timber.i(message);
        Snackbar.make(contextView,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}