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
import com.uca.devceargo.internic.adapters.CooperativeAdapter;
import com.uca.devceargo.internic.adapters.NewsAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooperativeFragment extends Fragment {
    private RecyclerView recycler;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cooperative, container, false);
        setRecycler();
        getCooperatives();
        return view;
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.list_cooperatives);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getCooperatives(){
        Call<List<Cooperative>> call = Api.instance().getCooperatives();
        call.enqueue(new Callback<List<Cooperative>>() {
            @Override
            public void onResponse(Call<List<Cooperative>> call, Response<List<Cooperative>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new CooperativeAdapter(getContext(), response.body()));
                }else{
                    Toast.makeText(getContext(), "Nulos las noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cooperative>> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure las noticias", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
