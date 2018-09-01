package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;


public class NotificationDetailActivity extends AppCompatActivity {

    private Button button;
    private TextView titleTextView,  descTextView,  addressTextView,  timeTextView, addTV, timeTV;
    private ImageView actionImage;
    private MapView mMapView;
    private MapboxMap mMapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        String type = details.getString("type");
        String title = details.getString("title");
        String description  = details.getString("description");
        String address = details.getString("address");
        String lat = details.getString("address");
        String lon = details.getString("lon");
        String time = details.getString("time");


        titleTextView = (TextView) findViewById(R.id.noti_title);
        descTextView = (TextView) findViewById(R.id.noti_desc);
        addressTextView = (TextView) findViewById(R.id.noti_add);
        timeTextView = (TextView) findViewById(R.id.noti_time);
        addTV = (TextView)findViewById(R.id.address);
        timeTV = (TextView) findViewById(R.id.time);
        actionImage = (ImageView) findViewById(R.id.imageAction);
        //mMapView = (MapView)findViewById(R.id.mapView);


        if (address != null || time != null)
        {
            addTV.setVisibility(View.VISIBLE);
            timeTextView.setVisibility(View.INVISIBLE);
        }

        if (title.toLowerCase().contains("water"))
        {
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.drinkwater);
        } else if (title.toLowerCase().contains("walk")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.walking);
        } else if (title.toLowerCase().contains("concert")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.stage);
        }else if (title.toLowerCase().contains("art")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.art);
        } else if (title.equals("Stand Up")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.coach);
        }else if (title.equals("Sitting Meditation")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.meditation);
        } else if (title.equals("Looking outside the window")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.curtain);
        }

        titleTextView.setText(title);
        descTextView.setText(description);
        addressTextView.setText(address);

        if (lat != null && lon != null){

            //mMapView.onCreate(savedInstanceState);
           // mMapView.setVisibility(View.VISIBLE);
            try {
                Double dLat = Double.parseDouble(lat);
                Double dLon = Double.parseDouble(lon);

                final LatLng geo_code = new LatLng(dLat,dLon);

            } catch (Exception e)
            {
                Log.d("Exception", "Error");
            }

//            mMapView.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(MapboxMap mapboxMap) {
//                    mMapboxMap = mapboxMap;
//                    mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geo_code, 11));
//                    addMarker(mMapboxMap, geo_code);
//                }
//            });

        }


        //timeTextView.setText(time);


        button = (Button) findViewById(R.id.getbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
        //moveTaskToBack(true);
    }

    private void addMarker(MapboxMap mapboxMap, LatLng geocode) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(geocode);
        mapboxMap.addMarker(markerOptions);
    }




}
