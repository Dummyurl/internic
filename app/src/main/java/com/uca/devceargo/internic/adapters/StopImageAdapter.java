package com.uca.devceargo.internic.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.entities.PictureStop;

import java.util.ArrayList;
import java.util.List;


public class StopImageAdapter extends RecyclerView.Adapter<StopImageAdapter.ViewHolder> {

    private List<PictureStop> pictureStops;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        SimpleDraweeView stopImage;
        TextView NameStopImage;
        ViewHolder(View view) {
            super(view);
            stopImage =  view.findViewById(R.id.stop_image);
            NameStopImage = view.findViewById(R.id.name_image_stop);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StopImageAdapter(List<PictureStop> pictureStops) {
        this.pictureStops = pictureStops;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public StopImageAdapter() {

        PictureStop pictureStop = new PictureStop();
        pictureStops = new ArrayList<>();
        pictureStops.add(pictureStop);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public StopImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_images_stops, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new StopImageAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final StopImageAdapter.ViewHolder holder, int position) {

        holder.stopImage.setImageResource(R.drawable.placeholder);
        holder.NameStopImage.setText("Nombre de la imagen " + (position+1));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pictureStops.size();
    }
}