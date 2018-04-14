package dev.lazyllamas.rizzyclient;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView;
    Animation an, an2;

    public static final int REQUEST_LOCATION = 1;

    boolean gpsGranted;

    boolean permissionRequested = false;


    public boolean GPSPermissionGrant() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                gpsGranted = true;
                permissionRequested = true;
                loadApp();
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


                return true;
            }
        } else {
            //  Log.v(TAG,"Permission is granted");

            gpsGranted = true;
            permissionRequested = true;
            loadApp();
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gpsGranted = true;
                    permissionRequested = true;
                    loadApp();

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SplashScreen.this).create();
                    alertDialog.setTitle(getString(R.string.noGPSTitle));
                    alertDialog.setMessage(getString(R.string.permissionGPSubititle));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    alertDialog.show();
                    gpsGranted = false;
                    permissionRequested = true;
                }
        }
    }

    private void loadApp() {


        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
            dialog.setMessage(R.string.gpsNotEnabled);
            dialog.setPositiveButton(R.string.openLocation, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getBaseContext().startActivity(myIntent);
                    finish();
                    //get gps
                }
            });
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    finish();
                }
            });
            dialog.show();
        }

        if ((gps_enabled || network_enabled) && !isSignedIn()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if ((gps_enabled || network_enabled) && gpsGranted) {
            Intent intent = new Intent(SplashScreen.this, MainTabbedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isSignedIn() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.tokenPrefKey), MODE_PRIVATE);
        return pref.getBoolean(getString(R.string.token), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        if (isMyServiceRunning(GPSService.class)) {
            Intent intent = new Intent(SplashScreen.this, MainTabbedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


        imageView = findViewById(R.id.imageView);

        an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        imageView.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                GPSPermissionGrant();


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(an2);


                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}