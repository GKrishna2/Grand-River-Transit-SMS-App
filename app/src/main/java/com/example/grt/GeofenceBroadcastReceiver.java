package com.example.grt;

import static java.lang.Boolean.TRUE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Toast.makeText(context, "Entered Broadcast Receiver", Toast.LENGTH_SHORT).show();
        Log.d("Geofence-BroadcastReceiver", "Entered Broadcast Receiver");

        if(geofencingEvent != null) {
            Log.d("Geofence-BroadcastReceiver", "Entered if statement");
            if (geofencingEvent.hasError() == TRUE) {
                String error = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
                Log.e("Geofence-BroadcastReceiver", error);
                Toast.makeText(context, "BroadcastReceiver-hasError", Toast.LENGTH_SHORT).show();
            }

            int geofenceTransition = geofencingEvent.getGeofenceTransition();

        /*if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(this,geofenceTransition,triggeringGeofences);
        }*/
            Intent geoIntent = new Intent("com.grt.geofence.entryTransition");

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d("Geofence-BroadcastReceiver", "Transition: Enter");
                Toast.makeText(context, "Transition: Enter", Toast.LENGTH_SHORT).show();
                geoIntent.putExtra("geoTransition","geoIntent Enter");
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d("Geofence-BroadcastReceiver", "Transition: Exit");
                Toast.makeText(context, "Transition: Exit", Toast.LENGTH_SHORT).show();
                geoIntent.putExtra("geoTransition","geoIntent Exit");
            }

            //context.sendBroadcast(geoIntent);
            context.sendBroadcast(geoIntent);
        }
        else if (geofencingEvent == null)
        {
            Log.d("Geofence-BroadcastReceiver", "geofencingEvent is null");
        }
    }
}