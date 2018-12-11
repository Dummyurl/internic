package com.uca.devceargo.internic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import java.util.List;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.entities.Comment;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    List<Comment> comments;
    Context context;

    public ComplaintAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private ImageView image;
        private TextView userName;
        private TextView createAt;
        private TextView description;

        ViewHolder(@NonNull View v) {
            super(v);
             profile = v.findViewById(R.id.card_complaint_user_profile);
             image = v.findViewById(R.id.card_complaint_image);
             userName = v.findViewById(R.id.card_complaint_user_name);
             createAt = v.findViewById(R.id.card_complaint_create_at);
             description = v.findViewById(R.id.card_complaint_complaint);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_comments, viewGroup, false);
        return new ComplaintAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Comment c = comments.get(i);
        Glide.with(context).load(c.getUser().getUrlImage()).apply(new RequestOptions().circleCrop()).into(holder.profile);
        holder.userName.setText(c.getUser().getFullName());
        holder.createAt.setText(new LocalDate().getDateInString(c.getCreateAt()));
        holder.description.setText(c.getDescription());
        Glide.with(context).load(c.getUrlImage()).apply(new RequestOptions().centerCrop()).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
