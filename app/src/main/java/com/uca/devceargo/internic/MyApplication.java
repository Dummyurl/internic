package com.uca.devceargo.internic;

import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tumblr.remember.Remember;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Remember.init(getApplicationContext(), "com.uca.apps.isi.taken");
        Fresco.initialize(this);
    }
}