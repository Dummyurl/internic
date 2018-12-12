package com.uca.devceargo.internic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.adapters.StopImageAdapter;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.classes.FireBasePicture;
import com.uca.devceargo.internic.classes.LocationPicker;
import com.uca.devceargo.internic.classes.RouteMapBox;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.Stop;
import com.uca.devceargo.internic.fragments.ProfileFragment;
import com.uca.devceargo.internic.interfaces.FireBaseEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteMapActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener,
        MapboxMap.OnMarkerClickListener, FireBaseEventListener {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private MapView mapView;
    private MapboxMap map;
    private RouteMapBox routeMapBox;
    private List<Stop> stops;
    private LocationPicker locationPicker;
    private FloatingActionButton saveRouteData;
    private FloatingActionButton deleteMarker;
    private FloatingActionButton drawRoute;
    private FloatingActionButton positionMarker;
    private LinearLayout bottomSheet;
    private int cooperativeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // This contains the MapView in XML and needs to be called after the access token is configured.
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_route_map);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        initViews();
        drawRouteInMap();

        KeyboardVisibilityEvent.setEventListener(this, (boolean isOpen) -> {
            if(isOpen){
                showFabActionButtons(false);
            }else{
                showFabActionButtons(true);
            }
        });
    }

    private void showFabActionButtons(boolean show){

        if(show){
            drawRoute.setVisibility(View.VISIBLE);
            saveRouteData.setVisibility(View.VISIBLE);
            positionMarker.setVisibility(View.VISIBLE);
        }else{
            drawRoute.setVisibility(View.INVISIBLE);
            saveRouteData.setVisibility(View.INVISIBLE);
            positionMarker.setVisibility(View.INVISIBLE);

        }
    }

    @SuppressLint({"InflateParams"})
    private void initViews(){

        saveRouteData = findViewById(R.id.save_route);
        deleteMarker = findViewById(R.id.delete_marker);
        drawRoute = findViewById(R.id.draw_route);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_images_stop);
        bottomSheet = findViewById(R.id.bottom_sheet);
        positionMarker = findViewById(R.id.position_marker);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new StopImageAdapter());
        stops = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout dialogEmptyView = (LinearLayout) inflater.inflate(R.layout.dialog_empty_view,null,false);
        LinearLayout stopInformationLayout = findViewById(R.id.stop_information_layout);
        stopInformationLayout.addView(dialogEmptyView);

        locationPicker = new LocationPicker(bottomSheet,stops, positionMarker);
        Intent intent = this.getIntent();
        Route route = new Route();
        route.setName(intent.getStringExtra(ProfileFragment.ROUTE_NAME));
        route.setDescription(intent.getStringExtra(ProfileFragment.ROUTE_DESCRIPTION));
        cooperativeID = getIntent().getIntExtra(ProfileFragment.COOPERATIVE_ID,0);
        locationPicker.setRoute(route);
        saveRouteData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        this.map = mapboxMap;
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.setOnMarkerClickListener(this);
        locationPicker.setMapboxMap(map);
        locationPicker.setMapView(mapView);
        locationPicker.definePositionMarker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_route_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.search_place){
            searchPlace();
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveRouteData(){
        saveRouteData.setOnClickListener((View view) ->{
            if(stops.size()> 1 && routeMapBox.getMapBoxUri() != null){
                FireBaseEventListener fireBaseEventListener = this;
                FireBasePicture firebasePicture = new FireBasePicture(fireBaseEventListener,this);
                firebasePicture.uploadPicture(routeMapBox.getMapBoxUri());
            }else{
                Toast.makeText(getApplicationContext(), "Defina más de dos marcadores y defina una ruta",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadStopsData(String url,ProgressDialog progressDialog){
        Route route = locationPicker.getRoute();
        route.setCost(35);
        route.setStops(stops);
        route.setCooperativeID(cooperativeID);
        route.setUrlImage(url);
        progressDialog.setMessage(getString(R.string.progrees_dialog_message));

        Call<Route> call = Api.instance().postRoute(route);
        call.enqueue(new Callback<Route>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<Route> call,@NonNull Response<Route> response) {
                System.out.print(call.request().url());
                if(response.body() != null){
                    //Finish Activity
                    response.body().setCreateAt("");
                    getIntent().putExtra(ProfileFragment.ROUTE_ID,response.body());
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
                progressDialog.dismiss();
            }
            @SuppressLint("LogNotTimber")
            @Override
            public void onFailure(@NonNull Call<Route> call, @NonNull Throwable throwable) {
                Log.e(getString(R.string.message), new ApiMessage().sendMessageOfResponseAPI(ApiMessage.DEFAULT_ERROR_CODE,
                        getApplicationContext()));
                progressDialog.dismiss();
            }
        });
    }

    public void drawRouteInMap(){

        drawRoute.setOnClickListener((View view) ->{
            if(map.getMarkers().size() > 1){
                routeMapBox = new RouteMapBox(getString(R.string.access_token), map,
                        this);
                routeMapBox.initSource();
                for(Marker marker : map.getMarkers()){
                    marker.hideInfoWindow();
                }
                routeMapBox.setRoute(locationPicker.getRoute());
                routeMapBox.drawRoute();
            }else{
                Toast.makeText(getApplicationContext(),"Defina como mínimo dos marcadores",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPlace() {

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .hint(getString(R.string.search_view_hint))
                        .country("ni")
                        .build(PlaceOptions.MODE_CARDS))
                .build(RouteMapActivity.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    private void deleteMarker(Marker marker){

        deleteMarker.setOnClickListener((View v) ->{
            map.removeMarker(marker);
            deleteMarker.setVisibility(View.GONE);

            locationPicker.showChildrenView(2);

            for (Stop stop : stops) {
                if(marker.getPosition().getLatitude() == stop.getLatitude() &&
                        marker.getPosition().getLongitude() == stop.getLongitude()){
                    stops.remove(stop);
                    break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {


        int visibility = bottomSheet.findViewById(R.id.scroll_view_sheet).getVisibility();
        deleteMarker.setVisibility(View.GONE);

        if(visibility != View.GONE){
            locationPicker.showChildrenView(2);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        deleteMarker.setVisibility(View.VISIBLE);
        locationPicker.showChildrenView(1);

        for (Stop stop : stops) {
            if(marker.getPosition().getLatitude() == stop.getLatitude() &&
                    marker.getPosition().getLongitude() == stop.getLongitude()){

                locationPicker.showInformationRoute(1,stop.getName(),stop.getDescription());
                locationPicker.setStopSelect(stop.getId());
                break;
            }
        }

        deleteMarker(marker);

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            String geoJsonSourceLayerId = "geoJsonSourceLayerId";
            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(
                    new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())});

            // Retrieve and update the source designated for showing a selected location's symbol layer icon
            GeoJsonSource source = map.getSourceAs(geoJsonSourceLayerId);
            if (source != null) {
                source.setGeoJson(featureCollection);
            }
            // Move map camera to the selected location
            CameraPosition newCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                            ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).longitude()))
                    .zoom(17)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 4000);
        }
    }

    @Override
    public void uploadPicture(Uri uri, ProgressDialog progressDialog) {
        uploadStopsData(String.valueOf(uri), progressDialog);
    }

    @Override
    public void uploadPictures() {

    }
}