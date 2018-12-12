package com.uca.devceargo.internic.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.LoginActivity;
import com.uca.devceargo.internic.activities.UserMapActivity;
import com.uca.devceargo.internic.adapters.CommentAdapter;
import com.uca.devceargo.internic.adapters.OfflineAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Comment;
import com.uca.devceargo.internic.entities.User;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private User user;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipe;
    int band;
    View view;
    public ProfileUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        user = (User) getArguments().getSerializable(LoginActivity.USER_ID);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        swipeListener(view);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe = view.findViewById(R.id.refresh_layout);
        band = 0;
        getComments();
        initFabButtons();
        return view;
    }

    private void swipeListener(View view){
        swipe = view.findViewById(R.id.refresh_layout);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initFabButtons(){
        FloatingActionMenu fab = view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnMenuButtonClickListener((View view)->
            startActivity(new Intent(getContext(),UserMapActivity.class)));
    }

    @SuppressLint("LogNotTimber")
    private void getComments(){
        if(band == 0)
            recyclerView.setAdapter(new ProgressAdapter());

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comments.add(comment);
        String filter = String.format(getString(R.string.user_comments_filter_in_request),user.getId());

        Call<List<Comment>> call = Api.instance().getCommentUser(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {

                if(response.body() != null){
                    if(response.body().size() > 0){
                        comments.addAll(response.body());
                        recyclerView.setAdapter(new CommentAdapter(comments,user));
                    }else{
                        recyclerView.setAdapter(new CommentAdapter(comments,user));
                    }
                    band = 1;
                }else{
                    if(band == 0){
                        recyclerView.setAdapter(new OfflineAdapter(
                                new ApiMessage().sendMessageOfResponseAPI(response.code(),getContext()),
                                String.valueOf(response.code()),getString(R.string.default_message)));
                    }
                    showMessageInSnackbar(response.code());
                }
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call,@NonNull Throwable throwable) {
                Log.e(getString(R.string.error_message_api),throwable.getMessage());
                if(throwable.getMessage().equalsIgnoreCase("timeout") && band == 0){
                    recyclerView.setAdapter(new OfflineAdapter(
                            new ApiMessage().sendMessageOfResponseAPI(ApiMessage.REQUEST_TIMEOUT,getContext()),
                            String.valueOf(ApiMessage.REQUEST_TIMEOUT),getString(R.string.default_message)));
                }else{
                    recyclerView.setAdapter(new OfflineAdapter(
                            getString(R.string.message_network_connection_error),
                            getString(R.string.default_message)));
                }
                showMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                swipe.setRefreshing(false);
            }
        });
    }

    private void showMessageInSnackbar(int code){
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getContext());
        Snackbar.make(view,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onRefresh() {
        getComments();
    }
}