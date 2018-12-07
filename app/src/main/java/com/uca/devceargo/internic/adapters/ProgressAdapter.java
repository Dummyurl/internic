package com.uca.devceargo.internic.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uca.devceargo.internic.R;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ViewHolder(View view) {
            super(view);
        }
    }

    @NonNull
    @Override
    public ProgressAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
