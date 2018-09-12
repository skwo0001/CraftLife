package com.jostlingjacks.craftlife;

/**
 * This class is for the activity that gives the user the navigation to the art event place
 * or other art places....
 */

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
//import com.mapbox.services.android.navigation.ui.v5.NavigationActivity;
//import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
//import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
//import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
//import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private Button startNavigationButton;
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;

    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MapActivity";

    private String artName;
    private String artDescription;
    private LatLng artCoordinates;

    private DirectionsRoute currentRoute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map_navigation);
        mapView = (MapView) findViewById(R.id.mapquestMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        startNavigationButton = findViewById(R.id.takeMeToTheArtPlaceButton);

        startNavigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Launch Navigation UI
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        //.origin(originPosition)
                        //.destination(destinationPosition)
                        //.shouldSimulateRoute(true)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(MapActivity.this, options);
            }
        });

        // get the intent from the previous activity...
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String artAddress = bundle.getString("ArtAddress");
        artName = bundle.getString("ArtName");
        artDescription = bundle.getString("ArtDescription");

        // convert the address string to coordinates....
       artCoordinates = getLocationFromAddress(getApplicationContext(), artAddress);
        //artCoordinates = new LatLng(-37.918349,145.129836);


    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();

        destinationMarker = map.addMarker(new MarkerOptions().position(artCoordinates));


        destinationPosition = Point.fromLngLat(artCoordinates.getLongitude(), artCoordinates.getLatitude());
        originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        destinationMarker.setTitle(artName);
        destinationMarker.setSnippet(artDescription);

        getRoute(originPosition, destinationPosition);
    }

    private void getRoute(Point origin, Point destination){
        String token = Mapbox.getAccessToken();
        NavigationRoute.builder(getApplicationContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if  (response.body() == null){
                            Log.e(TAG, "No routes found, check right user and access token");
                            return;
                        }else if (response.body().routes().size() == 0){
                            Log.e(TAG, "No routes found");
                            // can show a toast, no routes found...
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        //initisal the route
                        if (navigationMapRoute != null){
                            navigationMapRoute.removeRoute();
                        }else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                    }
                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error!!:" + t.getMessage());
                    }
                });
    }

//    private void calculateRoute() {
////        Location userLocation = mapboxMap.getMyLocation();
////        if (userLocation == null) {
////            Timber.d("calculateRoute: User location is null, therefore, origin can't be set.");
////            return;
////        }
//
//
//        NavigationRoute.builder().getRoute(origin, destination, new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                DirectionsRoute route = response.body().getRoutes().get(0);
//                NavigationActivity.this.route = route;
//                drawRouteLine(route);
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                Timber.e("onFailure: navigation.getRoute()", throwable);
//            }
//        });
//    }

    private void enableLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            // do some stuff
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
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
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
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
    protected void onStart() {
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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null){
            locationEngine.removeLocationUpdates();
        }
        if  (locationLayerPlugin!=null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
        if (locationEngine != null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }


    public LatLng getLocationFromAddress(Context context, String inputtedAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng resLatLng = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(inputtedAddress, 5);
            if (address == null) {
                return null;
            }

            if (address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return resLatLng;
    }


}
