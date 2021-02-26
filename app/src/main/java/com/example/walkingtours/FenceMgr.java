package com.example.walkingtours;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FenceMgr {

    private static final String TAG = "FenceMgr";
    private final MapsActivity mapsActivity;
    private final GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private final ArrayList<Circle> circles = new ArrayList<>();
    private final List<PatternItem> pattern = Collections.singletonList(new Dot());
    private static final ArrayList<FenceData> fenceList = new ArrayList<>();
    private static final ArrayList<LatLng> latLonPath = new ArrayList<>();
    private GoogleMap mMap;
    private PolylineOptions polylineOptions = new PolylineOptions();
    private Polyline llPathPolyline;

    FenceMgr(final MapsActivity mapsActivity, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.mMap   = mMap;
        geofencingClient = LocationServices.getGeofencingClient(mapsActivity);

        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(mapsActivity, aVoid -> Log.d(TAG, "onSuccess: removeGeofences"))
                .addOnFailureListener(mapsActivity, e -> {
                    Log.d(TAG, "onFailure: removeGeofences");
                    Toast.makeText(mapsActivity, "Trouble removing existing fences: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        new Thread(new FenceDataDownloader(mapsActivity, this)).start();
    }

    static FenceData getFenceData(String id) {
        for (FenceData fd : fenceList) {
            if (fd.getId().equals(id))
                return fd;
        }
        return null;
    }

    void drawFences() {
        for (FenceData fd : fenceList) {
            drawFence(fd);
        }
    }

    void eraseFences() {
        for (Circle c : circles)
            c.remove();
        circles.clear();
    }

    private void drawFence(FenceData fd) {

        int line = Color.parseColor(fd.getFenceColor());
        int fill = ColorUtils.setAlphaComponent(line, 85);

        LatLng latLng = new LatLng(fd.getLat(), fd.getLon());
        Circle c = mapsActivity.getMap().addCircle(new CircleOptions()
                .center(latLng)
                .radius(fd.getRadius())
                .strokePattern(pattern)
                .strokeColor(line)
                .fillColor(fill));

        circles.add(c);
    }

    void addFences(ArrayList<FenceData> fences, String[] arrPath) {
        savePath(arrPath);
        fenceList.clear();
        fenceList.addAll(fences);

        for (FenceData fd : fenceList) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(fd.getId())
                    .setCircularRegion(
                            fd.getLat(),
                            fd.getLon(),
                            fd.getRadius())
                    .setTransitionTypes(1)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE) //Fence expires after N millis  -or- Geofence.NEVER_EXPIRE
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .addGeofence(geofence)
                    .build();

            geofencePendingIntent = getGeofencePendingIntent();

            if (ActivityCompat.checkSelfPermission(mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            geofencingClient
                    .addGeofences(geofencingRequest, geofencePendingIntent)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: addGeofences"))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Log.d(TAG, "onFailure: addGeofences");

                        Toast.makeText(mapsActivity, "Trouble adding new fence: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    });
        }
        mapsActivity.runOnUiThread(this::drawFences);
    }

    public void savePath(String[] arrPath) {
        latLonPath.clear();

        for (int i = 0; i < arrPath.length; i++) {
            String latLngStr = arrPath[i];
            String[] parts = latLngStr.split(",");
            String lng = parts[0];
            String lat = parts[1];
            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            latLonPath.add(latLng);
        }

        for (LatLng ll : latLonPath) {
            polylineOptions.add(ll);
        }

        mapsActivity.runOnUiThread(this::drawPath);
    }

    void drawPath() {
        llPathPolyline = mMap.addPolyline(polylineOptions);
        llPathPolyline.setEndCap(new RoundCap());
        llPathPolyline.setWidth(8);
        llPathPolyline.setColor(Color.YELLOW);
    }

    void showPath() {
        llPathPolyline.setColor(Color.YELLOW);
    }

    void erasePath() {
        llPathPolyline.setColor(Color.TRANSPARENT);
    }

    private PendingIntent getGeofencePendingIntent() {

        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(mapsActivity, GeofenceBroadcastReceiver.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(mapsActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


}

