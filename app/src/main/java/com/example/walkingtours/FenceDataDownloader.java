package com.example.walkingtours;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class FenceDataDownloader implements Runnable {

    private static final String TAG = "FenceDataDownloader";
    private final Geocoder geocoder;
    private final FenceMgr fenceMgr;
    private final MapsActivity mapsActivity;
    private static final String FENCE_URL = "http://www.christopherhield.com/data/WalkingTourContent.json";

    FenceDataDownloader(MapsActivity mapsActivity, FenceMgr fenceMgr) {
        this.mapsActivity = mapsActivity;
        this.fenceMgr = fenceMgr;
        geocoder = new Geocoder(mapsActivity);
    }

    private void processData(String result) {

        if (result == null)
            return;

        ArrayList<FenceData> fences = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray jArr = jObj.getJSONArray("fences");

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject fObj = jArr.getJSONObject(i);
                String id = fObj.getString("id");
                String address = fObj.getString("address");
                float rad = (float) fObj.getDouble("radius");
                String color = fObj.getString("fenceColor");
                String description = fObj.getString("description");
                String image = fObj.getString("image");

                double latitude = fObj.getDouble("latitude");
                double longitude = fObj.getDouble("longitude");
                FenceData fd = new FenceData(id, latitude, longitude, address, rad, color, description, image);
                fences.add(fd);
            }
            JSONArray jPath = jObj.getJSONArray("path");
            String[] arrPath = new String[jPath.length()];
            for(int i = 0; i < jPath.length(); i++)
                arrPath[i] = jPath.getString(i);

            fenceMgr.addFences(fences, arrPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(FENCE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: Response code: " + connection.getResponseCode());
                return;
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            processData(buffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LatLng getLatLong(String address) {
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            Address a = addressList.get(0);
            return new LatLng(a.getLatitude(), a.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
