package dev.lazyllamas.rizzyclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.lazyllamas.rizzyclient.Business.APIService;
import dev.lazyllamas.rizzyclient.Business.APIUtils;
import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.Business.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitiesActivity extends AppCompatActivity {

    private Person person;

    private APIService mAPIService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        final LinearLayout linearLayout = findViewById(R.id.activitiesID);

        mAPIService = APIUtils.getAPIService();
        mAPIService.getProfile(Utils.getMyId(getBaseContext())).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                person = response.body();
                if(person != null){
                    if (person.getLikedActivities() != null) {
                        for (int i = 0; i < person.getLikedActivities().size(); i++) {
                            boolean yes = false;
                            String text;
                            if (person.getLikedActivities().get(i) == person.getCurrentActivities()) {
                                text = new String("¤ " + person.getCurrentActivities().toString());
                                yes = true;
                            } else {
                                text = new String(person.getLikedActivities().get(i).toString());
                                yes = false;
                            }
                            TextView textView1 = new TextView(getApplicationContext());
                            if (yes == true) {
                                textView1.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.activeActivityColor));
                            } else {
                                textView1.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                            }
                            textView1.setText(text);
                            textView1.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.FILL_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView1.setGravity(Gravity.CENTER_VERTICAL);
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                            textView1.setPadding(8, 8, 8, 8);
                            linearLayout.addView(textView1);
                        }
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Log.e("Rizzy", "Failed downloading current profile");
            }


        });
    }
}