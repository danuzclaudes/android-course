package edu.unc.chongrui.assignment3;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard to display location information and Map.
 * <p>
 * In case Android Emulator needs to update Google Play Services:
 * https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project
 */
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private GeofencingRequest mGeofencingRequest;
    private List<Geofence> mGeofenceList = new ArrayList<>();
    private static GoogleMap mGoogleMap;
    // An object that manages Messages in a Thread
    static Handler mHandler;

    private static Map<String, Marker> mapOfMarkers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using two layout xml files for one Activity.
        // Thanks to code:
        // http://www.java2s.com/Code/Android/UI/UsingtwolayoutxmlfileforoneActivity.htm
        LinearLayout layoutMain = new LinearLayout(this);
        layoutMain.setOrientation(LinearLayout.VERTICAL);
        setContentView(layoutMain);
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dashboard = (LinearLayout) inflate.inflate(R.layout.activity_dashboard, null);
        LinearLayout map = (LinearLayout) inflate.inflate(R.layout.activity_maps, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutMain.addView(dashboard, params);
        layoutMain.addView(map, params);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create Google Location Api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Set up handler to receive message from background thread
        setHandler();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(Constants.LOG_TAG, "Map ready!");
        mGoogleMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(Constants.LOG_TAG, "We are connected to Google Services");

        getLocationUpdateService();
        getGenfences();
        getGeofencingRequest();
        getGeofencePendingIntent();
    }

    /**
     * Set Up a Location Request
     */
    private void getLocationUpdateService() {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //Log.v(Constants.LOG_TAG, "Original Location: " + location.getLatitude() + ", " + location.getLongitude());
            // Move camera of map to current location
            showCameraToCurrentLocation(location);

            // Create a new `LocationRequest` object
            LocationRequest mLocationRequest = new LocationRequest()
                    .setInterval(Constants.LOCATION_UPDATE_INT)
                    .setFastestInterval(Constants.FASTEST_LOCATION_UPDATE_INT)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // Register the location request to location update service
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private void showCameraToCurrentLocation(Location location) {
        // Add a marker at home; `geo fix -79.0561119 35.9445247`
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        // mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f));
    }

    /**
     * Create and populate a list of Geofence objects
     */
    private void getGenfences() {
        mGeofenceList.add(buildGeofenceObject(
                Constants.HOME_REQ_ID,
                Constants.HOME_LAT,
                Constants.HOME_LONG,
                Constants.GEOFENCE_RADIUS_IN_METERS
        ));
        mGeofenceList.add(buildGeofenceObject(
                Constants.F_BROOK_REQ_ID,
                Constants.F_BROOK_LAT,
                Constants.F_BROOK_LONG,
                Constants.GEOFENCE_RADIUS_IN_METERS
        ));
        mGeofenceList.add(buildGeofenceObject(
                Constants.POLK_PLACE_REQ_ID,
                Constants.POLK_PLACE_LAT,
                Constants.POLK_PLACE_LONG,
                Constants.GEOFENCE_RADIUS_IN_METERS
        ));
        mGeofenceList.add(buildGeofenceObject(
                Constants.OLD_WELL_REQ_ID,
                Constants.OLD_WELL_LAT,
                Constants.OLD_WELL_LONG,
                Constants.GEOFENCE_RADIUS_IN_METERS
        ));
        mGeofenceList.add(buildGeofenceObject(
                Constants.UL_REQ_ID,
                Constants.UL_LAT,
                Constants.UL_LONG,
                Constants.GEOFENCE_RADIUS_IN_METERS
        ));
    }

    /**
     * Use Geofence.Builder to create a geofence,
     * setting the desired radius, duration, and
     * transition types for the geofence.
     *
     * @param requestId
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    private Geofence buildGeofenceObject(String requestId,
                                         double latitude,
                                         double longitude,
                                         float radius) {
        return new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    /**
     * Specify geofences and initial triggers.
     * <p>
     * Specifying INITIAL_TRIGGER_ENTER tells Location services that
     * GEOFENCE_TRANSITION_ENTER should be triggered if the the device
     * is already inside the geofence.
     */
    private void getGeofencingRequest() {
        mGeofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList)
                .build();
    }

    /**
     * Define an Intent for geofence transitions.
     * Add geofences by Google API client, GeoFencingRequest object,
     * and the PendingIntent object.
     */
    private void getGeofencePendingIntent() {
        // Handle Geofence Transitions by another service
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // Use `FLAG_UPDATE_CURRENT` so that we get the same pending intent
        // back when calling `addGeofences()` and `removeGeofences()`.
        PendingIntent pendingIntent = PendingIntent.getService(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add the geofences and check SecurityException
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    mGeofencingRequest,
                    pendingIntent);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(Constants.LOG_TAG, "Connection failed: " + connectionResult.getErrorCode() + "");
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        GeofenceMedia.getInstance().pauseSong();
    }

    @Override
    public void onLocationChanged(Location location) {
        updateDashboard(location);

    }

    /**
     * Update location coordinates and address on Dashboard
     * as soon as the location has changed.
     *
     * @param location
     */
    private void updateDashboard(Location location) {
        // Update location coordinates on Dashboard
        // Log.v(Constants.LOG_TAG, location.getLatitude() + ", " + location.getLongitude());
        Geocoder g = new Geocoder(this, Locale.getDefault());
        StringBuilder coordinatesText = new StringBuilder("\nLatitude: ")
                .append(location.getLatitude())
                .append("\nLongitude: ")
                .append(location.getLongitude());
        TextView coordinates = (TextView) findViewById(
                R.id.geo_location_coordinates);
        coordinates.setText(coordinatesText.toString());

        // Update address on Dashboard
        StringBuilder addressText = new StringBuilder("Address:\n");
        try {
            List<Address> la = g.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            // Log.v(Constants.LOG_TAG, la.get(0).toString());
            addressText.append(la.get(0).getAddressLine(0)).append("\n")
                    .append(la.get(0).getAddressLine(1))
                    .append(la.get(0).getAddressLine(2));
            TextView address = (TextView) findViewById(R.id.geo_address);
            address.setText(addressText.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * http://www.biemmeitalia.net/blog/bundle-android/
     */
    private void setHandler() {
        mHandler = new Handler() {
            /**
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             *
             * @param inputMessage
             */
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle bundle = inputMessage.getData();
                if(inputMessage.what == 1) {
                    showMsg("Entering: " + bundle.get("Id"));
                    Log.v(Constants.LOG_TAG, "ENTERING: " + bundle.get("Id"));
                    MapsActivity.addMarker(
                            bundle.getDouble("lat"),
                            bundle.getDouble("lng"),
                            bundle.getString("Id")
                    );
                }
                else if(inputMessage.what == 0) {
                    showMsg("Exiting: " + bundle.get("Id"));
                    Log.v(Constants.LOG_TAG, "EXITING");
                    MapsActivity.removeMarkers();
                }
                super.handleMessage(inputMessage);
            }

        };
    }

    private static void addMarker(double lat, double lng, String requestId) {
        LatLng currentLatLng = new LatLng(lat, lng);
        Marker marker = mGoogleMap.addMarker(
                new MarkerOptions().position(currentLatLng).title(requestId)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f));
        mapOfMarkers.put(requestId, marker);
    }

    private static void removeMarkers() {
        for(String requestId : mapOfMarkers.keySet()) {
            mapOfMarkers.get(requestId).remove();
        }
        mapOfMarkers.clear();
    }

    protected void showMsg(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

}