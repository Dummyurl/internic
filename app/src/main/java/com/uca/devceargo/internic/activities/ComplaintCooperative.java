package com.uca.devceargo.internic.activities;

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
    int cooperativeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_cooperative);
        relieveParameters();
        setRecycler();
        getCooments();
    }

    private void relieveParameters() {
        if(getIntent().getExtras() != null){
            cooperativeID = getIntent().getExtras().getInt("cooperativeID");
        }
    }

    private void setRecycler(){
        recycler = findViewById(R.id.list_complaints);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getCooments(){
        String filter = String.format(this.getString(R.string.complaint_cooperative_filter), cooperativeID);

        Call<List<Comment>> call = Api.instance().getCommentCooperative(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new ComplaintAdapter(response.body(), getApplicationContext()));
                }else{
                    Toast.makeText(getApplicationContext(), "nulos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
