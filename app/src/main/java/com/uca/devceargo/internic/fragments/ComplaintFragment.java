package com.uca.devceargo.internic.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.ComplaintAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ComplaintFragment extends Fragment {
    RecyclerView recycler;
    int cooperativeID = 1; // Para probar el fragment.
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_compaint, container, false);
        setRecycler();
        getComments();
        return view;
    }

    private void setRecycler(){
        recycler = view.findViewById(R.id.list_complaints);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getComments(){
        // Agregá en el where el otro filtro para traer el typeCommentID = 3;
        String filter = String.format(this.getString(R.string.complaint_cooperative_filter), cooperativeID);

        Call<List<Comment>> call = Api.instance().getCommentCooperative(filter);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.body() != null){
                    recycler.setAdapter(new ComplaintAdapter(response.body(), getContext()));
                }else{
                    Toast.makeText(getContext(), "nulos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
