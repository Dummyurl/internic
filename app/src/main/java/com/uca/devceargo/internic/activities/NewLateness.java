package com.uca.devceargo.internic.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.uca.devceargo.internic.R;


public class NewLateness extends AppCompatActivity implements OnMapReadyCallback {

    LayoutInflater layoutInflater;
    FloatingActionButton endLateness;
    EditText title;
    EditText description;
    MapView mapView;
    MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_new_lateness);
        mapView = findViewById(R.id.newsMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initViews();
        initActions();
    }

    public void initViews(){
        layoutInflater = LayoutInflater.from(getApplicationContext());
        endLateness = findViewById(R.id.new_lateness_next);
    }

    private void initActions(){
        //endLateness.setOnClickListener(view -> showDialog());

        title = findViewById(R.id.standard_title);
        description = findViewById(R.id.standard_description);
    }
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
    }
}
