package edu.unc.chongrui.assignment3;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Handle Geofence Transitions.
 * <p>
 * The service will receive an Intent sent out from PendingIntent when
 * the user has entered or exited a geofence. This service obtains the
 * geofencing event from the intent, determines the type of Geofence
 * transition(s), and determines which of the defined geofences was
 * triggered.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    /**
     * Create an IntentService default constructor.
     * <p>
     * BZ: must set up for Geofence Monitoring by
     * adding an element specifying the service name as
     * `<service android:name=".GeofenceTransitionsIntentService"/>`.
     * This will use the IntentService to listen for transition.
     */
    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    /**
     * Play music and show marker when in specific geofence.
     *
     * @param intent    the current intent from PendingIntent;
     *                  this Intent is received by current service,
     *                  which contains geofencing information.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(Constants.LOG_TAG, geofencingEvent.getErrorCode() + "");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered.
            // A single event can trigger multiple geofences.
            for (Geofence geofence : geofencingEvent.getTriggeringGeofences())
                Log.v(Constants.LOG_TAG, geofence.getRequestId());
            Geofence triggered = geofencingEvent.getTriggeringGeofences().get(0);

            // Log the transition details.
            Log.v(Constants.LOG_TAG, "Detect entering: " + triggered.getRequestId());
            Log.v(Constants.LOG_TAG, "Detect location: " +
                            geofencingEvent.getTriggeringLocation().getLatitude()
                            + ", " + geofencingEvent.getTriggeringLocation().getLongitude()
            );
            // Play specific music according to location
            GeofenceMedia.getInstance().playSong(this, triggered.getRequestId());
            // Show marker on the location?
            GeofenceMedia.getInstance().showMarker(
                    geofencingEvent.getTriggeringLocation(),
                    triggered.getRequestId()
            );
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            GeofenceMedia.getInstance().pauseSong();
            GeofenceMedia.getInstance().removeMarkers();
        } else {
            // Log the error.
            Log.e(Constants.LOG_TAG, "No Enter or Exit detected");
        }
    }
}
