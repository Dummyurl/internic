package com.uca.devceargo.internic.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.uca.devceargo.internic.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class RouteUserMapBox {
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String ROUTE_LAYER_ID2 = "route-layer-id2";
    private static final String ROUTE_SOURCE_ID2 = "route-source-id2";
    private static final String ICON_SOURCE_ID2 = "icon-source-id2";
    public static final boolean PROFILE_DRIVING = false;
    public static final boolean PROFILE_WALKING = true;
    private MapboxMap map;
    private String accessToken;
    private Context context;
    private MapboxDirections.Builder directionsBuilder;
    private DirectionsRoute currentRoute;
    private List<Point> points;
    private boolean typeRoute;
    private LinearLayout routeDetails;
    private TextView distanceUser;
    private TextView timeUser;
    private LinearLayout bottomSheet;

    public RouteUserMapBox (String accessToken, MapboxMap map, Context context){
        this.accessToken = accessToken;
        this.map = map;
        typeRoute = PROFILE_DRIVING;

        this.context = context;
    }

    private void initViews(View view){
        routeDetails = view.findViewById(R.id.user_route_detail);
        distanceUser = view.findViewById(R.id.user_route_distance);
        timeUser = view.findViewById(R.id.user_route_time);
    }

    public void setTypeRoute(boolean typeRoute) {
        this.typeRoute = typeRoute;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void setBottomSheet(LinearLayout bottomSheet) {
        this.bottomSheet = bottomSheet;
        initViews(bottomSheet);
    }

    /**
     * Add the route and marker sources to the map
     */
    public void initSource() {
        GeoJsonSource routeGeoJsonSource;
        if(typeRoute){
            routeGeoJsonSource = new GeoJsonSource(ROUTE_SOURCE_ID,
                    FeatureCollection.fromFeatures(new Feature[] {}));
        }else{
            routeGeoJsonSource = new GeoJsonSource(ROUTE_SOURCE_ID2,
                    FeatureCollection.fromFeatures(new Feature[] {}));
        }

        if(map.getSource(ROUTE_SOURCE_ID) == null || map.getSource(ROUTE_SOURCE_ID2) == null){
            map.addSource(routeGeoJsonSource);
            List<Feature> features = new ArrayList<>();

            for (Point point : points) {
                features.add( Feature.fromGeometry(point));
            }
            FeatureCollection iconFeatureCollection = FeatureCollection.fromFeatures(features);
            GeoJsonSource iconGeoJsonSource;
            if(typeRoute){
               iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, iconFeatureCollection);
            }else{
                iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID2, iconFeatureCollection);
            }
            map.addSource(iconGeoJsonSource);
            initLayers();
        }
    }

    /**
     * Add the route and maker icon layers to the map
     */
    private void initLayers() {
        LineLayer routeLayer;
        String colorLine = "#03A9F4";
        if(typeRoute){
            routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        }else {
            colorLine = "#009688";
            routeLayer = new LineLayer(ROUTE_LAYER_ID2, ROUTE_SOURCE_ID2);
        }

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor(colorLine))
        );
        map.addLayerBelow(routeLayer,"com.mapbox.annotations.points");
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     */

    public void drawRoute(){

        int positionEnd = points.size() -1;
        directionsBuilder =  MapboxDirections.builder();

        for (int i = 0; i < points.size(); i++) {

            if(i == 0){
                directionsBuilder.origin(points.get(i));
            }else if(i == positionEnd){
                directionsBuilder.destination(points.get(i));
            }else{
                directionsBuilder.destination(points.get(i));
            }
        }
        defineRoute();
    }

    @SuppressLint("LogNotTimber")
    private void defineRoute() {
        MapboxDirections client;
        if(typeRoute){
            routeDetails.setVisibility(View.VISIBLE);
            bottomSheet.findViewById(R.id.user_route_title).setVisibility(View.GONE);
            directionsBuilder.profile(DirectionsCriteria.PROFILE_WALKING);
        }
        else{
            directionsBuilder.profile(DirectionsCriteria.PROFILE_DRIVING);
        }
        client = directionsBuilder
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .accessToken(accessToken)
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {

            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.i(context.getString(R.string.message),"Response code " + response.code());

                if (response.body() == null) {
                    Log.e(context.getString(R.string.error_message),"No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(context.getString(R.string.error_message),"No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                if(typeRoute){
                    distanceUser.setText(new LocalDistance().getStringDistance(currentRoute.distance()));
                    if(currentRoute.duration() != null ){
                        Double duration = currentRoute.duration();
                        assert duration != null;
                        timeUser.setText(new LocalDate().getHourInString(duration));
                    }
                }

                GeoJsonSource source;

                if(typeRoute){
                    // Retrieve and update the source designated for showing the directions route
                    source = map.getSourceAs(ROUTE_SOURCE_ID);
                }else{
                    // Retrieve and update the source designated for showing the directions route
                    source = map.getSourceAs(ROUTE_SOURCE_ID2);
                }

                // Create a LineString with the directions route's geometry
                FeatureCollection featureCollection = FeatureCollection.fromFeature(
                        Feature.fromGeometry(LineString.fromPolyline(Objects.requireNonNull(currentRoute.geometry()), PRECISION_6)));

                // Reset the GeoJSON source for the route LineLayer source
                if (source != null) {
                    Log.d(context.getString(R.string.message),"onResponse: source != null");
                    source.setGeoJson(featureCollection);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call,@NonNull Throwable throwable) {
                Log.e(context.getString(R.string.error_message), throwable.getMessage());
            }
        });
    }
}
