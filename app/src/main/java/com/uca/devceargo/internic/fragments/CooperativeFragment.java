package com.uca.devceargo.internic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.CooperativeAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Cooperative;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CooperativeFragment extends Fragment {
    private RecyclerView recycler;
    private View view;
    private SwipeRefreshLayout swipe;
    //private CooperativeFragment fragment;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cooperative, container, false);
        setRecycler();
        getCooperatives();
        swipeListener();

        return view;
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.list_cooperatives);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void swipeListener(){
        swipe = view.findViewById(R.id.swipe_cooperatives);
        swipe.setOnRefreshListener(this::getCooperatives);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getCooperatives() {
        recycler.setAdapter(new ProgressAdapter());
        Call<List<Cooperative>> call = Api.instance().getCooperatives();
        call.enqueue(new Callback<List<Cooperative>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cooperative>> call,@NonNull Response<List<Cooperative>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new CooperativeAdapter(getContext(), response.body()));
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(false);
                    sendMessageInSnackbar(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cooperative>> call,@NonNull Throwable throwable) {
                swipe.setRefreshing(false);
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
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
