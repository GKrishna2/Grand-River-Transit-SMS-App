package com.example.grt;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SendSMSBackgroundService extends Service {
    private String smsNumber;
    private String smsMessage;

    private String scAddress = null;
    private PendingIntent sentIntent = null;
    private PendingIntent deliveryIntent = null;

    private String threadFlag = "Run";

    private int count = 0;

    public SendSMSBackgroundService() {
    }

    @Override
    public void onCreate() {
        smsNumber = String.format("smsto:%s", "57555");
        smsMessage = "3623";
        count = 0;
        ServiceThread thread = new ServiceThread();
        registerReceiver(threadStopBroadcastReceiver,new IntentFilter("com.grt.sms.stopThread"));
        thread.start();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //smsMessage = intent.getStringExtra("Contact");
        smsMessage = "3623";
        Log.d("SendSMSBackgroundService", "SMS onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(threadStopBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class ServiceThread extends Thread
    {
        @Override
        public void run() {
            while(threadFlag.equals("Run") && count<6) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(smsNumber, scAddress, smsMessage, sentIntent, deliveryIntent);
                Log.d("SendSMSBackgroundService", "SMS sent");
                count = count + 1;
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //super.run();
            }
            currentThread().interrupt();
        }
    }

    private BroadcastReceiver threadStopBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentText = intent.getStringExtra("threadState");
            threadFlag = intentText;
            ServiceThread.currentThread().interrupt();
        }
    };


}