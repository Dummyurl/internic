package com.uca.devceargo.internic.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.MediaLoader;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.classes.CreateCooperative;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.User;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.io.File;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooperativeRegister extends AppCompatActivity {

    private ViewSwitcher viewSwitcher;
    private int navigationView = 0;
    private CircleImageView profile;
    private ImageView cover;
    private EditText name;
    private EditText fullName;
    private EditText description;
    private EditText number;
    private Spinner contactType;
    private Button nextButton;
    private Uri uriProfile;
    private Uri uriCover;
    private Uri uri;
    //Dialog
    AlertDialog dialog;
    View view;
    LayoutInflater layoutInflater;
    TextView uploadingText;

    // Users
    private Button createUser;
    private CreateCooperative register;
    private FirebaseStorage mStorage;
    StorageReference reference;
    private int day, month, year;
    private String date;
    private EditText userName;
    private EditText userLongName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userBirthday;
    private CircleImageView userProfile;
    private Uri userUriImage ;
    Button buttonBirthday;
    private int images;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperative_register);

        initViews();
    }

    private void initViews(){
        Album.initialize(AlbumConfig.newBuilder(getApplicationContext())
                .setAlbumLoader(new MediaLoader())
                .build());
        register = new CreateCooperative(this);
        profile = findViewById(R.id.cooperative_register_profile_image);
        cover = findViewById(R.id.cooperative_register_cover_image);
        name = findViewById(R.id.cooperative_register_name);
        description = findViewById(R.id.cooperative_register_description);
        number = findViewById(R.id.cooperative_register_contact);
        contactType = findViewById(R.id.cooperative_register_contact_type);
        fullName = findViewById(R.id.cooperative_register_full_name);
        viewSwitcher = findViewById(R.id.view_switcher_cooperative);
        nextButton = findViewById(R.id.cooperative_register_next_button);

        //User
        userProfile = findViewById(R.id.user_register_profile_image);
        createUser = findViewById(R.id.button_register);

        mStorage = FirebaseStorage.getInstance();
        reference = mStorage.getReference();

        userName = findViewById(R.id.user_name_register);
        userLongName = findViewById(R.id.full_name_register);
        userEmail = findViewById(R.id.user_email_register);
        userBirthday = findViewById(R.id.birthday_register);
        userPassword = findViewById(R.id.user_password_register);
        buttonBirthday = findViewById(R.id.birthday_button);
        //dialog
        layoutInflater = LayoutInflater.from(getApplicationContext());
        view = layoutInflater.inflate(R.layout.dialog_uploading_images, null);
        uploadingText = view.findViewById(R.id.text_uploading);

        dialog = buildDialog();
        initActions();
    }

    private void initActions(){

        profile.setOnClickListener(view -> loadImage(1));

        cover.setOnClickListener(view -> loadImage(2));

        userProfile.setOnClickListener(View -> loadImage(3));

        nextButton.setOnClickListener((View v) -> {
            navigationView = 1;
            viewSwitcher.showNext();
        });

        createUser.setOnClickListener(view1 -> {
            createUser.setEnabled(false);

            if(countImages() == 0){
                registerCooperative();
                Toast.makeText(getApplicationContext(), "Registrando...", Toast.LENGTH_SHORT).show();
            }else {
                selectImagesToUpload();
                dialog.setView(view);
                dialog.show();
                uploadingText.setText("Preparando todo, espere...");

            }
        });

        buttonBirthday.setOnClickListener(view -> initCalendar());
    }

    private void selectImagesToUpload(){
        if(uriProfile != null){
            uploadPicture(uriProfile, 1);

        }
        if(uriCover != null){
            uploadPicture(uriCover, 2);

        }
        if(userUriImage != null){
            uploadPicture(userUriImage, 3);
        }
    }

    private int countImages(){
        images = 0;
        if(uriProfile != null){images = images+1;}
        if(uriCover != null){images = images+1;}
        if(userUriImage != null){images = images+1;}
        return images;
    }

    private void initCalendar() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, monthOfYear, dayOfMonth) -> {
            date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
            userBirthday.setText(date);
        },day,month,year);
        datePickerDialog.show();
    }


    private void loadImage(int typeImage){
        Album.image(this)
                .singleChoice()
                .columnCount(2)
                .onResult(result -> {
                    if(typeImage == 1) {
                        Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(profile);
                        uriProfile = Uri.parse(result.get(0).getPath());
                    }else if(typeImage == 2) {
                        Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(cover);
                        uriCover = Uri.parse(result.get(0).getPath());
                    }else if(typeImage == 3){
                        Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(userProfile);
                        userUriImage = Uri.parse(result.get(0).getPath());
                    }
                })
                .onCancel(result -> {
                })
                .start();
    }

    private void uploadPicture(Uri url, int typeImage) {
        Uri file = Uri.fromFile(new File(String.valueOf(url)));
        StorageReference riversRef = reference .child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(exception -> {
        }).addOnSuccessListener(taskSnapshot -> {
            uri =  taskSnapshot.getUploadSessionUri();
            if(typeImage == 1)
                uriProfile = uri;
            if(typeImage == 2)
                uriCover = uri;
            if(typeImage == 3)
                userUriImage = uri;
            uploadingText.setText(this.getString(R.string.uploading_images_dialog, images));
            images = images -1;
            if(images == 0){
                uploadingText.setText(this.getString(R.string.uploading_images_dialog, 0));
                registerCooperative();
                dialog.dismiss();


            }
        });
    }


    public void registerCooperative(){
        Cooperative cooperative = new Cooperative();
        cooperative.setDescription(description.getText().toString());
        cooperative.setFullName(fullName.getText().toString());
        cooperative.setName(name.getText().toString());
        cooperative.setLocationID(1);
        cooperative.setQualification("1");

        if(uriCover != null) {
            cooperative.setUrlCoverImage(uriCover.toString());
        }else{
            cooperative.setUrlCoverImage(null);
        }

        if(uriProfile != null) {
            cooperative.setUrlShield(uriProfile.toString());
        }else{
            cooperative.setUrlShield(null);
        }

        cooperative.setContactType(contactType.getSelectedItemPosition());
        cooperative.setContactNumber(number.getText().toString());
        //cooperative.setUserType(1);

        registerUser(cooperative);


    }

    private void registerUser(Cooperative cooperative){
        User user = new User();
        user.setUserName(userName.getText().toString());
        user.setEmail(userEmail.getText().toString());
        user.setPassword(userPassword.getText().toString());
        user.setFullName(userLongName.getText().toString());
        user.setBirthDate(/*birthday.getText().toString()*/null);
        if(userUriImage != null) {
            user.setUrlImage(userUriImage.toString());
        }else{
            user.setUrlImage(null);
        }
        user.setEmailVerified(false);
        user.setTypeUserID(2);
        register.userRegister(user, cooperative);

        createUser.setEnabled(true);

    }

    private AlertDialog buildDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if(view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        return builder.create();
    }



    @Override
    public void onBackPressed() {
        if(navigationView == 0 || navigationView== 2){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }else {
            if (navigationView == 1){
                viewSwitcher.showPrevious();
                navigationView =2;
            }
        }
    }


}
