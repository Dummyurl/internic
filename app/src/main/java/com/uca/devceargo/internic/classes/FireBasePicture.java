package com.uca.devceargo.internic.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.interfaces.FireBaseEventListener;

public class FireBasePicture{
    private FireBaseEventListener mListiner;
    private Context context;
    public FireBasePicture(FireBaseEventListener mListiner, Context context) {
        this.mListiner = mListiner;
        this.context = context;
    }
    public void uploadPicture(Uri file) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.progrees_dialog_firebase_message));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference reference = mStorage.getReference();
        StorageReference riversRef = reference .child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                assert task.getException() != null;
                throw task.getException();
            }
            return riversRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                mListiner.uploadPicture(task.getResult(),progressDialog);
            } else {
                progressDialog.dismiss();
                Toast.makeText(context, "Error al subir la imagen ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}