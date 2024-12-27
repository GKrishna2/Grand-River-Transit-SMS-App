package com.example.grt;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class LocationBackgroundService extends Service {
    private static final String TAG = "LocationBackgroundService";
    private static final int PERMISSIONS_LOCATION_REQUEST = 9;

    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList = new ArrayList<Geofence>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceThread thread = new ServiceThread();
        Log.d(TAG, "Entered LocationBackgroundService: onStartCommand");
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ServiceThread extends Thread {
        @Override
        public void run()
        {
            Log.d(TAG, "Running ServiceThread");
            geofencingClient = LocationServices.getGeofencingClient(LocationBackgroundService.this);
            //Toast.makeText(this, "Geofence OnCreate entered", Toast.LENGTH_LONG).show();

            int latitude = 50;
            int longitude = -50;
            LatLng latLng = new LatLng(latitude,longitude);

            addGeofence("9995",latLng,500);
        }
    }

    public void createGeofence(String id, LatLng latLng, float radius) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    private GeofencingRequest geofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        //if (geofencePendingIntent() != null) return geofencePendingIntent
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        //sendBroadcast(intent);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    private void addGeofence(String id, LatLng latLng, float radius) {
        createGeofence(id, latLng, radius);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Permission", "Permission Not Granted.");
        }
        geofencingClient.addGeofences(geofencingRequest(), getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Geofence", "OnSuccess: Geofence");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Geofence", "OnFailure: Geofence - " + e.getMessage());
            }
        });
    }
}
