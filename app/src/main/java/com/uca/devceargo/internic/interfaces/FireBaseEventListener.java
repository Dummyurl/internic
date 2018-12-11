package com.uca.devceargo.internic.interfaces;

import android.app.ProgressDialog;
import android.net.Uri;

public interface FireBaseEventListener {
    /**
     * Definition of methods to upload images based on fire
     */
    void uploadPicture(Uri uri, ProgressDialog progressDialog);
    void uploadPictures();
}
