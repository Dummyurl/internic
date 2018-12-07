package com.uca.devceargo.internic;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tumblr.remember.Remember;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Remember.init(getApplicationContext(), "com.uca.apps.isi.taken");
        Fresco.initialize(this);
    }
}