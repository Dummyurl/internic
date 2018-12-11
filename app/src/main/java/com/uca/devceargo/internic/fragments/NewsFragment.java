package com.uca.devceargo.internic.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.NewsAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class NewsFragment extends Fragment {
    RecyclerView recycler;
    View view;
    SwipeRefreshLayout swipe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        setRecycler();
        getNews();
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
        //String filter = getString(R.string.news_filter);
        Call<List<News>> call = Api.instance().getNews();
        recycler.setAdapter(new ProgressAdapter());
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if(response.body() != null){
                    swipe.setRefreshing(false);
                    recycler.setAdapter(new NewsAdapter(response.body(), getContext()));
                }else{
                    swipe.setRefreshing(false);
                    sendMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                swipe.setRefreshing(false);
            }
        });

    }

    private void sendMessageInSnackbar(int code){
        View contextView = view.findViewById(android.R.id.content);
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getContext());
        Timber.i(message);
        Snackbar.make(contextView,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
