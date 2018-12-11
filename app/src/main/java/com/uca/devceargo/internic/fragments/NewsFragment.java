package com.uca.devceargo.internic.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.NewsAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {
    RecyclerView recycler;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        setRecycler();
        getNews();
        return view;
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.list_news);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getNews(){
        //String filter = getString(R.string.news_filter);
        Call<List<News>> call = Api.instance().getNews();
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new NewsAdapter(response.body(), getContext()));
                }else{
                    Toast.makeText(getContext(), "Nulos las noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure las noticias"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
