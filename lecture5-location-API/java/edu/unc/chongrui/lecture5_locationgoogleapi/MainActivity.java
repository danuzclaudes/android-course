package edu.unc.chongrui.lecture5_locationgoogleapi;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleApiClient apiClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of GoogleAPIClient.
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("LOC", "Connected to Google Services");
        try {
            // Use the fused location provider's getLastLocation() method
            // to retrieve the device location
            Location lastloc = LocationServices.FusedLocationApi
                    .getLastLocation(apiClient);
            Log.v("LOC", "" + lastloc.getLatitude() + ", " + lastloc.getLongitude());

            LocationRequest locRequest = new LocationRequest();
            locRequest.setInterval(1000);  // preferred rate
            locRequest.setFastestInterval(500);
            locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Receive location updates by implementing LocationListener interface
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, this);
            // stop listening:
            // LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // display a location address
        Log.v("GPS", location.getLatitude() + ", " + location.getLongitude());
        // Goecoder converts Lat/Long to an Address
        Geocoder g = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> la = g.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.v("Address", la.get(0).toString());

            //la = g.getFromLocationName("Sitterson Hall", 1);
            //Log.v("From name", la.get(0).toString());

        }catch (Exception ex) {

        }
    }
}
