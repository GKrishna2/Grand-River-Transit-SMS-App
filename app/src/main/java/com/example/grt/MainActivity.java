package com.example.grt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    public static final String TAG = MainActivity.class.getSimpleName();
    Button geofenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckSmsPermission();

        Intent serviceIntent = new Intent(this, LocationBackgroundService.class);
        startService(serviceIntent);

        //Intent smsIntent = new Intent(getApplicationContext(),SMSBroadcastReceiver.class);
        //smsIntent.putExtra("Contact","3623");
        //getApplicationContext().sendBroadcast(smsIntent);

        //Intent smsIntent = new Intent(this,SendSMSBackgroundService.class);
        //smsIntent.putExtra("Contact","3623");
        //startService(smsIntent);

        geofenceButton = (Button) findViewById(R.id.geofence_button);
        geofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,GeoFenceActivity.class);
                startActivity(intent);
            }
        });

        //IntentFilter geoIntentFilter = new IntentFilter("com.grt.geofence.entry");
        //registerReceiver(geoStatReceiver,geoIntentFilter);
    }

    public void smsSendMessage (View view)
    {
        EditText smsText = (EditText) findViewById(R.id.sms_message);
        String smsNumber = String.format("smsto:%s", "57555");
        String smsMessage = smsText.getText().toString();

        String scAddress = null;
        PendingIntent sentIntent = null;
        PendingIntent deliveryIntent = null;

        CheckSmsPermission();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber,scAddress,smsMessage,sentIntent,deliveryIntent);
        Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        Log.d("SMS sent", "SMS sent");
    }

    private void disableSmsButton ()
    {
        Toast.makeText(this, "SMS permissions disabled.",Toast.LENGTH_LONG).show();
        ImageButton smsButton = (ImageButton) findViewById(R.id.message_icon);
        smsButton.setVisibility(View.INVISIBLE);
    }

    private void enableSmsButton ()
    {
        Toast.makeText(this, "SMS permissions enabled", Toast.LENGTH_SHORT).show();
        ImageButton smsButton = (ImageButton) findViewById(R.id.message_icon);
        smsButton.setVisibility(View.VISIBLE);
    }

    private void CheckSmsPermission ()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, getString(R.string.permission_not_granted));
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else
        {
            enableSmsButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode)
        {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
            {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    enableSmsButton();
                }
                else
                {
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(this,getString(R.string.failure_permission),Toast.LENGTH_LONG).show();
                    disableSmsButton();
                }

                if (permissions[1].equalsIgnoreCase(Manifest.permission.RECEIVE_SMS) && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    enableSmsButton();
                }
                else
                {
                    Log.d(TAG, getString(R.string.failure_permissionReceive));
                    Toast.makeText(this,getString(R.string.failure_permissionReceive),Toast.LENGTH_LONG).show();
                    disableSmsButton();
                }
            }
        }
    }

    private BroadcastReceiver geoTranstionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String geoTransitionText = intent.getStringExtra("geoTransition");
            Log.d("geoTransitionBroadcastReceiver",geoTransitionText);
            //Intent smsIntent = new Intent(getApplicationContext(),SMSBroadcastReceiver.class);
            //smsIntent.putExtra("Contact","3623");
            //getApplicationContext().sendBroadcast(smsIntent);
            //Intent smsIntent = new Intent(context,SendSMSBackgroundService.class);
            //smsIntent.putExtra("Contact","3623");
            //startService(smsIntent);
            Intent smsIntent = new Intent(context,SendSMSBackgroundService.class);
            smsIntent.putExtra("Contact","3623");
            //startService(smsIntent);
            if(geoTransitionText.equals("geoIntent Enter"))
            {
                Log.d("geoTransitionBroadcastReceiver","IF: ENTER TRUE");
                startService(smsIntent);
            }
            else if (geoTransitionText.equals("geoIntent Exit"))
            {
                Log.d("geoTransitionBroadcastReceiver","IF: EXIT FALSE");
                Intent stopSMSThread = new Intent("com.grt.sms.stopThread");
                stopSMSThread.putExtra("threadState","Stop");
                context.sendBroadcast(stopSMSThread);
                stopService(smsIntent);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(geoTranstionBroadcastReceiver,new IntentFilter("com.grt.geofence.entryTransition"));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(geoTranstionBroadcastReceiver);
        super.onStop();
    }

    /*BroadcastReceiver geoStatReceiver = new BroadcastReceiver() {
        String value;
        @Override
        public void onReceive(Context context, Intent intent) {
            geostat = intent.getExtras().getString("geoStatus");
            Toast.makeText(context,geostat,Toast.LENGTH_LONG);
        }
    };*/
}