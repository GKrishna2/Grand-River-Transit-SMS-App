package com.example.grt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceActivity extends AppCompatActivity {

    private GeofencingClient geofencingClient;
    private String GeofenceRequestId = "1005";
    private static final int PERMISSIONS_LOCATION_REQUEST = 9;

    public Geofence geofence;
    List<Geofence> geofenceList = new ArrayList<Geofence>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        geofencingClient = LocationServices.getGeofencingClient(this);
        Toast.makeText(this, "Geofence OnCreate entered", Toast.LENGTH_LONG).show();

        int latitude = 50;
        int longitude = -50;
        LatLng latLng = new LatLng(latitude,longitude);

        addGeofence("9995",latLng,100);
    }

    // Adding Geofence to geofenceList[] instead of returning single Geofence.
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
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},PERMISSIONS_LOCATION_REQUEST);
        }
        geofencingClient.addGeofences(geofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Geofence", "OnSuccess: Geofence");
                        Toast.makeText(GeoFenceActivity.this, "OnSuccess: Added geofence", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Geofence", "OnFailure: Geofence - " + e.getMessage());
                        Toast.makeText(GeoFenceActivity.this, "OnFailure: Couldn't add geofence", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case PERMISSIONS_LOCATION_REQUEST:
            {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Access Fine Location Granted", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.d("Permissions", "Access Fine Location Permission Failed.");
                    Toast.makeText(this,"Access Fine Location Permission Failed.",Toast.LENGTH_LONG).show();
                }

                if (permissions[1].equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Access Coarse Location Granted", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.d("Permissions", "Access Coarse Location Permission Failed.");
                    Toast.makeText(this,"Access Coarse Location Permission Failed.",Toast.LENGTH_LONG).show();
                }

                if (permissions[2].equalsIgnoreCase(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Access Background Location Granted", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.d("Permissions", "Access Background Location Permission Failed.");
                    Toast.makeText(this,"Access Background Location Permission Failed.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}