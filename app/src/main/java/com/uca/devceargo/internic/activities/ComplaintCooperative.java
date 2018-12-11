package com.uca.devceargo.internic.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.ComplaintAdapter;
import com.uca.devceargo.internic.adapters.ProgressAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Comment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ComplaintCooperative extends AppCompatActivity {
    private static final String COOPERATIVE_ID = "cooperativeID";
    RecyclerView recycler;
    SwipeRefreshLayout swipe;
    int cooperativeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_cooperative);
        receiveParameters();
        setRecycler();
        getComments();
        swipeListener();
    }

    private void receiveParameters() {
        if(getIntent().getExtras() != null){
            cooperativeID = getIntent().getExtras().getInt(COOPERATIVE_ID);
        }
    }

    private void setRecycler(){
        recycler = findViewById(R.id.list_complaints);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void swipeListener(){
        swipe = findViewById(R.id.swipe_comments);
        swipe.setOnRefreshListener(this::getComments);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getComments(){
        String filter = String.format(this.getString(R.string.complaint_cooperative_filter), cooperativeID);
        recycler.setAdapter(new ProgressAdapter());
        Call<List<Comment>> call = Api.instance().getCommentCooperative(filter);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call,@NonNull Response<List<Comment>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new ComplaintAdapter(response.body(), getApplicationContext()));
                    swipe.setRefreshing(false);
                }else{
                    sendMessageInSnackbar(response.code());
                    swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call,@NonNull Throwable throwable) {
                sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);
                swipe.setRefreshing(false);
            }
        });
    }
    private void sendMessageInSnackbar(int code){
        View contextView = findViewById(android.R.id.content);
        String message = new ApiMessage().sendMessageOfResponseAPI(code,getApplicationContext());
        Timber.i(message);
        Snackbar.make(contextView,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
