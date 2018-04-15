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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        final RadioGroup radioGroup = findViewById(R.id.activitiesID);

        mAPIService = APIUtils.getAPIService();
        mAPIService.getProfile(Utils.getMyId(getBaseContext())).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(final Call<Person> call, Response<Person> response) {
                person = response.body();

             /*   ArrayList<Person.Activities> likedActivities = new ArrayList<>();
                likedActivities.add(Person.Activities.Running);
                likedActivities.add(Person.Activities.Skateboarding);
                likedActivities.add(Person.Activities.NordicWalking);
                person = new Person("test", 12, "test description", 1.05,10.5, Person.Activities.Running, likedActivities, null, "1122");
*/
                if(person != null){
                    if (person.getLikedActivities() != null) {
                        for (int i = 0; i < person.getLikedActivities().size(); i++) {
                            String text = new String(person.getLikedActivities().get(i).toString());
                            RadioButton radioButton = new RadioButton(getBaseContext());
                            radioButton.setText(text);
                            radioButton.setId(i);
                            radioGroup.setOrientation(RadioGroup.VERTICAL);
                            if(person.getCurrentActivities() == person.getLikedActivities().get(i)){
                                radioButton.setChecked(true);
                               // radioButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.activeActivityColor));
                            }
                            else{
                                radioButton.setChecked(false);
                               // radioButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                            }
                            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                                    RadioGroup.LayoutParams.WRAP_CONTENT,
                                    RadioGroup.LayoutParams.WRAP_CONTENT));
                            radioButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                            radioButton.setPadding(8, 8, 8, 8);
                            radioGroup.addView(radioButton);
                        }
                    }
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                            person.setCurrentActivities(person.getLikedActivities().get(i));
                        }
                    });
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