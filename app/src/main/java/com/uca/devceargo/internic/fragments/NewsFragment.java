package com.uca.devceargo.internic.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.EmptyAdapter;
import com.uca.devceargo.internic.adapters.NewsAdapter;
import com.uca.devceargo.internic.adapters.OfflineAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {
    public static final String COOPERATIVE_ID = "cooperativeID";
    RecyclerView recycler;
    View view;
    SwipeRefreshLayout swipe;
    int cooperativeID;
    int band;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        setRecycler();
        band = 0;
        assert getArguments() != null;
        cooperativeID = getArguments().getInt(COOPERATIVE_ID);
        if(cooperativeID  != 0){
            getNews(cooperativeID);
        }else{
            getNews();
        }
        swipeListener();
        return view;
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.list_news);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void swipeListener(){
        swipe = view.findViewById(R.id.swipe_news);
        swipe.setOnRefreshListener(this::getNews);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getNews(){
        Call<List<News>> call = Api.instance().getNews();
        if(band == 0)
            recycler.setAdapter(new ProgressAdapter());

        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,@NonNull Response<List<News>> response) {
                if(response.body() != null){
                    if(response.body().isEmpty()){
                        recycler.setAdapter(new NewsAdapter(response.body(), getContext()));
                    }else{
                        recycler.setAdapter(new EmptyAdapter());
                    }
                    band = 1;
                }else{
                    if(band == 0){
                        recycler.setAdapter(new OfflineAdapter(
                                new ApiMessage().sendMessageOfResponseAPI(response.code(),getContext()),
                                String.valueOf(response.code()),getString(R.string.default_message)));
                    }
                    sendMessageInSnackbar(response.code());
                }
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call,@NonNull Throwable throwable) {
                if(throwable.getMessage().equalsIgnoreCase("timeout") && band == 0){
                    recycler.setAdapter(new OfflineAdapter(
                            new ApiMessage().sendMessageOfResponseAPI(ApiMessage.REQUEST_TIMEOUT,getContext()),
                            String.valueOf(ApiMessage.REQUEST_TIMEOUT),getString(R.string.default_message)));
                }else{
                    recycler.setAdapter(new OfflineAdapter(
                            getString(R.string.message_network_connection_error),
                            getString(R.string.default_message)));
                }
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                swipe.setRefreshing(false);
            }
        });

    }

    private void getNews(int id){
        String filter = "{\"include\":\"cooperative\",\"where\":{\"cooperativeID\":"+id+"}}";
        Call<List<News>> call = Api.instance().getNews(filter);
        if(band == 0){
            recycler.setAdapter(new ProgressAdapter());
        }
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,@NonNull Response<List<News>> response) {
                if(response.body() != null){
                    if(!response.body().isEmpty()){
                        recycler.setAdapter(new NewsAdapter(response.body(), getContext()));
                    }else{
                        recycler.setAdapter(new EmptyAdapter());
                    }
                    band = 1;
                }else{
                    if(band == 0){
                        recycler.setAdapter(new OfflineAdapter(
                                new ApiMessage().sendMessageOfResponseAPI(response.code(),getContext()),
                                String.valueOf(response.code()),getString(R.string.default_message)));
                    }
                    sendMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call,@NonNull Throwable throwable) {
                if(throwable.getMessage().equalsIgnoreCase("timeout") && band == 0){
                    recycler.setAdapter(new OfflineAdapter(
                            new ApiMessage().sendMessageOfResponseAPI(ApiMessage.REQUEST_TIMEOUT,getContext()),
                            String.valueOf(ApiMessage.REQUEST_TIMEOUT),getString(R.string.default_message)));
                }else{
                    recycler.setAdapter(new OfflineAdapter(
                            getString(R.string.message_network_connection_error),
                            getString(R.string.default_message)));
                }
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                swipe.setRefreshing(false);
            }
        });
    }

    private void sendMessageInSnackbar(int code){
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getContext());
        Snackbar.make(view,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
