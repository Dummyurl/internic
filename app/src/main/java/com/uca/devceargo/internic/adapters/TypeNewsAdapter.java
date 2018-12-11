package com.uca.devceargo.internic.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;
import java.util.Objects;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.NewLateness;
import com.uca.devceargo.internic.entities.TypeNews;

public class TypeNewsAdapter extends RecyclerView.Adapter<TypeNewsAdapter.MyViewHolder> {

    private List<TypeNews> typeNewsList;
    private AlertDialog dialog;

    public TypeNewsAdapter(List<TypeNews> typeNewsList, AlertDialog dialog) {
        this.typeNewsList = typeNewsList;
        this.dialog = dialog;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView description;
        LinearLayout typeNewsItem;

        private MyViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.type_news_image);
            title = view.findViewById(R.id.type_news_title);
            description = view.findViewById(R.id.type_news_description);
            typeNewsItem = view.findViewById(R.id.item_type_news);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_type_news, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TypeNews type = typeNewsList.get(position);
        if(type.getId()== 1){ // Lateness
            Glide.with(dialog.getContext().getApplicationContext()).load(R.drawable.ic_lateness_icon).apply(new RequestOptions().circleCrop())
                  .into(holder.image);

        }else if (type.getId() == 2){
        Glide.with(dialog.getContext()).load(R.drawable.ic_general_news_icon).apply(new RequestOptions().circleCrop())
                    .into(holder.image);
        }

        holder.description.setText(type.getDescription());
        holder.title.setText(type.getName());

        ViewSwitcher viewSwitcher = dialog.findViewById(R.id.view_switcher_types_news);

        holder.typeNewsItem.setOnClickListener(view1 -> {
            if(type.getId() == 2){
                dialog.setTitle(R.string.post_news_title);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                Objects.requireNonNull(viewSwitcher).showNext();

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener((View view2)->{
                    viewSwitcher.showPrevious();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener((View view3)->
                        dialog.dismiss());
                });
            }else{
                Intent intent = new Intent(dialog.getContext(), NewLateness.class);
                dialog.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeNewsList.size();
    }
}
