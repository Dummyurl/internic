package com.uca.devceargo.internic.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.classes.LocalDate;
import com.uca.devceargo.internic.classes.LocalDistance;
import com.uca.devceargo.internic.classes.RouteUserMapBox;
import com.uca.devceargo.internic.entities.Cooperative;
import com.uca.devceargo.internic.entities.Route;
import com.uca.devceargo.internic.entities.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener,
        MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener{
    private static final String TAG = "MainActivity";
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private Location originLocation;
    private Marker markerSelected;
    private com.uca.devceargo.internic.entities.Location myLocation;
    private List<Route> routes;
    private List<Stop> stops;
    private RouteUserMapBox routeUserMapBox;
    private List<Cooperative> cooperatives;
    private LinearLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_map);
        initViews();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        routes = new ArrayList<>();
        stops = new ArrayList<>();
        myLocation = new com.uca.devceargo.internic.entities.Location();
        getCooperatives();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        bottomSheet = findViewById(R.id.bottom_user_sheet);
    }

    private void initFabButtons(){
        FloatingActionButton defineLocation = findViewById(R.id.define_user_location);
        FloatingActionButton drawUserRoute = findViewById(R.id.draw_user_route);

        defineLocation.setOnClickListener((View view)->{
            if(originLocation != null){
                enableLocation();
            }
        });

        drawUserRoute.setOnClickListener((View view) ->{
            if(originLocation != null){
                if(markerSelected != null){
                    /*for(Marker marker : map.getMarkers()){
                        marker.hideInfoWindow();
                    }*/
                    routeUserMapBox.setPoints(definePointsUserRoute());
                    routeUserMapBox.setBottomSheet(bottomSheet);
                    routeUserMapBox.setTypeRoute(RouteUserMapBox.PROFILE_WALKING);
                    routeUserMapBox.initSource();
                    routeUserMapBox.drawRoute();
                }else{
                    Toast.makeText(getApplicationContext(),"Selecione un destino",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(),"No se ha definido su ubicación aún",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Point> definePointsUserRoute(){
        List<Point> points = new ArrayList<>();
        Point point1 = Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
        Point point2 = Point.fromLngLat(markerSelected.getPosition().getLongitude(),
                markerSelected.getPosition().getLatitude());
        points.add(point1);
        points.add(point2);
        return points;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
        enableLocation();
        routeUserMapBox = new RouteUserMapBox(getString(R.string.access_token),map,this.getApplicationContext());
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.setOnMarkerClickListener(this);
        initFabButtons();
    }

    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            Toast.makeText(getApplicationContext(),"Accediendo a la información GPS del dispositivo",Toast.LENGTH_SHORT).show();
            initializeLocationEngine();
        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        LocationComponent locationComponent = map.getLocationComponent();
        locationComponent.activateLocationComponent(this);
        locationComponent.setLocationComponentEnabled(true);
        LocationEngine locationEngine = new LocationEngineProvider(getApplicationContext()).obtainBestLocationEngineAvailable();

        Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation != null){
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
            myLocation.setAccuracy(originLocation.getAccuracy());
            myLocation.setAltitude(originLocation.getAltitude());
            myLocation.setLatitude(originLocation.getLatitude());
            myLocation.setLongitude(originLocation.getLongitude());
            //makeGeocodeSearch(Point.fromLngLat(myLocation.getLongitude(),myLocation.getLatitude()));
        }

    }

    @SuppressLint("LogNotTimber")
    private void makeGeocodeSearch(Point point) {
        try {
            // Build a Mapbox geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.access_token))
                    .query(point)
                    .country("ni")
                    .limit(1)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();

                    if (results.size() > 0) {

                        CarmenFeature feature = results.get(0);
                        feature.text();
                        feature.placeName();

                    } else {
                        sendMessageInSnackbar(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call,@NonNull Throwable throwable) {

                    sendMessageInSnackbar(ApiMessage.DEFAULT_ERROR_CODE);

                    Stop stop = new Stop();
                    stop.setLatitude(point.latitude());
                    stop.setLongitude(point.longitude());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e(getString(R.string.error_message_api), servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()),13.0));
    }

    @SuppressLint("LogNotTimber")
    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        Log.i("HOLA","PASO 8");
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location != null){
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onStart() {
        super.onStart();
        try {
            mapView.onStart();
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
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
        mapView.onDestroy();
    }

    private void sendMessageInSnackbar(int code){

        String message = new ApiMessage().sendMessageOfResponseAPI(code,getApplicationContext());
        Timber.i(message);
        Snackbar.make(mapView,message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void getCooperatives(){
        Call<List<Cooperative>> call = Api.instance().getCooperativesWithLocation();
        call.enqueue(new Callback<List<Cooperative>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cooperative>> call,@NonNull Response<List<Cooperative>> response) {

                if(response.body() != null){
                    cooperatives = response.body();
                    setInMapCooperatives(cooperatives);
                    setInMapStops();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cooperative>> call,@NonNull Throwable throwable) {

            }
        });
    }

    private void setInMapCooperatives(List<Cooperative> cooperatives){
        boolean band = false;
        for (Cooperative cooperative : cooperatives){
            if(cooperative.getRoutes() != null){
                routes.addAll(cooperative.getRoutes());
            }
            for(Marker marker : map.getMarkers()){
                if(cooperative.getLocation().getLatitude() == marker.getPosition().getLatitude() ||
                        cooperative.getLocation().getLongitude() == marker.getPosition().getLongitude()){
                    band = true;
                    break;
                }
            }
            if(!band){
                MarkerOptions markerOptions = new MarkerOptions().position(
                        new LatLng(cooperative.getLocation().getLatitude(), cooperative.getLocation().getLongitude()));
                markerOptions.setTitle(cooperative.getName());
                Spanned text;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    text = Html.fromHtml(cooperative.getDescription(), Html.FROM_HTML_MODE_COMPACT);
                } else {
                    text = Html.fromHtml(cooperative.getDescription());
                }

                markerOptions.setSnippet(text.toString());
                Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.blue_marker);
                markerOptions.setIcon(icon);
                map.addMarker(markerOptions);
                band = false;
            }
        }
    }

    private void setInMapStops(){

        for(Route route : routes){
            if(route.getStops() != null){
                stops.addAll(route.getStops());

                for(Stop stop : route.getStops()){
                    MarkerOptions markerOptions = new MarkerOptions().position(
                            new LatLng(stop.getLatitude(), stop.getLongitude()));
                    markerOptions.setTitle(stop.getName());
                    markerOptions.setSnippet("Ruta: "+route.getName());
                    Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.purple_marker);
                    markerOptions.setIcon(icon);
                    map.addMarker(markerOptions);
                }
            }

        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        markerSelected = null;
    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onMarkerClick(@NonNull Marker marker) {
        markerSelected = marker;
        String value = marker.getSnippet().substring(0,4);
        System.out.println("InterNIC: "+value);
        if(value.equalsIgnoreCase(value)){
            System.out.println("InterNIC: Funcionó");
            for(Route route : routes){
                for(Stop stop : route.getStops()){
                    Double longitude = stop.getLongitude();
                    Double latitude = stop.getLatitude();
                    if(marker.getPosition().getLongitude() == longitude &&
                            marker.getPosition().getLatitude() == latitude){
                        showInformation(route);
                        break;
                    }
                }
            }
        }
        return false;
    }

    private void showInformation(Route route){
        TextView bottomSheetTitle = findViewById(R.id.user_route_title);
        bottomSheetTitle.setText(getString(R.string.route_map_box_title_bottom_sheet));
        TextView routeTitle = findViewById(R.id.user_route_name);
        TextView routeDescription = findViewById(R.id.user_route_description);
        TextView routeTime = findViewById(R.id.user_tim_route);
        TextView userDistance = findViewById(R.id.user_distan_route);
        Button drawRoute = findViewById(R.id.draw_route_coop);

        routeTitle.setText(route.getName());
        routeDescription.setText(route.getDescription());
        routeTime.setText(new LocalDate().getHourInString(route.getTime()));
        userDistance.setText(new LocalDistance().getStringDistance(route.getDistance()));

        drawRoute.setOnClickListener((View view) ->{
            routeUserMapBox.setPoints(setPointRoute(route));
            routeUserMapBox.setBottomSheet(bottomSheet);
            routeUserMapBox.setTypeRoute(RouteUserMapBox.PROFILE_DRIVING);
            routeUserMapBox.initSource();
            routeUserMapBox.drawRoute();
        });
    }

    private List<Point> setPointRoute(Route route){
        List<Point> points = new ArrayList<>();
        for(Stop stop : route.getStops()){
            points.add(Point.fromLngLat(stop.getLongitude(),stop.getLatitude()));
        }

        return points;
    }
}