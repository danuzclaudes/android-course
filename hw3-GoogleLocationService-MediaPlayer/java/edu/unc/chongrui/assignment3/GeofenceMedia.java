package edu.unc.chongrui.assignment3;

import android.content.Context;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


/**
 * Play music and display marker on map
 * according to the triggered geofence.
 *
 * Apply Singleton Design pattern.
 */
public class GeofenceMedia
        implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;
    private static GeofenceMedia mGeofenceMedia;
    private Bundle bundle;

    private GeofenceMedia(){}

    public static GeofenceMedia getInstance() {
        if(mGeofenceMedia == null) mGeofenceMedia = new GeofenceMedia();
        return mGeofenceMedia;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mMediaPlayer != null) mMediaPlayer.start();
    }

    protected void playSong(Context context, String requestId) {
        Log.v("LOC", "Playing song with: " + requestId);
        switch (requestId) {
            case "FB":
                mMediaPlayer = MediaPlayer.create(context, R.raw.fb);
                break;
            case "Polk Place":
                mMediaPlayer = MediaPlayer.create(context, R.raw.polk_place);
                break;
            default:
                mMediaPlayer = MediaPlayer.create(context, R.raw.old_well);
        }
        if(mMediaPlayer != null) mMediaPlayer.start();
    }

    protected void pauseSong() {
        Log.v("LOC", "Pause song.");
        if(mMediaPlayer != null) mMediaPlayer.pause();
    }

    protected void showMarker(Location location, String requestId) {
        // Create the bundle and add data to the bundle
        bundle = new Bundle();
        bundle.putDouble("lat", location.getLatitude());
        bundle.putDouble("lng", location.getLongitude());
        bundle.putString("Id", requestId);

        // Create a message from the message
        Message msg = MapsActivity.mHandler.obtainMessage();
        msg.what = 1;
        msg.setData(bundle);
        MapsActivity.mHandler.sendMessage(msg);
    }

    protected void removeMarkers() {
        // Create a message from the message
        Message msg = MapsActivity.mHandler.obtainMessage();
        msg.what = 0;
        MapsActivity.mHandler.sendMessage(msg);
    }
}
