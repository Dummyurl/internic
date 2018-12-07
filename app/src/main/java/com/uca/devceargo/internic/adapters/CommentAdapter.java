package com.uca.devceargo.internic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.entities.Comment;
import com.uca.devceargo.internic.entities.User;

import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Comment> comments;
    private User user;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView cooperativeCardCover;
        ImageView cooperativeCardShield;
        TextView cooperativeCardLongName;
        TextView cooperativeCardShortName;
        ImageView userCardImage;
        TextView userCardName;
        TextView userCardComment;
        TextView commentCreateAt;
        View view;
        ViewHolder(View view) {
            super(view);
            this.view = view;
            cooperativeCardCover = view.findViewById(R.id.cooperative_card_cover);
            cooperativeCardShield = view.findViewById(R.id.cooperative_card_shield);
            cooperativeCardLongName = view.findViewById(R.id.cooperative_card_long_name);
            cooperativeCardShortName = view.findViewById(R.id.cooperative_card_short_name);
            userCardImage = view.findViewById(R.id.user_card_image);
            userCardName = view.findViewById(R.id.user_card_name);
            userCardComment = view.findViewById(R.id.user_card_comment);
            commentCreateAt = view.findViewById(R.id.comment_create_at);
        }
    }

    static class ViewHolder2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView userImage;
        TextView userName;
        TextView userFullName;
        TextView email;
        TextView registrationDate;
        TextView userBirthDate;
        Context context;
        ViewHolder2(View view) {
            super(view);
            userImage = view.findViewById(R.id.user_profile_image);
            userName = view.findViewById(R.id.user_name);
            userFullName = view.findViewById(R.id.user_full_name);
            email = view.findViewById(R.id.user_email);
            registrationDate = view.findViewById(R.id.user_registration_date);
            userBirthDate = view.findViewById(R.id.user_birth_date);
            context = view.getContext();
        }

        @SuppressLint("CheckResult")
        private void loadImage(String url, ImageView imageView){

            CircularProgressDrawable placeHolder = new CircularProgressDrawable(
                    Objects.requireNonNull(context));
            placeHolder.setStrokeWidth(5f);
            placeHolder.setCenterRadius(30f);
            placeHolder.start();

            Glide.with(context).load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            placeHolder.stop();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            placeHolder.stop();
                            return false;
                        }
                    }).apply(new RequestOptions().centerCrop().error(R.drawable.placeholder)).into(imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentAdapter(List<Comment> comments, User user) {
        this.comments = comments;
        this.user = user;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 1;
        }else{
            return 2;
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view;
        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_user_data, parent, false);
                // set the view's size, margins, paddings and layout parameters
                return new ViewHolder2(view);
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_user_comments, parent, false);
                return new ViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_user_comments, parent, false);
                // set the view's size, margins, paddings and layout parameters
                return new ViewHolder(view);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int itemType = holder.getItemViewType();
        switch (itemType){
            case 1:
                ViewHolder2 fragmentHolder = (ViewHolder2) holder;
                fragmentHolder.userName.setText(user.getUserName());
                fragmentHolder.userFullName.setText(user.getFullName());
                fragmentHolder.userBirthDate.setText(new LocalDate().getDateInString(user.getBirthDate()));
                fragmentHolder.registrationDate.setText(String.format(
                        fragmentHolder.context.getString(R.string.user_registration_date_format),
                        new LocalDate().getDateInString(user.getCreateAt())));

                fragmentHolder.email.setText(user.getEmail());
                fragmentHolder.loadImage(user.getUrlImage(),fragmentHolder.userImage);
                break;
            case 2:
                ViewHolder cardHolder = (ViewHolder) holder;
                Comment comment = comments.get(position);
                Glide.with(cardHolder.view.getContext()).load(comment.getCooperative().getUrlCoverImage())
                        .apply(new RequestOptions().centerCrop().error(R.drawable.placeholder))
                        .into(cardHolder.cooperativeCardCover);

                Glide.with(cardHolder.view.getContext()).load(comment.getCooperative().getUrlShield())
                        .apply(RequestOptions.circleCropTransform().error(R.drawable.circle_place_holder))
                        .into(cardHolder.cooperativeCardShield);

                Glide.with(cardHolder.view.getContext()).load(comment.getUser().getUrlImage())
                        .apply(RequestOptions.circleCropTransform().error(R.drawable.circle_place_holder))
                        .into(cardHolder.userCardImage);

                cardHolder.cooperativeCardLongName.setText(comment.getCooperative().getFullName());
                cardHolder.cooperativeCardShortName.setText(comment.getCooperative().getName());
                cardHolder.userCardComment.setText(comment.getDescription());
                cardHolder.userCardName.setText(comment.getUser().getUserName());
                cardHolder.commentCreateAt.setText(new LocalDate().getDateInStringWithHour(comment.getCreateAt()));
                break;
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return comments.size();
    }
}
