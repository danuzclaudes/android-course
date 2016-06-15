package edu.unc.chongrui.assignment3;

import com.google.android.gms.location.Geofence;

public class Constants {
    public static final String LOG_TAG = "LOC";

    public static final long LOCATION_UPDATE_INT = 2000,
            FASTEST_LOCATION_UPDATE_INT = 1000,
            GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;

    public static final float GEOFENCE_RADIUS_IN_METERS = 20;

    public static final String F_BROOK_REQ_ID = "FB";
    public static final double F_BROOK_LAT = 35.910085,
            F_BROOK_LONG = -79.053193;

    public static final String OLD_WELL_REQ_ID = "Old Well";
    public static final double OLD_WELL_LAT = 35.912304,
            OLD_WELL_LONG = -79.051143;

    public static final String POLK_PLACE_REQ_ID = "Polk Place";
    public static final double POLK_PLACE_LAT = 35.910810,
            POLK_PLACE_LONG = -79.05620;

    public static final String HOME_REQ_ID = "HOME";
    public static final double HOME_LAT = 35.944595,
            HOME_LONG = -79.0561413;

    public static final String UL_REQ_ID = "UL";
    public static final double UL_LAT = 35.9096663,
            UL_LONG = -79.0490126;
}
