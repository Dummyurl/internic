package com.uca.devceargo.internic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.entities.TypeComment;
import com.uca.devceargo.internic.fragments.CooperativeFragment;

public class TypesComplaintAdapter  extends RecyclerView.Adapter<TypesComplaintAdapter.ViewHolder>{
    List<TypeComment> typeComments;
    CooperativeFragment fragment;

    public TypesComplaintAdapter(List<TypeComment> typeComments, CooperativeFragment context) {
        this.typeComments = typeComments;
        this.fragment = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView description;
        private LinearLayout layout;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.type_complaint_title);
            description = v.findViewById(R.id.type_complaint_description);
            layout = v.findViewById(R.id.types_complaint_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_types_complaints, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        TypeComment c = typeComments.get(i);
        holder.title.setText(c.getName());
        holder.description.setText(c.getDescription());

        holder.layout.setOnClickListener(view -> launchActivity(1,1));

    }

    private void launchActivity(int typeCommentID, int cooperativeID){

    }

    @Override
    public int getItemCount() {
        return typeComments.size();
    }
}
