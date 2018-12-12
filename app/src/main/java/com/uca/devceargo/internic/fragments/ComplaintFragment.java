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
import com.uca.devceargo.internic.adapters.ComplaintAdapter;
import com.uca.devceargo.internic.adapters.EmptyAdapter;
import com.uca.devceargo.internic.adapters.OfflineAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class ComplaintFragment extends Fragment {
    public static final String COOPERATIVE_ID = "cooperativeID";
    RecyclerView recycler;
    SwipeRefreshLayout swipe;
    int cooperativeID;
    int band;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_compaint, container, false);
        band = 0;
        assert getArguments() != null;
        cooperativeID = getArguments().getInt(COOPERATIVE_ID);
        setRecycler();
        getComments();
        swipeListener();
        return view;
    }

    private void swipeListener(){
        swipe = view.findViewById(R.id.swipe_complaints);
        swipe.setOnRefreshListener(this::getComments);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.recycler_view_fragment);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getComments(){
        // Agregá en el where el otro filtro para traer el typeCommentID = 3;
        if(band == 0)
            recycler.setAdapter(new ProgressAdapter());

        String filter = String.format(this.getString(R.string.complaint_cooperative_filter), cooperativeID);
        Call<List<Comment>> call = Api.instance().getCommentCooperative(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call,@NonNull Response<List<Comment>> response) {
                if(response.body() != null){
                    if(!response.body().isEmpty()){
                        recycler.setAdapter(new ComplaintAdapter(response.body(), getContext()));
                    }else{
                        recycler.setAdapter(new EmptyAdapter());
                    }
                    band = 1;
                }else{
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
            public void onFailure(@NonNull Call<List<Comment>> call,@NonNull Throwable throwable) {

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
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
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
