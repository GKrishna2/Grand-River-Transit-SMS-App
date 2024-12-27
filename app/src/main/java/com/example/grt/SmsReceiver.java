package com.example.grt;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Detect message", "Detected message");
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String message = "";
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);

        Toast.makeText(context, "OnReceive entered.", Toast.LENGTH_LONG).show();

        if (pdus != null)
        {
            // Check Android version
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];
            Toast.makeText(context, "Loading message", Toast.LENGTH_LONG).show();

            for (int i=0; i<msgs.length; i++) {
                // Newer Android versions (>M)
                if (isVersionM) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // Older Android versions (<L)
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                message += msgs[i].getOriginatingAddress() + ": " + msgs[i].getMessageBody() + "\n";
                Log.d(TAG, "onReceive: " + message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(context, "Could not load message", Toast.LENGTH_LONG).show();
        }
    }
}