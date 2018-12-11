package com.uca.devceargo.internic.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.ComplaintAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Comment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintCooperative extends AppCompatActivity {
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
            cooperativeID = getIntent().getExtras().getInt("cooperativeID");
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

        Call<List<Comment>> call = Api.instance().getCommentCooperative(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new ComplaintAdapter(response.body(), getApplicationContext()));
                    swipe.setRefreshing(false);
                }else{
                    Toast.makeText(getApplicationContext(), "nulos", Toast.LENGTH_SHORT).show();
                    swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure "+t.getMessage(), Toast.LENGTH_SHORT).show();
                swipe.setRefreshing(false);
            }
        });
    }
}
