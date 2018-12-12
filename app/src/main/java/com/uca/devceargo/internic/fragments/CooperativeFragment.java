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
import com.uca.devceargo.internic.adapters.CooperativeAdapter;
import com.uca.devceargo.internic.adapters.EmptyAdapter;
import com.uca.devceargo.internic.adapters.OfflineAdapter;
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
    private int band;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cooperative, container, false);
        band = 0;
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
        if(band == 0)
            recycler.setAdapter(new ProgressAdapter());
        Call<List<Cooperative>> call = Api.instance().getCooperatives();
        call.enqueue(new Callback<List<Cooperative>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cooperative>> call,@NonNull Response<List<Cooperative>> response) {
                if(response.body() != null){
                    if(!response.body().isEmpty()){
                        recycler.setAdapter(new CooperativeAdapter(getContext(), response.body()));
                    }else{
                        recycler.setAdapter(new EmptyAdapter());
                    }
                    band = 1;
                } else {
                    sendMessageInSnackbar(response.code());
                    if(band == 0){
                        recycler.setAdapter(new OfflineAdapter(
                                new ApiMessage().sendMessageOfResponseAPI(response.code(),getContext()),
                                String.valueOf(response.code()),getString(R.string.default_message)));
                    }
                }
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Cooperative>> call,@NonNull Throwable throwable) {
                swipe.setRefreshing(false);
                if(throwable.getMessage().equalsIgnoreCase("timeout") && band == 0){
                    recycler.setAdapter(new OfflineAdapter(
                            new ApiMessage().sendMessageOfResponseAPI(ApiMessage.REQUEST_TIMEOUT,getContext()),
                            String.valueOf(ApiMessage.REQUEST_TIMEOUT),getString(R.string.default_message)));
                }else{
                    recycler.setAdapter(new OfflineAdapter(
                           getString(R.string.message_network_connection_error),
                            getString(R.string.default_message)));
                }
            }
        });
    }

    private void sendMessageInSnackbar(int code){
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getContext());
        Timber.i(message);
        Snackbar.make(view,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
