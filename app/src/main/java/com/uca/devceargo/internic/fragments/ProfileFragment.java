package com.uca.devceargo.internic.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.User;
import com.uca.devceargo.internic.entities.UserCoperative;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class ProfileFragment extends Fragment {

    private View fragmentContent;
    private TextView cooperativeLongName;
    private TextView cooperativeShortName;
    private ImageView imageCover;
    private ImageView shield;
    private TextView cooperativeDescription;
    private RatingBar ratingBar;
    private TextView cooperativeLocation;
    private TextView cooperativeRegistationDate;
    private FrameLayout progressContent;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getArguments() != null;
        user = (User) getArguments().getSerializable(LoginActivity.USER_ID);
        initViews();
        getCooperativeAPI();
        return fragmentContent;
    }

    private void initViews(){
        progressContent = fragmentContent.findViewById(R.id.progress_content);
        cooperativeDescription = fragmentContent.findViewById(R.id.cooperative_description);
        cooperativeLongName = fragmentContent.findViewById(R.id.cooperative_long_name);
        cooperativeShortName = fragmentContent.findViewById(R.id.cooperative_short_name);
        cooperativeLocation = fragmentContent.findViewById(R.id.cooperative_location);
        cooperativeRegistationDate = fragmentContent.findViewById(R.id.cooperative_registration_date);
        shield = fragmentContent.findViewById(R.id.shield_image);
        setDefaultImages(shield,1);
        imageCover = fragmentContent.findViewById(R.id.cooperative_cover_image);
        setDefaultImages(imageCover,2);

        ratingBar = fragmentContent.findViewById(R.id.rating_bar_indicator);
    }

    private void showInformation(Cooperative cooperative){

        cooperativeShortName.setText(cooperative.getName());
        cooperativeLongName.setText(cooperative.getFullName());
        Spanned text;
        cooperativeLocation.setText(String.format(getString(R.string.cooperative_location_description)
                ,cooperative.getLocation().getName(),cooperative.getLocation().getDescription()));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(cooperative.getDescription(),Html.FROM_HTML_MODE_COMPACT);
        }else{
            text = Html.fromHtml(cooperative.getDescription());
        }
        cooperativeDescription.setText(text);
        cooperativeRegistationDate.setText(String.format(getString(R.string.registration_date_format),
                new LocalDate().getDateInString(cooperative.getCreateAt())));

        ratingBar.setRating(Float.parseFloat(cooperative.getQualification()));
    }

    @SuppressLint("CheckResult")
    private void loadImage(String url, ImageView imageView, int opc){

        CircularProgressDrawable placeHolder = new CircularProgressDrawable(fragmentContent.getContext());
        placeHolder.setStrokeWidth(5f);
        placeHolder.setCenterRadius(30f);
        placeHolder.start();

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);
        if(opc == 1){
            options.circleCrop();
        }else{
            options.placeholder(placeHolder);
            options.centerCrop();
        }

        Glide.with(this).load(url)
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
                }).apply(options).into(imageView);
    }

    private void setDefaultImages(ImageView imageView, int opc){

        if(opc == 1){
            Glide.with(this).load(R.drawable.placeholder).apply(new RequestOptions().circleCrop())
                    .into(imageView);
        }else{
            Glide.with(this).load(R.drawable.placeholder).apply(new RequestOptions().centerCrop())
                    .into(imageView);
        }
    }
    @SuppressLint("LogNotTimber")
    private void getCooperativeAPI(){

        String filter = String.format(getString(R.string.user_cooperative_filter),
                user.getId());

        Call<List<UserCoperative>> call = Api.instance().getUserCooperative(filter);
        call.enqueue(new Callback<List<UserCoperative>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserCoperative>> call,@NonNull Response<List<UserCoperative>> response) {
                if(response.body() != null){

                    if(response.body().size() > 0){
                        Cooperative cooperative = response.body().get(0).getCooperative();
                        loadImage(cooperative.getUrlShield(),shield, 1);
                        loadImage(cooperative.getUrlCoverImage(),imageCover,2);
                        showInformation(cooperative);
                    }
                    progressContent.setVisibility(View.GONE);

                }else{
                    showMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserCoperative>> call,@NonNull Throwable throwable) {
                showMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                Log.e(fragmentContent.getContext().getString(R.string.error_message_api),
                        throwable.getMessage());
            }
        });
    }

    private void showMessageInSnackbar(int code){
        String message = new ApiMessage().sendMessageOfResponseAPI(code,fragmentContent.getContext());
        Timber.i(message);

        Snackbar.make(fragmentContent,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}