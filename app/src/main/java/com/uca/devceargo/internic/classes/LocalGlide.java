package com.uca.devceargo.internic.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class LocalGlide {
    public static final int CENTER_CROP = 1;
    public static final int CENTER_INSIDE = 2;
    public static final int CIRCLE_CROP = 3;
    public static final int FIT_CENTER = 4;
    private static final int DEFAULT_NUMBER = 0;
    private int errorImageID;
    private int backgroundImageID;
    private RequestOptions requestOptions;

    public LocalGlide() {
        errorImageID = 0;
        backgroundImageID = 0;
    }

    @SuppressLint("CheckResult")
    private void loadImage(String url, ImageView imageView){

    }

    @SuppressLint("CheckResult")
    private void defineRequiestOption(int opc){
        requestOptions = new RequestOptions();

        if(errorImageID != DEFAULT_NUMBER){
            requestOptions.error(errorImageID);
        }else if(backgroundImageID != DEFAULT_NUMBER){
            requestOptions.placeholder(backgroundImageID);
        }

        switch (opc){
            case CENTER_CROP:
                requestOptions.centerCrop();
                break;
            case CIRCLE_CROP:
                requestOptions.circleCrop();
                break;
            case CENTER_INSIDE:
                requestOptions.centerInside();
            case FIT_CENTER:
                requestOptions.fitCenter();
                break;
        }
    }

    @SuppressLint("CheckResult")
    public void loadImage(ImageView imageView, String url, int opc){
        defineRequiestOption(opc);
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
    }

    @SuppressLint("CheckResult")
    public void loadImageCenterCrop(ImageView imageView, String url,Context context){
        defineRequiestOption(CENTER_CROP);
        CircularProgressDrawable placeHolder = new CircularProgressDrawable(context);
        placeHolder.setStrokeWidth(5f);
        placeHolder.setCenterRadius(30f);
        placeHolder.start();
        requestOptions.placeholder(placeHolder);

        Glide.with(imageView.getContext()).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        placeHolder.stop();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        placeHolder.stop();
                        return false;
                    }
                }).apply(requestOptions).into(imageView);
    }

    public int getErrorImageID() {
        return errorImageID;
    }

    public void setErrorImageID(int errorImageID) {
        this.errorImageID = errorImageID;
    }

    public int getBackgroundImageID() {
        return backgroundImageID;
    }

    public void setBackgroundImageID(int backgroundImageID) {
        this.backgroundImageID = backgroundImageID;
    }
}
