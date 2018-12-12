package com.uca.devceargo.internic.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uca.devceargo.internic.R;


/*
 * Created by Mario Arce on 19/11/2017.
 */

public class OfflineAdapter extends RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    private String errorMessage;
    private String messageCode;
    private String messageTitle;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView message;
        TextView messageTitle;
        TextView messageCode;

        public ViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.error_message);
            messageCode = view.findViewById(R.id.message_code);
            messageTitle = view.findViewById(R.id.message_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OfflineAdapter(String errorMessage, String messageCode, String messageTitle) {
        this.errorMessage = errorMessage;
        this.messageCode = messageCode;
        this.messageTitle = messageTitle;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OfflineAdapter(String errorMessage, String messageTitle) {
        this.errorMessage = errorMessage;
        this.messageCode = "";
        this.messageTitle = messageTitle;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public OfflineAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.without_connection, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new OfflineAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull OfflineAdapter.ViewHolder holder, int position) {

        holder.message.setText(errorMessage);
        holder.messageTitle.setText(messageTitle);
        holder.messageCode.setText(messageCode);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}
