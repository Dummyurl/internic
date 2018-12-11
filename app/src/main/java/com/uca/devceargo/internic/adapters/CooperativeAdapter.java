package com.uca.devceargo.internic.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.activities.ComplaintCooperative;
import com.uca.devceargo.internic.activities.NewComplaint;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.fragments.CooperativeFragment;

import java.util.List;

public class CooperativeAdapter extends RecyclerView.Adapter<CooperativeAdapter.ViewHolder> {

    private Context context;
    private List<Cooperative> cooperativeList;

    public CooperativeAdapter(Context context, List<Cooperative> cooperativeList) {
        this.context = context;
        this.cooperativeList = cooperativeList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private ImageView cover;
        private ImageView menu;
        private TextView name;
        private TextView description;
        private RatingBar qualification;
        private TextView contact;
        private TextView showDetails;

        public ViewHolder(@NonNull View view) {
            super(view);

            profile = view.findViewById(R.id.card_cooperative_profile);
            cover = view.findViewById(R.id.card_cooperative_cover);
            menu = view.findViewById(R.id.card_cooperative_menu);
            name = view.findViewById(R.id.card_cooperative_name);
            description = view.findViewById(R.id.card_cooperative_description);
            qualification = view.findViewById(R.id.card_cooperative_qualification);
            contact = view.findViewById(R.id.card_cooperative_contact);
            showDetails = view.findViewById(R.id.card_cooperative_show_details);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_cooperatives, viewGroup, false);
        return new CooperativeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Cooperative c = cooperativeList.get(i);
        holder.name.setText(c.getName());
        holder.description.setText(c.getDescription());
        holder.qualification.setRating(Float.parseFloat(c.getQualification()));
        holder.contact.setText(c.getContactNumber());

        Glide.with(context).load(c.getUrlShield()).apply(new RequestOptions().circleCrop()).into(holder.profile);
        Glide.with(context).load(c.getUrlCoverImage()).into(holder.cover);

        holder.showDetails.setOnClickListener(view -> {
            Intent intent = new Intent(context, ComplaintCooperative.class);
            intent.putExtra("cooperativeID", c.getId());
            context.startActivity(intent);
        });

        holder.menu.setOnClickListener(view -> {
            showPopupMenu(view, i);
        });
    }


    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cooperative, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;
        MyMenuItemClickListener(int position) {this.position = position;}
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent i = new Intent(context, NewComplaint.class);
            switch (menuItem.getItemId()) {

                case R.id.action_new_complaint:
                    i.putExtra("commentTypeID", 3);
                    i.putExtra("cooperativeID", cooperativeList.get(position).getId());
                    context.startActivity(i);
                    break;
                case R.id.action_new_opinion:
                    i.putExtra("commentTypeID", 1);
                    i.putExtra("cooperativeID", cooperativeList.get(position).getId());
                    context.startActivity(i);
                    break;
                case R.id.action_new_comment:
                    i.putExtra("commentTypeID", 2);
                    i.putExtra("cooperativeID", cooperativeList.get(position).getId());
                    context.startActivity(i);
                    break;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return cooperativeList.size();
    }
}
