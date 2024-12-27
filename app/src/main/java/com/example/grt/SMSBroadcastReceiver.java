package com.example.grt;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String smsNumber = String.format("smsto:%s", "57555");
        String smsMessage = intent.getStringExtra("Contact");

        String scAddress = null;
        PendingIntent sentIntent = null;
        PendingIntent deliveryIntent = null;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber,scAddress,smsMessage,sentIntent,deliveryIntent);
        Toast.makeText(context, "BroadcastReceiver SMS sent", Toast.LENGTH_SHORT).show();
        Log.d("SMSBroadcastReceiver: SMS sent", "SMS sent");
    }
}