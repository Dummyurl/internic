package com.uca.devceargo.internic.classes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.entities.Route;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class RouteMapBox {
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String ROUTE_LAYER_ID2 = "route-layer-id2";
    private static final String ROUTE_SOURCE_ID2 = "route-source-id2";
    private static final String ICON_SOURCE_ID2 = "icon-source-id2";

    private DirectionsRoute currentRoute;
    private MapboxMap map;
    private String accessToken;
    private Context context;
    private Route route;
    private MapboxDirections.Builder directionsBuilder;
    private Uri mapBoxUri;
    private ProgressDialog progressDialog;

    public void setRoute(Route route) {
        this.route = route;
    }

    public RouteMapBox (String accessToken, MapboxMap map, Context context){
        this.accessToken = accessToken;
        this.map = map;
        this.context = context;
        mapBoxUri = Uri.parse("/");
    }

    public Uri getMapBoxUri() {
        return mapBoxUri;
    }

    /**
     * Add the route and marker sources to the map
     */
    public void initSource() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Definiendo la ruta ...");

        GeoJsonSource routeGeoJsonSource = new GeoJsonSource(ROUTE_SOURCE_ID,
                FeatureCollection.fromFeatures(new Feature[] {}));

        if(map.getSource(ROUTE_SOURCE_ID) == null){
            map.addSource(routeGeoJsonSource);
            List<Feature> features = new ArrayList<>();

            for (Marker marker : map.getMarkers()) {
                features.add( Feature.fromGeometry(Point.fromLngLat(marker.getPosition().getLongitude(),
                        marker.getPosition().getLatitude())));
            }
            FeatureCollection iconFeatureCollection = FeatureCollection.fromFeatures(features);

            GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, iconFeatureCollection);
            map.addSource(iconGeoJsonSource);
            initLayers();
        }
    }

    /**
     * Add the route and maker icon layers to the map
     */
    private void initLayers() {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        map.addLayerBelow(routeLayer,"com.mapbox.annotations.points");
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     */

    public void drawRoute(){
        List<Marker> markers = map.getMarkers();
        int positionEnd = markers.size() -1;
        Point point;
        directionsBuilder =  MapboxDirections.builder();

        for (int i = 0; i < map.getMarkers().size(); i++) {

            if(i == 0){
                point = Point.fromLngLat(markers.get(i).getPosition().getLongitude(),
                        markers.get(i).getPosition().getLatitude());
                directionsBuilder.origin(point);
            }else if(i == positionEnd){
                point = Point.fromLngLat(markers.get(i).getPosition().getLongitude(),
                        markers.get(i).getPosition().getLatitude());
                directionsBuilder.destination(point);
            }else{
                point = Point.fromLngLat(markers.get(i).getPosition().getLongitude(),
                        markers.get(i).getPosition().getLatitude());
                directionsBuilder.addWaypoint(point);
            }
        }
        defineRoute();
    }
    @SuppressLint("LogNotTimber")
    private void defineRoute() {

        MapboxDirections client;

        client = directionsBuilder
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(accessToken)
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {

            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.i(context.getString(R.string.message),"Response code " + response.code());

                if (response.body() == null) {
                    Log.e(context.getString(R.string.error_message),"No routes found, make sure you set the right user and access token.");
                    progressDialog.dismiss();
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(context.getString(R.string.error_message),"No routes found");
                    progressDialog.dismiss();
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);

                if(currentRoute.duration() != null && currentRoute.distance() != null){
                    assert currentRoute.duration() != null;
                    Double duration = currentRoute.duration();
                    assert currentRoute.distance() != null;
                    Double distance = currentRoute.distance();

                    if(duration != null && distance != null){
                        route.setTime(duration);
                        route.setDistance(distance);
                    }
                }

                // Retrieve and update the source designated for showing the directions route
                GeoJsonSource source = map.getSourceAs(ROUTE_SOURCE_ID);

                // Create a LineString with the directions route's geometry
                FeatureCollection featureCollection = FeatureCollection.fromFeature(
                        Feature.fromGeometry(LineString.fromPolyline(Objects.requireNonNull(currentRoute.geometry()), PRECISION_6)));

                // Reset the GeoJSON source for the route LineLayer source
                if (source != null) {
                    Log.d(context.getString(R.string.message),"onResponse: source != null");
                    source.setGeoJson(featureCollection);
                    positionCamera();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call,@NonNull Throwable throwable) {
                Log.e(context.getString(R.string.error_message), throwable.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("LogNotTimber")
    private void positionCamera(){
        int endPosition = map.getMarkers().size()-1;
        LatLng locationOne = new LatLng(map.getMarkers().get(0).getPosition().getLatitude(),
                map.getMarkers().get(0).getPosition().getLongitude());
        LatLng locationTwo = new LatLng(map.getMarkers().get(endPosition).getPosition().getLatitude(),
                map.getMarkers().get(endPosition).getPosition().getLongitude());

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(locationOne) // Northeast
                .include(locationTwo) // Southwest
                .build();
        progressDialog.setMessage("Reposicionando la camara en el mapa ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 180)
                ,7000, new MapboxMap.CancelableCallback() {
                @Override
                public void onCancel() {}

                @Override
                public void onFinish() {
                    map.snapshot((Bitmap snapshot) -> {
                        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "map_route_" + System.currentTimeMillis() + ".png");
                        FileOutputStream out;
                        try {
                            out = new FileOutputStream(file);
                            snapshot.compress(Bitmap.CompressFormat.PNG, 90, out);
                            mapBoxUri = Uri.fromFile(file);
                            System.out.println("InterNIC uri local file: "+Uri.fromFile(file));
                            try {
                                out.close();
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                        }
                    });
                    progressDialog.dismiss();
                }
        });
    }
}
