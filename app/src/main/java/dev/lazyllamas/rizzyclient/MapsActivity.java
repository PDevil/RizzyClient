package dev.lazyllamas.rizzyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.lazyllamas.rizzyclient.Business.Person;

public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static Double lat = 0d, lon = 0d;
    public static Double prev_lat = 0d, prev_lon = 0d;
    public static String mainIntentName = "gpsWorking";
    int zoom = 15;
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

            Bitmap imageBitmap = BitmapFactory.decodeResource(getView().getResources(),
                    R.drawable.sport);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);

            marker.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
            marker.position(pos);

            mMap.addMarker(marker);

            //   mMap.setOnMapClickListener((GoogleMap.OnMapClickListener)this);
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.gpsPosIntent)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver_GPS, new IntentFilter(getString(R.string.gpsIntent)));


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(13);

        updateCamera(lat, lon);

        addPersonMarker(new Person("Gerard", 19, "", 54.365, 18.60773902));
        addPersonMarker(new Person("xDD", 19, "", 54.369, 18.60573902));
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
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(getActivity(),
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
