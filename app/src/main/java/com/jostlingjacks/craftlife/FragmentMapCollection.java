package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentMapCollection extends Fragment implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMarkerClickListener, MapboxMap.OnInfoWindowClickListener {

    MapView mapView;
    View view;
    DataBaseHelper db;
    ArrayList<String[]> mapEntries;
    private MapboxMap map;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;

    private String artName;
    private String artDescription;
    private LatLng artCoordinates;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_collection, container, false);
        // initialise the database helper class instance
        db = new DataBaseHelper(getContext());

        Mapbox.getInstance(getContext(), getString(R.string.access_token));
        mapView = (MapView) view.findViewById(R.id.mapCollectionMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        // get the sharedPreference to retrieve the email of the user...
        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");
        // get the data from the database using the primary key of user's email..
        mapEntries = getMapEntryDataFromDatabase(emailAddress);

        // if you want to open location service ...
        //enableLocation();

        addMarkersOnTheMap(mapEntries);
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        if (mapEntries.size() != 0 ) {
            map.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(Double.valueOf(mapEntries.get(0)[4]), Double.valueOf(mapEntries.get(0)[5])))
                    .zoom(15)
                    .build());

        }

    }

    public ArrayList<String[]> getMapEntryDataFromDatabase(String email){
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        Cursor cursor = db.getAddress(email);
        if (cursor.moveToFirst()){
            do {
                String[] row = new String[8];
                row[0] = cursor.getString(0);
                row[1] = cursor.getString(1);
                row[2] = cursor.getString(2);

                if (cursor.getString(3)== null){
                    row[3] = "-1";
                }else{
                    row[3] = cursor.getString(3);
                }

                row[4] = cursor.getString(4);
                row[5] = cursor.getString(5);
                row[6] = cursor.getString(6);
                row[7] = cursor.getString(7);
                arrayList.add(row);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }


    private void addMarkersOnTheMap(ArrayList<String[]> mapEntries) {

        for(int i = 0; i < mapEntries.size(); i++){
            LatLng latLng = new LatLng(Double.valueOf(mapEntries.get(i)[4]), Double.valueOf(mapEntries.get(i)[5]));
            Marker marker = map.addMarker(new MarkerOptions().position(latLng));

            // like or disliked on title of marker...
            String likedOrDislike = "";
            if (mapEntries.get(i)[3].equals("1")){
                likedOrDislike = " (Liked)";
            }else if (mapEntries.get(i)[3].equals("0")){
                likedOrDislike = " (Disliked)";
            } else {
                likedOrDislike = " (No response yet)";
            }

            marker.setTitle(mapEntries.get(i)[0] + likedOrDislike);
            marker.setSnippet(mapEntries.get(i)[1]);

            // set different markers
            IconFactory iconFactory = IconFactory.getInstance(getContext());
            if (mapEntries.get(i)[3].equals("1")){
                Icon icon = iconFactory.fromResource(R.drawable.good_pin_100p);
                marker.setIcon(icon);
            }else if (mapEntries.get(i)[3].equals("0")){
                Icon icon = iconFactory.fromResource(R.drawable.dislike_pin_100p);
                marker.setIcon(icon);
            } else {
                Icon icon = iconFactory.fromResource(R.drawable.no_response_pin);
                marker.setIcon(icon);
            }
        }
    }

    private void enableLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(getContext())){
            // do some stuff
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        locationEngine = new LocationEngineProvider(getContext()).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null){
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if (locationEngine != null){
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {
        Intent notificationDetail = new Intent(getContext(),NotificationDetailActivity.class);
        Bundle bundle = new Bundle();

        // get the info
        int positionInTheMapEntryList = 0;
        for(int i = 0; i < mapEntries.size(); i++) {
            if (mapEntries.get(i)[4].equals(String.valueOf(marker.getPosition().getLatitude())) && // latitude
                    mapEntries.get(i)[5].equals(String.valueOf(marker.getPosition().getLongitude()))) {  //lng
                positionInTheMapEntryList = i;
            }
        }

        bundle.putString("title", mapEntries.get(positionInTheMapEntryList)[0]);
        bundle.putString("description",marker.getSnippet());
        bundle.putString("address",mapEntries.get(positionInTheMapEntryList)[2]);
        //bundle.putString("time",time);
        bundle.putString("id", mapEntries.get(positionInTheMapEntryList)[6]);
        bundle.putString("time", mapEntries.get(positionInTheMapEntryList)[7]);
        notificationDetail.putExtras(bundle);
        startActivity(notificationDetail);



        return false;
    }

        @Override
    public void onResume() {
        super.onResume();


            // get the sharedPreference to retrieve the email of the user...
            SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
            // get the data from the database using the primary key of user's email..
            mapEntries = getMapEntryDataFromDatabase(userInfoSharedPreferences.getString("UserEmailAddress", ""));

        if (map != null) {
            map.removeAnnotations();
            this.addMarkersOnTheMap(mapEntries);
        }
        mapView.onResume();
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (locationEngine != null){
//            locationEngine.removeLocationUpdates();
//        }
//        if  (locationLayerPlugin!=null){
//            locationLayerPlugin.onStop();
//        }
//        mapView.onStop();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (locationEngine != null){
//            locationEngine.deactivate();
//        }
//        mapView.onDestroy();
//    }

}
