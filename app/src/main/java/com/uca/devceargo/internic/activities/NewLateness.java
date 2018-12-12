package com.uca.devceargo.internic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.uca.devceargo.internic.MainActivity;
import com.uca.devceargo.internic.R;
import com.uca.devceargo.internic.api.Api;
import com.uca.devceargo.internic.api.ApiMessage;
import com.uca.devceargo.internic.entities.Location;
import com.uca.devceargo.internic.entities.News;
import com.uca.devceargo.internic.entities.Stop;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewLateness extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener,
        MapboxMap.OnMarkerClickListener {
    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static int TYPE_REQUEST;
    LayoutInflater layoutInflater;
    FloatingActionButton endLateness;
    EditText title;
    EditText description;
    EditText placeTitle;
    EditText placeDescription;
    MapView mapView;
    MapboxMap map;
    Location location;
    View view;
    View view2;

    TextView uploadingText;
    AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_new_lateness);
        mapView = findViewById(R.id.newsMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        location = new Location();

        initViews();
        initActions();
    }


    @SuppressLint("RestrictedApi")
    public void initViews(){
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.dialog_new_news, null);
        title = view.findViewById(R.id.dialog_news_tile);
        description = view.findViewById(R.id.dialog_news_description);
        placeDescription = view.findViewById(R.id.dialog_news_place_description);
        placeTitle = view.findViewById(R.id.dialog_news_place_title);

        endLateness = findViewById(R.id.new_lateness_next);
        endLateness.setVisibility(View.GONE);

        dialog = buildDialogUploading();

    }

    private void initActions(){
        endLateness.setOnClickListener(view -> buildDialog());
    }

    private boolean validateFields(){
        boolean isError = false;

        if(TYPE_REQUEST != 2){
            if(title.getText().toString().isEmpty())
                isError = true;
            if(description.getText().toString().isEmpty())
                isError = true;
        }

        if(placeTitle.getText().toString().isEmpty())
            isError = true;
        if (placeDescription.getText().toString().isEmpty())
            isError = true;

        return isError;

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String text;
        if(TYPE_REQUEST == 2) {
            text = "listo";
            title.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
        else text = "Subir";

        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int id) -> {})
        .setPositiveButton(text, (DialogInterface dialog, int id) -> {});

        if(view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        builder.setView(view);
        builder.setTitle("Complete los campos");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            if (!validateFields()){
                location.setName(placeTitle.getText().toString());
                location.setDescription(placeDescription.getText().toString());

                if(TYPE_REQUEST == 2) {
                    Intent i = new Intent();
                    i.putExtra("latitude", location.getLatitude());
                    i.putExtra("longitude", location.getLongitude());
                    i.putExtra("altitude", location.getAltitude());
                    i.putExtra("name", location.getName());
                    i.putExtra("description", location.getDescription());
                    setResult(22, i);
                    finish();
                }else {
                    postLocation();
                    alertDialog.dismiss();
                    dialog.show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Faltan algunos campos por definir", Toast.LENGTH_SHORT).show();
            }

        });
    }




    private AlertDialog buildDialogUploading(){
        LayoutInflater layoutInflater2 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view2 = layoutInflater2.inflate(R.layout.dialog_uploading_images, null);
        uploadingText = view2.findViewById(R.id.text_uploading);
        uploadingText.setText("Publicando noticia...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if(view2.getParent() != null) {
            ((ViewGroup)view2.getParent()).removeView(view2);
        }
        return builder.create();
    }

    private void postLocation(){
        Call<Location> call = Api.instance().postLocation(location);
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                if(response.body()!= null){
                    postNews(response.body().getId());
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), "nulos: "+response.code(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postNews(int locationID){
        News news = new News();
        news.setTitle(title.getText().toString());
        news.setDescription(description.getText().toString());
        news.setLocationID(locationID);
        news.setTypeNewID(1);
        news.setCooperativeID(4);

        Call<News> call = Api.instance().postNews(news);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.body()!= null){
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                dialog.dismiss();
            }
        });
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
    public void onMapClick(@NonNull LatLng point) {
        if(map.getMarkers().size() != 0)
            map.removeMarker(map.getMarkers().get(0));
        if(map.getMarkers().size() == 0)
            map.addMarker(new MarkerOptions().position(point));
        Point point2 = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        makeGeocodeSearch(point2);

        location.setAltitude(point.getAltitude());
        location.setLatitude(point.getLatitude());
        location.setLongitude(point.getLongitude());
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
                .build(NewLateness.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    @SuppressLint("LogNotTimber")
    private void makeGeocodeSearch(Point point) {
        try {
            ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setMessage(getString(R.string.progrees_dialog_firebase_message));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            // progressDialog.show();
            // Build a Mapbox geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getApplicationContext().getString(R.string.access_token))
                    .query(point)
                    .country("ni")
                    .limit(1)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        placeDescription.setText(feature.placeName());
                        placeTitle.setText(feature.text());


                        map.getMarkers().get(0).setTitle(feature.text());
                        map.getMarkers().get(0).setSnippet(feature.placeName());
                        //progressDialog.dismiss();
                        endLateness.setVisibility(View.VISIBLE);

                    } else {
                        //sendMessageInSnackbar(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
                }
            });
        } catch (ServicesException servicesException) {

        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        return false;
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
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_route_map_menu, menu);
        return true;
    }


}
