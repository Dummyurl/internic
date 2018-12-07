package com.uca.devceargo.internic.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.*;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.MediaLoader;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.News;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewComplaint extends AppCompatActivity {
    private FirebaseStorage mStorage;
    StorageReference reference;
    ImageView image;
    TextView title;
    TextView description;
    Uri uri;
    boolean withPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_complaint);
        initViews();
        initActions();
    }
    private void initViews(){
        mStorage = FirebaseStorage.getInstance();
        reference = mStorage.getReference();
        image = findViewById(R.id.image_complaint);
        title = findViewById(R.id.new_complaint_title);
        description = findViewById(R.id.new_complaint_description);

        Album.initialize(AlbumConfig.newBuilder(getApplicationContext())
                .setAlbumLoader(new MediaLoader())
                .build());
    }

    private void initActions(){
        image.setOnClickListener(view-> takePicture());
    }

     private  void takePicture(){
         Album.image(this)
                 .singleChoice()
                 .columnCount(2)
                 .onResult(result -> {
                     Glide.with(getApplicationContext()).load(result.get(0).getPath()).into(image);
                     uri = Uri.parse(result.get(0).getPath());
                     withPicture = true;
                 })
                 .onCancel(result -> {
                 })
                 .start();
     }

     private void uploadPicture() {
         Uri file = Uri.fromFile(new File(String.valueOf(uri)));
         StorageReference riversRef = reference .child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

         uploadTask.addOnFailureListener(exception -> {
         }).addOnSuccessListener(taskSnapshot -> {
             //Intent i = new Intent(getApplicationContext(), MainActivity.class);
             //startActivity(i);
         });
     }

     public void uploadNews(){
         News news = new News();
         news.setCooperativeID(4);
         news.setLocationID(1);
         news.setTypeNewID(2);
         news.setDescription(description.getText().toString());
         news.setTitle(title.getText().toString());

         Call<News> call = Api.instance().postNews(news);
         call.enqueue(new Callback<News>() {
             @Override
             public void onResponse(Call<News> call, Response<News> response) {
                 if(response.body() != null) {
                     Toast.makeText(getApplicationContext(), "News arriba", Toast.LENGTH_SHORT).show();
                 }else{
                     //Toast.makeText(getApplicationContext(), "Nulos", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<News> call, Throwable t) {
                 Toast.makeText(getApplicationContext(), "News error", Toast.LENGTH_SHORT).show();
                 System.out.println("onFailure "+t.getMessage());
             }
         });
     }

     private boolean validateFields(){
        boolean isError;
        if(title.getText().toString().isEmpty()) {
            title.setError(this.getString(R.string.empty_fields_message));
            isError = false;
        }else if(description.getText().toString().isEmpty()) {
            description.setError(this.getString(R.string.empty_fields_message));
            isError = false;
        }else{
            isError = true;
        }

         return isError;
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_complaint_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.upload_complaint:
                if(validateFields()){
                    uploadNews();
                    onBackPressed();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
