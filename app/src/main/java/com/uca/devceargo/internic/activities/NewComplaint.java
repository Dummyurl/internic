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
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.MediaLoader;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Comment;
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
    int cooperativeID;
    int commentTypeID;

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

        if(getIntent().getExtras() != null){
            cooperativeID = getIntent().getExtras().getInt("cooperativeID");
            commentTypeID = getIntent().getExtras().getInt("commentTypeID");
        }

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
             Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                 if (!task.isSuccessful()) {
                     throw Objects.requireNonNull(task.getException());
                 }
                 return riversRef.getDownloadUrl();
             }).addOnCompleteListener(task -> {
                 if (task.isSuccessful()) {
                     uri = task.getResult();
                     uploadComplaint();
                 } else {
                     Toast.makeText(getApplicationContext(), "No se pudieron subir la imagen ", Toast.LENGTH_SHORT).show();
                 }
             });
         });
     }


     public void uploadComplaint(){
        Comment comment = new Comment();
        comment.setTitle(title.getText().toString());
        comment.setDescription(description.getText().toString());
        if(uri != null)
            comment.setUrlImage(uri.toString());
        else
            comment.setUrlImage(null);
        comment.setTypeCommentID(commentTypeID);
        comment.setCooperativeID(cooperativeID);
        comment.setUserID(1);

         Call<Comment> call = Api.instance().postComment(comment);
         call.enqueue(new Callback<Comment>() {
             @Override
             public void onResponse(Call<Comment> call, Response<Comment> response) {
                 if(response.body() != null) {
                     Toast.makeText(getApplicationContext(), "Opinión publicada", Toast.LENGTH_SHORT).show();
                 }else{
                     Toast.makeText(getApplicationContext(), "Nulos", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<Comment> call, Throwable t) {
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
                    if(uri != null){
                        uploadPicture();
                    }else{
                        uploadComplaint();
                    }
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
