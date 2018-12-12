package com.uca.devceargo.internic.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.MediaLoader;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.entities.AccessToken;
import com.uca.devceargo.internic.entities.User;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UserRegisterActivity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    StorageReference reference;
    private int day, month, year;
    private String date;
    private EditText userName;
    private EditText fullName;
    private EditText userEmail;
    private EditText passwordRegister;
    private EditText birthday;
    private CircleImageView profileImage;
    private Uri uri ;
    private Button buttonRegister;
    private Activity activity;
    Button buttonBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        initViews();
        initActions();
    }

    private void initActions() {
        profileImage.setOnClickListener(view -> loadImage());

        buttonRegister.setOnClickListener(view ->{
            if(uri != null){
                uploadPicture();
            }else {
                userRegister();
            }
        });

        buttonBirthday.setOnClickListener(view -> initCalendar());
    }

    private void initCalendar() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, monthOfYear, dayOfMonth) -> {
            date = year+"-"+(monthOfYear)+"-"+dayOfMonth;
            birthday.setText(date);
        },day,month,year);
        datePickerDialog.show();
    }



    public void initViews(){
        mStorage = FirebaseStorage.getInstance();
        reference = mStorage.getReference();

        userName = findViewById(R.id.user_name_register);
        fullName = findViewById(R.id.full_name_register);
        userEmail = findViewById(R.id.user_email_register);
        birthday = findViewById(R.id.birthday_register);
        passwordRegister = findViewById(R.id.user_password_register);
        buttonRegister = findViewById(R.id.button_register);
        buttonBirthday = findViewById(R.id.birthday_button);
        profileImage = findViewById(R.id.user_register_profile_image);
        activity = this;

        Album.initialize(AlbumConfig.newBuilder(getApplicationContext())
                .setAlbumLoader(new MediaLoader())
                .build());
    }

    private void userRegister(){
        User user = new User();
        user.setUserName(userName.getText().toString());
        user.setEmail(userEmail.getText().toString());
        user.setPassword(passwordRegister.getText().toString());
        user.setFullName(fullName.getText().toString());
        user.setBirthDate(new LocalDate().getDateISOformat(birthday.getText().toString()));
        if(uri != null) {
            user.setUrlImage(uri.toString());
        }else{
            user.setUrlImage(null);
        }
        user.setEmailVerified(false);
        user.setTypeUserID(1);
        user.setTtl(1111);



        Call<User> call = Api.instance().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,@NonNull Response<User> response) {
                if(response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Bienvenido :)", Toast.LENGTH_SHORT).show();
                    response.body().setPassword(passwordRegister.getText().toString());
                    loginRequest(response.body());
                }else{
                    Toast.makeText(getApplicationContext(), "Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call,@NonNull Throwable t) {
                System.out.println("onFailure "+t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadImage(){
        Album.image(this)
                .singleChoice()
                .columnCount(2)
                .onResult(result -> {
                    Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(profileImage);
                    uri = Uri.parse(result.get(0).getPath());
                })
                .onCancel(result -> {
                })
                .start();

    }

    private void uploadPicture() {
        Uri file = Uri.fromFile(new File(String.valueOf(uri)));
        StorageReference riversRef = reference .child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return riversRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uri = task.getResult();
                userRegister();

            } else {
                Toast.makeText(getApplicationContext(), "No se pudieron subir la imagen ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginRequest(User userReg) {
        User user = new User();
        user.setEmail(userReg.getEmail());
        user.setPassword(userReg.getPassword());
        // create call
        Call<AccessToken> call = Api.instance().login(user);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call,@NonNull final Response<AccessToken> response) {
                if (response.body() != null) {
                    activity.finish();
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                }else{
                    sendMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable throwable) {
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
            }
        });

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
