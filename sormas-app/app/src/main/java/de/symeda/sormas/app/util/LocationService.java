package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Mate Strysewske on 16.08.2017.
 */
public class LocationService {

    // Minimum distance between two locations to trigger a new location update
    private static final long MIN_DISTANCE_CHANGE = 100; // 100 metres
    // Minimum time between updates
    private static final long MIN_TIME_CHANGE = 1000 * 60 * 5; // 5 minutes

    private static LocationService instance = null;

    private LocationManager locationManager;
    private Location location;

    public static LocationService getLocationService(Context context) {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * Checks whether the SORMAS app has the permission to access the phone's location and GPS is turned on.
     * @param context
     * @return
     */
    public boolean hasGPSEnabled(Context context) {
        if (instance == null) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGPSEnabled || isNetworkEnabled;
    }

    private LocationService(Context context) {
        initLocationService(context);
    }

    private void initLocationService(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                return;
            }

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LocationService.this.location = location;
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            };

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_CHANGE, MIN_DISTANCE_CHANGE, locationListener, Looper.getMainLooper());
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_CHANGE, MIN_DISTANCE_CHANGE, locationListener, Looper.getMainLooper());
            }
        } catch (SecurityException e) {
            Log.e(getClass().getName(), "Error while initializing LocationService", e);
        }
    }

    public Location getLocation() {
        return location;
    }

}
