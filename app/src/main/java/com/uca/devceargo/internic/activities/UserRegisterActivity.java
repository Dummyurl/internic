package com.uca.devceargo.internic.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uca.devceargo.internic.MediaLoader;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.User;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {

    public static final int GALLERY_INTENT = 10;
    private int day, month, year;
    private String date;
    private String typeUserId;
    private EditText userName;
    private EditText fullName;
    private EditText userEmail;
    private EditText passwordRegister;
    private EditText birthday;
    private EditText contactNumber;
    private EditText contactType;
    private CircleImageView profileImage;
    private StorageReference mStorage;
    private Uri uri ;
    //private Button buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        Button buttonBirthday;
        initViews();

        userName = findViewById(R.id.user_name_register);
        fullName = findViewById(R.id.full_name_register);
        userEmail = findViewById(R.id.user_email_register);
        birthday = findViewById(R.id.birthday_register);
        passwordRegister = findViewById(R.id.user_password_register);
        contactNumber = findViewById(R.id.number_contact_register);
        contactType = findViewById(R.id.contact_type_register);
        //buttonRegister = findViewById(R.id.button_register);
        buttonBirthday = findViewById(R.id.birthday_button);
        profileImage = findViewById(R.id.user_register_profile_image);

        mStorage = FirebaseStorage.getInstance().getReference();

        profileImage.setOnClickListener(view -> loadImage());

        buttonBirthday.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                    birthday.setText(date);
                }
            },day,month,year);
            datePickerDialog.show();
        });
    }

    public void initViews(){
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
            user.setBirthDate(birthday.getText().toString());
            user.setUrlImage(uri.toString());
            user.setTypeUserID(getIntent().getExtras().getInt("typeUserId"));

            Call<List<User>> call = Api.instance().createUser(user);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(@NonNull Call<List<User>> call,@NonNull Response<List<User>> response) {
                    Toast.makeText(getApplicationContext(),"Se ha guardado el usuario",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<List<User>> call,@NonNull Throwable t) {

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

    private void loadImage(){


        Album.image(this)
                .singleChoice()
                .columnCount(2)
                //.selectCount(4)
                .onResult(result -> {
                    Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(profileImage);
                    System.out.println("Hola3 "+result.get(0).getPath());
                    System.out.println("Hola4 "+result.get(0).getBucketName());
                })
                .onCancel(result -> {
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            if (data != null)
            uri = data.getData();
            profileImage.setImageURI(uri);
            assert uri.getLastPathSegment() != null;
            StorageReference filePath = mStorage.child("images").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(taskSnapshot ->
                    uri = taskSnapshot.getUploadSessionUri());
            Toast.makeText(getApplicationContext(), "Subida a firebase", Toast.LENGTH_LONG).show();
        }

    }
}
