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
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;

import dev.lazyllamas.rizzyclient.Business.APIService;
import dev.lazyllamas.rizzyclient.Business.APIUtils;
import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.Business.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    public static Double lat = 0d, lon = 0d;
    public static Double prev_lat = 0d, prev_lon = 0d;
    int zoom = 13;
    private GoogleMap mMap;
    final long refresh_interval = 10000;
    ArrayList<Person> nearbys;
    private Timer mTimer = null;
    private Handler mHandler = new Handler();
    private APIService mAPIService;


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


            Bitmap tmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.sport), 100, 100, false);

            //TODO  Bitmap resizedBitmap = Bitmap.createScaledBitmap(person.getImage(), 100, 100, false);


            marker.icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(tmp)));
            marker.position(pos);



            Marker marker_Ready = mMap.addMarker(marker);
            marker_Ready.setTag(person);

            marker_Ready.showInfoWindow();


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

        mAPIService = APIUtils.getAPIService();


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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.gpsPosIntent)));

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

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, refresh_interval);


    }

    @Override
    public void onResume() {
        super.onResume();


    }


    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAPIService.getNearby(Utils.getMyId(getContext())).enqueue(new Callback<ArrayList<Person>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Person>> call, Response<ArrayList<Person>> response) {
                            nearbys = response.body();
                            mMap.clear();

                            if(nearbys!=null) {
                                for (Person person : nearbys) {
                                    person.setImage(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                            R.drawable.sport), 100, 100, false)); //TODO
                                    person.setCurrentActivities(Person.Activities.Running);

                                    ArrayList<Person.Activities> tmp = new ArrayList<>();
                                    tmp.add(Person.Activities.Running);

                                    person.setLikedActivities(tmp);
                                    addPersonMarker(person);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Person>> call, Throwable t) {
                            Log.e("Rizzy", "Failed downloading nearby");
                        }
                    });
                }
            });

        }
    }

    @Override
    public void onPause() {
        try {
            if (broadcastReceiver != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
                // broadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }


}
