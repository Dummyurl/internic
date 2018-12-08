package com.uca.devceargo.internic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.entities.News;

/**
 * Created by ElOskar101 on 14/11/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> newsList;
    private Context context;

    public NewsAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private TextView description;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.news_image);
            title = view.findViewById(R.id.news_title);
            description = view.findViewById(R.id.news_description);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_news, parent, false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News n = newsList.get(position);
        holder.title.setText(n.getTitle());
        holder.description.setText(n.getDescription());
        Glide.with(context).load(R.drawable.background_profile).into(holder.image);
    }

    @Override
    public int getItemCount() {return newsList.size();}


}
