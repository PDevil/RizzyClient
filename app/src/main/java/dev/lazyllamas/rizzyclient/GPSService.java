package dev.lazyllamas.rizzyclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import dev.lazyllamas.rizzyclient.Business.APIService;
import dev.lazyllamas.rizzyclient.Business.APIUtils;
import dev.lazyllamas.rizzyclient.Business.ErrorState;
import dev.lazyllamas.rizzyclient.Business.NewUser;
import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.Business.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPSService extends Service implements LocationListener {
    public GPSService() {
    }


    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    public static double latitude, longitude;
    LocationManager locationManager;
    public Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    public static String str_receiver = "gps.service.receiver";
    Intent intent;

    String TAG = "RizzyClient";

    APIService mAPIService;


    Notification gpsNotification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
            intent = new Intent(str_receiver);

        mAPIService = APIUtils.getAPIService();

//        fn_getlocation();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent_closeApp = new Intent(this, CloseApp.class);

        PendingIntent resultPendingIntent =
                        PendingIntent.getBroadcast(
                                        this.getApplicationContext(),
                                        0,
                                        intent_closeApp,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                        );

        Intent intent = new Intent(getApplicationContext(), MainTabbedActivity.class);
        PendingIntent resultPendingIntentStartActivity =
                PendingIntent.getActivity(
                        this.getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );



        Notification.Builder n  = new Notification.Builder(getApplicationContext())
                .setContentTitle("Looking for friends")
                .setSmallIcon(R.drawable.ic_r)
                .setContentIntent(resultPendingIntentStartActivity)
                .addAction(R.drawable.ic_r,"Stop location", resultPendingIntent)
                .setOngoing(true);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = getString(R.string.notifyChannel);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, getString(R.string.notifyChannel),importance);
            notificationManager.createNotificationChannel(mChannel);

            n.setChannelId(id);
        }

        notificationManager.notify(1, n.build());




    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        mTimer.cancel();
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.e(TAG, "Location not available");


        } else {




            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.d(TAG, "Location from GPS");
                        Log.d("latitude", location.getLatitude() + "");
                        Log.d("longitude", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location, getBaseContext());

                    }
                }
            }
            else if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.d(TAG, "Location from Network");
                        Log.d("latitude", location.getLatitude() + "");
                        Log.d("longitude", location.getLongitude() + "");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location, getBaseContext());

                    }
                }

            }


        }

    }


    public void sendPOST(double lat, double lon, Context context) {
        Person person = new Person(lat, lon);


        mAPIService.updateLocation(Utils.getMyId(context), person).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("", "");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("", "");
            }
        });


    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }


    private void fn_update(Location location, Context context) {

        sendPOST(latitude, longitude, context);

        intent.putExtra("latutide", location.getLatitude() + "");
        intent.putExtra("longitude", location.getLongitude() + "");
        sendBroadcast(intent);

        Intent in = new Intent();
        in.putExtra("latutide", location.getLatitude() + "");
        in.putExtra("longitude", location.getLongitude() + "");
        in.setAction(getString(R.string.gpsPosIntent));


        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

        MapsActivity.lat = location.getLatitude();
        MapsActivity.lon = location.getLongitude();



    }

}
