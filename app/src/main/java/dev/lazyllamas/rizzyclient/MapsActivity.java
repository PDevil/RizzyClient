package dev.lazyllamas.rizzyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dev.lazyllamas.rizzyclient.Business.Person;

public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static Double lat = 0d, lon = 0d;
    public static Double prev_lat = 0d, prev_lon = 0d;
    public static String mainIntentName = "gpsWorking";
    int zoom = 13;
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver_GPS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean gpsWorking = intent.getBooleanExtra(mainIntentName, false);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
            ImageView gpsIcon = getView().findViewById(R.id.imageView3);

            TextView gpsText = getView().findViewById(R.id.textView);


            if (gpsIcon != null && gpsText != null) {
                if (gpsWorking) {
                    gpsIcon.startAnimation(myFadeInAnimation);
                    gpsText.setText("GPS is working");
                } else {
                    gpsIcon.clearAnimation();
                    gpsText.setText("GPS is not working");
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            try {

                if (prev_lat == 0 && prev_lon == 0)
                    updateCamera(lat, lon);

                prev_lat = lat;
                prev_lon = lon;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public static MapsActivity newInstance(int sectionNumber) {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        args.putInt(MainTabbedActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    void updateCameraMarker(Double lat, Double lon) {

        if (mMap != null) {
            LatLng pos = new LatLng(lat, lon);
            //  mMap.clear();
            mMap.addMarker(new MarkerOptions().position(pos));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
    }

    void addPersonMarker(Person person) {

        if (mMap != null) {
            LatLng pos = new LatLng(person.getLat(), person.getLon());
            MarkerOptions marker = new MarkerOptions();
            marker.title(person.getName());


            Bitmap resizedBitmap = Bitmap.createScaledBitmap(person.getImage(), 100, 100, false);


            marker.icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(resizedBitmap)));
            marker.position(pos);


            Marker marker_Ready = mMap.addMarker(marker);
            marker_Ready.setTag(person);

            mMap.setOnMarkerClickListener(this);

            // mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    void updateCamera(Double lat, Double lon) {

        if (mMap != null) {
            LatLng pos = new LatLng(lat, lon);
            //  mMap.clear();
            // mMap.addMarker(new MarkerOptions().position(pos).title("Marker in Gdańsk"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        return v;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, Bitmap img) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.pin);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = new BitmapDrawable(img);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.gpsPosIntent)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver_GPS, new IntentFilter(getString(R.string.gpsIntent)));


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(13);

        updateCamera(lat, lon);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Bundle b = new Bundle();
                b.putParcelable("dev.lazyllamas.rizzyclient", (Person) marker.getTag());

                Intent i = new Intent(getContext(), PersonActivity.class);
                i.putExtras(b);

                startActivity(i);

            }
        });

        //TODO

        Bitmap icon = BitmapFactory.decodeResource(getView().getResources(),
                R.drawable.sport);

        addPersonMarker(new Person("Gerard", 19, "", 54.365, 18.60773902, new ArrayList<Person.Activities>(), new ArrayList<Person.Activities>(), icon, "b6524ae7-d9bd-49c7-a885-883aa1a64938"));
        addPersonMarker(new Person("xDD", 19, "", 54.369, 18.60573902, new ArrayList<Person.Activities>(), new ArrayList<Person.Activities>(), icon, "77fc6c96-7ebf-4759-87f7-2228fec6c323"));
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        try {
            if (broadcastReceiver != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
                // broadcastReceiver = null;
            }
            if (broadcastReceiver_GPS != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver_GPS);
                //  broadcastReceiver_GPS = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
