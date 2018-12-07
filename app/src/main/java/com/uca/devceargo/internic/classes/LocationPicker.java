package com.uca.devceargo.internic.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.clans.fab.FloatingActionButton;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.Stop;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LocationPicker {
    private Context context;
    private MapView mapView;
    private LinearLayout bottomSheet;
    private MapboxMap mapboxMap;
    private Marker droppedMarker;
    private List<Stop> stops;
    private Route route;
    private int stopSelect;
    private SimpleDraweeView hoveringMarker;
    private FloatingActionButton selectLocationButton;

    public LocationPicker(LinearLayout bottomSheet, List<Stop> stops, FloatingActionButton floatingActionButton) {
        this.selectLocationButton = floatingActionButton;
        this.stops = stops;
        this.bottomSheet = bottomSheet;

        Button editRouteData = bottomSheet.findViewById(R.id.edit_route_data);
        Button editStopData = bottomSheet.findViewById(R.id.edit_stop_data);
        editRouteData.setOnClickListener((View view) ->
                showDialogEditData(2));

        editStopData.setOnClickListener((View view) ->
            showDialogEditData(1));
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
        showRouteData();
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
        this.context = mapView.getContext();
    }

    public void setMapboxMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    public void setStopSelect(int stopSelect) {
        this.stopSelect = stopSelect;
    }

    private void showRouteData(){
        TextView routeTitle = bottomSheet.findViewById(R.id.route_name);
        TextView routeDescription = bottomSheet.findViewById(R.id.route_description);
        routeTitle.setText(route.getName());
        routeDescription.setText(route.getDescription());
    }

    public void definePositionMarker() {
        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
        // This is done by using an image view with the default marker found in the SDK. You can
        // swap out for your own marker image, just make sure it matches up with the dropped marker.
        hoveringMarker = new SimpleDraweeView(context);
        hoveringMarker.setImageResource(R.drawable.ic_marker_bus);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        hoveringMarker.setLayoutParams(params);
        hoveringMarker.setVisibility(View.GONE);
        mapView.addView(hoveringMarker);

        // Button for user to drop marker or to pick marker back up.
        selectLocationButton.setOnClickListener((View view) -> {

            if (hoveringMarker.getVisibility() == View.GONE) {
                selectLocationButton.setColorNormal(ContextCompat.getColor(context, R.color.colorAccent));
                // Lastly, set the hovering marker back to visible.
                hoveringMarker.setVisibility(View.VISIBLE);

                droppedMarker = null;
            }else{
                if (droppedMarker == null) {
                    // We first find where the hovering marker position is relative to the mapboxMap.
                    // Then we set the visibility to gone.
                    float coordinateX = hoveringMarker.getLeft() + (hoveringMarker.getWidth() / 2);
                    float coordinateY = hoveringMarker.getBottom();
                    float[] coords = new float[]{coordinateX, coordinateY};
                    final Point latLng = Point.fromLngLat(mapboxMap.getProjection().fromScreenLocation(
                            new PointF(coords[0], coords[1])).getLongitude(), mapboxMap.getProjection().fromScreenLocation(
                            new PointF(coords[0], coords[1])).getLatitude());
                    hoveringMarker.setVisibility(View.GONE);
                    // Transform the appearance of the button to become the cancel button
                    /*selectLocationButton.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.colorAccent));*/

                    // Placing the marker on the mapboxMap as soon as possible causes the illusion
                    // that the hovering marker and dropped marker are the same.
                    droppedMarker = mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latLng.latitude(), latLng.longitude()))
                    );
                    int position = mapboxMap.getMarkers().size() - 1;
                    bottomSheet.findViewById(R.id.progress_bar_stop).setVisibility(View.VISIBLE);
                    makeGeocodeSearch(latLng,position);
                    // Switch the button apperance back to select a location.
                    selectLocationButton.setColorNormal(ContextCompat.getColor(context, R.color.colorWhite));
                }
            }
        });
    }


    public void showChildrenView(int itemSelect){

        switch (itemSelect){
            case 1:
                bottomSheet.findViewById(R.id.progress_bar_stop).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.dialog_empty_view).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.edit_stop_data).setVisibility(View.VISIBLE);
                bottomSheet.findViewById(R.id.stop_information_child).setVisibility(View.VISIBLE);
                break;
            case 2:
                bottomSheet.findViewById(R.id.stop_information_child).setVisibility(View.GONE);
                bottomSheet.findViewById(R.id.edit_stop_data).setVisibility(View.INVISIBLE);
                bottomSheet.findViewById(R.id.dialog_empty_view).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }

    public void showInformationRoute(int itemSelect, String name, String description){
        switch (itemSelect){
            case 1:

                TextView placeName = bottomSheet.findViewById(R.id.place_name);
                TextView placeDescription = bottomSheet.findViewById(R.id.place_description);
                placeName.setText(name);
                placeDescription.setText(description);
                break;
            case 2:

                TextView routeTitle = bottomSheet.findViewById(R.id.route_name);
                TextView routeDescription = bottomSheet.findViewById(R.id.route_description);
                routeTitle.setText(name);
                routeDescription.setText(description);
                break;
        }
    }

    private void saveChangeStop(int position, String name, String description){

        if( name.isEmpty() || description.isEmpty()){
            Toast.makeText(context,context.getString(R.string.empty_fields_message), Toast.LENGTH_LONG).show();
        }else if(stops.size() > 0){

            stops.get(position).setName(name);
            stops.get(position).setDescription(description);
            mapboxMap.getMarkers().get(position).setTitle(name);
            mapboxMap.getMarkers().get(position).setSnippet(description);

            /*BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/
        }
    }

    @SuppressLint({"InflateParams"})
    private void showDialogEditData(int opc){

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_new_route, null);

        EditText newRouteName = view.findViewById(R.id.new_route_name);
        EditText newRouteDescription = view.findViewById(R.id.new_route_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Siguiente",(DialogInterface dialog, int id) ->{});
        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) ->
                Toast.makeText(context,"Edición cancelada", Toast.LENGTH_SHORT).show());

        builder.setView(view);
        if(opc == 1){

            builder.setTitle(context.getString(R.string.layout_stop_title));
            TextView placeName = bottomSheet.findViewById(R.id.place_name);
            TextView placeDescription = bottomSheet.findViewById(R.id.place_description);
            newRouteName.setText(placeName.getText().toString());
            newRouteDescription.setText(placeDescription.getText());

        }else{
            builder.setTitle(context.getString(R.string.layout_route_title));
            TextView routeTitle = bottomSheet.findViewById(R.id.route_name);
            TextView routeDescription = bottomSheet.findViewById(R.id.route_description);
            newRouteName.setText(routeTitle.getText());
            newRouteDescription.setText(routeDescription.getText());

        }

        AlertDialog dialog = builder.create();

        if(view.getParent() != null)
            ((ViewGroup)view.getParent()).removeView(view);

        dialog.setView(view);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View v) -> {

            String name = newRouteName.getText().toString();
            String description = newRouteDescription.getText().toString();

            if(name.isEmpty() || description.isEmpty()){
                Toast.makeText(context,context.getText(R.string.empty_fields_message), Toast.LENGTH_SHORT).show();
            }else if(opc == 1){
                saveChangeStop(stopSelect,name,description);
                showInformationRoute(1,name,description);
                dialog.dismiss();
            }else if (opc == 2){
                showInformationRoute(2,name,description);
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("LogNotTimber")
    private void makeGeocodeSearch(Point point, int position) {
        try {
            // Build a Mapbox geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(context.getString(R.string.access_token))
                    .query(point)
                    .country("ni")
                    .limit(1)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();
                    System.out.println(call.request().url());
                    Stop stop = new Stop();
                    stop.setLatitude(point.latitude());
                    stop.setLongitude(point.longitude());
                    stop.setId(position);

                    if (results.size() > 0) {

                        CarmenFeature feature = results.get(0);

                        TextView placeName = bottomSheet.findViewById(R.id.place_name);
                        TextView placeDescription = bottomSheet.findViewById(R.id.place_description);

                        placeName.setText(feature.text());
                        placeDescription.setText(feature.placeName());

                        stop.setName(feature.text());
                        stop.setDescription(feature.placeName());
                        mapboxMap.getMarkers().get(stop.getId()).setTitle(stop.getName());
                        mapboxMap.getMarkers().get(stop.getId()).setSnippet(stop.getDescription());

                    } else {
                        sendMessageInSnackbar(response.code());
                    }
                    showChildrenView(1);
                    setStopSelect(position);
                    stops.add(stop);
                }

                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call,@NonNull Throwable throwable) {

                    sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);

                    Stop stop = new Stop();
                    stop.setLatitude(point.latitude());
                    stop.setLongitude(point.longitude());
                    stops.add(stop);
                    showChildrenView(1);
                    setStopSelect(position);
                }
            });
        } catch (ServicesException servicesException) {
            Log.e(context.getString(R.string.error_message_api), servicesException.toString());
            servicesException.printStackTrace();
            showChildrenView(1);
            setStopSelect(position);
        }
    }

    private void sendMessageInSnackbar(int code){

        String message = new ApiMessage().sendMessageOfResponseAPI(code,context);
        Timber.i(message);
        Snackbar.make(mapView,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
