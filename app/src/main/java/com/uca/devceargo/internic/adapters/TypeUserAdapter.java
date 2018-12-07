package com.uca.devceargo.internic.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.TypeUser;
import com.uca.devceargo.internic.activities.LoginActivity;

import java.util.List;

public class TypeUserAdapter extends RecyclerView.Adapter<TypeUserAdapter.ViewHolder> {

    private List<TypeUser> typeUserList;
    private LoginActivity activity;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        SimpleDraweeView typeUserImage;
        TextView typeUserTitle;
        TextView typeUserDescription;
        LinearLayout typeUserItem;

        ViewHolder(View view) {
            super(view);

            typeUserImage = view.findViewById(R.id.type_user_image);
            typeUserTitle = view.findViewById(R.id.type_user_title);
            typeUserDescription = view.findViewById(R.id.type_user_description);
            typeUserItem = view.findViewById(R.id.item_type_user);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TypeUserAdapter(List<TypeUser> typeUserList, LoginActivity activity) {
        this.typeUserList = typeUserList;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TypeUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_type_user, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new TypeUserAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final TypeUserAdapter.ViewHolder holder, int position) {

        final TypeUser typeUser = typeUserList.get(position);

        holder.typeUserTitle.setText(typeUser.getName());
        holder.typeUserDescription.setText(typeUser.getDescription());
        holder.typeUserImage.setImageURI(typeUser.getImageURL());
        holder.typeUserItem.setOnClickListener(v -> activity.showRegisterActivity(typeUser.getId()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return typeUserList.size();
    }
}