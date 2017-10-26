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
 *
 * See https://developer.android.com/guide/topics/location/strategies.html for a guide
 */
public final class LocationService {

    private static LocationService instance;
    public static LocationService instance() {
        return instance;
    }

    private Context context;

    private Location bestKnownLocation = null;

    private LocationService() {
    }

    public static void init(Context context) {
        if (instance != null) {
            throw new UnsupportedOperationException("instance already initialized");
        }
        instance = new LocationService();

        instance.context = context;

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return;
            }

            LocationListener gpsBaseLocationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*10, 100, gpsBaseLocationListener, Looper.getMainLooper());

            LocationListener networkBaseLocationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60*10, 100, networkBaseLocationListener, Looper.getMainLooper());

        } catch (SecurityException e) {
            Log.e(LocationService.class.getName(), "Error while initializing LocationService", e);
        }
    }

    int activeRequestCount = 0;
    LocationListener gpsActiveLocationListener = new MyLocationListener();
    LocationListener networkActiveLocationListener = new MyLocationListener();

    public void requestActiveLocationUpdates() {

        if (!isRequestingActiveLocationUpdates()) {

            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60, 0, networkActiveLocationListener, Looper.getMainLooper());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 0, gpsActiveLocationListener, Looper.getMainLooper());
        }
        activeRequestCount++;
    }

    public void removeActiveLocationUpdates() {

        if (activeRequestCount <= 0) {
            throw new UnsupportedOperationException("removeActiveLocationUpdates was called although no active request exists");
        }

        activeRequestCount--;

        if (!isRequestingActiveLocationUpdates()) {

            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            locationManager.removeUpdates(networkActiveLocationListener);
            locationManager.removeUpdates(gpsActiveLocationListener);
        }
    }

    public boolean isRequestingActiveLocationUpdates() {
        return activeRequestCount > 0;
    }

    public void requestFreshLocation() {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new MyLocationListener(), Looper.getMainLooper());
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListener(), Looper.getMainLooper());
    }

    public static final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, instance.bestKnownLocation)) {
                instance.bestKnownLocation = location;
            }
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
    }

    /**
     * Checks whether the SORMAS app has the permission to access the phone's location
     */
    public boolean hasGPSAccess() {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the phone's GPS is turned on
     * @return
     */
    public boolean hasGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public Location getLocation() {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            throw new UnsupportedOperationException();
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isBetterLocation(location, bestKnownLocation)) {
            bestKnownLocation = location;
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (isBetterLocation(location, bestKnownLocation)) {
                bestKnownLocation = location;
            }
        }

        return bestKnownLocation;
    }

    private static final int SIGNIFICANTLY_NEWER_DELTA = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > SIGNIFICANTLY_NEWER_DELTA;
        boolean isSignificantlyOlder = timeDelta < -SIGNIFICANTLY_NEWER_DELTA;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
