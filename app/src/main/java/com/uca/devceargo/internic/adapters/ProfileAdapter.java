package com.uca.devceargo.internic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.classes.LocalDistance;
import com.uca.devceargo.internic.classes.LocalGlide;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.Route;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int INFLATE_COOPERATIVE = 1;
    private static final int INFLATE_ROUTES_LIST = 2;

    private List<Route> routes;
    private Cooperative cooperative;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProfileAdapter(List<Route> routes) {
        this.routes = routes;
    }

    static class CooperativeHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView cooperativeLongName;
        TextView cooperativeShortName;
        ImageView imageCover;
        ImageView shield;
        TextView cooperativeDescription;
        RatingBar ratingBar;
        TextView cooperativeLocation;
        TextView cooperativeRegistrationDate;
        Context context;

        CooperativeHolder(View view) {
            super(view);
            context = view.getContext();
            cooperativeDescription = view.findViewById(R.id.cooperative_description);
            cooperativeLongName = view.findViewById(R.id.cooperative_long_name);
            cooperativeShortName = view.findViewById(R.id.cooperative_short_name);
            cooperativeLocation = view.findViewById(R.id.cooperative_location);
            cooperativeRegistrationDate = view.findViewById(R.id.cooperative_registration_date);
            imageCover = view.findViewById(R.id.cooperative_cover_image);
            shield = view.findViewById(R.id.shield_image);
            ratingBar = view.findViewById(R.id.rating_bar_indicator);
        }
    }

    static class RouteHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView routeTime;
        TextView routeDistance;
        TextView routeCost;
        TextView routeTitle;
        TextView routeDescription;
        TextView routeCreateAt;
        ImageView routeImage;
        Context context;
        RouteHolder(View view) {
            super(view);
            context = view.getContext();
            routeCost = view.findViewById(R.id.route_cost);
            routeCreateAt = view.findViewById(R.id.route_create_at);
            routeDescription = view.findViewById(R.id.route_description);
            routeTime = view.findViewById(R.id.route_time);
            routeDistance = view.findViewById(R.id.route_distance);
            routeTitle = view.findViewById(R.id.route_title);
            routeImage = view.findViewById(R.id.route_image);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return INFLATE_COOPERATIVE;
        }
        return INFLATE_ROUTES_LIST;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view;
        switch (viewType){
            case INFLATE_COOPERATIVE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cooperative_profile, parent, false);
                return new CooperativeHolder(view);
            case INFLATE_ROUTES_LIST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_route, parent, false);
                return new RouteHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_route, parent, false);
                return new RouteHolder(view);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int itemType = holder.getItemViewType();
        switch (itemType){
            case INFLATE_COOPERATIVE:
                CooperativeHolder coopHolder =(CooperativeHolder) holder;
                showCooperative(coopHolder);
                break;
            case INFLATE_ROUTES_LIST:
                RouteHolder routeHolder = (RouteHolder) holder;
                showRoutes(routeHolder,position);
                break;
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount(){
        return routes.size();
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }


    private void showCooperative(CooperativeHolder coopHolder){
        coopHolder.cooperativeShortName.setText(cooperative.getName());
        coopHolder.cooperativeLongName.setText(cooperative.getFullName());
        coopHolder.ratingBar.setRating(Float.parseFloat(cooperative.getQualification()));
        //Instance local glide class
        LocalGlide localGlide = new LocalGlide();
        //Load image for cover cooperative
        localGlide.loadImage(coopHolder.imageCover,cooperative.getUrlCoverImage(),
                LocalGlide.CENTER_CROP);
        //Define circle background for shield ImageView
        localGlide.setBackgroundImageID(R.drawable.circle_place_holder);
        //Load image for shield imageView
        localGlide.loadImage(coopHolder.shield,cooperative.getUrlShield(),
                LocalGlide.CIRCLE_CROP);
        coopHolder.cooperativeLocation.setText(String.format(
                coopHolder.context.getString(R.string.cooperative_location_description),
                cooperative.getLocation().getName(), cooperative.getLocation().getDescription()));
        coopHolder.cooperativeRegistrationDate.setText(
                String.format(coopHolder.context.getString(R.string.registration_date_format),
                        new LocalDate().getDateInString(cooperative.getCreateAt())));
        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(cooperative.getDescription(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            text = Html.fromHtml(cooperative.getDescription());
        }
        coopHolder.cooperativeDescription.setText(text);
    }

    private void showRoutes(RouteHolder routeHolder, int position){
        Route route;
        route = routes.get(position);
        routeHolder.routeTitle.setText(route.getName());
        routeHolder.routeDistance.setText(new LocalDistance().getStringDistance(route.getDistance()));
        routeHolder.routeTime.setText(new LocalDate().getHourInString(route.getTime()));
        routeHolder.routeDescription.setText(route.getDescription());
        routeHolder.routeCost.setText(String.format(routeHolder.context.getString(R.string.cooperative_route_cost_format),
                route.getCost()));
        routeHolder.routeCreateAt.setText(new LocalDate().getDateInStringWithHour(route.getCreateAt()));

        new LocalGlide().loadImageCenterCrop(routeHolder.routeImage,
                route.getUrlImage(),routeHolder.context);
    }
}
