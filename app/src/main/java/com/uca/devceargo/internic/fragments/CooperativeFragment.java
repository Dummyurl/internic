package com.uca.devceargo.internic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.CooperativeAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.entities.Cooperative;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooperativeFragment extends Fragment {
    private RecyclerView recycler;
    private View view;
    //private CooperativeFragment fragment;

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
        //fragment = new CooperativeFragment();
    }

    private void getCooperatives() {
        Call<List<Cooperative>> call = Api.instance().getCooperatives();
        call.enqueue(new Callback<List<Cooperative>>() {
            @Override
            public void onResponse(Call<List<Cooperative>> call, Response<List<Cooperative>> response) {
                if (response.body() != null) {
                    recycler.setAdapter(new CooperativeAdapter(getContext(), response.body()));
                } else {
                    Toast.makeText(getContext(), "Nulos las noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cooperative>> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure las noticias", Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* public void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_types_users, null);

        RecyclerView recyclerView = view.findViewById(R.id.list_types_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getTypeComment();
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getTypeComment() {
        Call<List<TypeComment>> call = Api.instance().getComments();
        call.enqueue(new Callback<List<TypeComment>>() {
            @Override
            public void onResponse(Call<List<TypeComment>> call, Response<List<TypeComment>> response) {
                if (response.body() != null) {
                    recycler.setAdapter(new TypesComplaintAdapter( response.body(), fragment));
                } else {
                    Toast.makeText(getContext(), "Nulos las noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TypeComment>> call, Throwable t) {
                Toast.makeText(getContext(), "onFailure las noticias", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}
