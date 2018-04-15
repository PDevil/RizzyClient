package dev.lazyllamas.rizzyclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.lazyllamas.rizzyclient.Business.APIService;
import dev.lazyllamas.rizzyclient.Business.APIUtils;
import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.Business.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends AppCompatActivity {

    EditText name;
    EditText description;
    CheckBox running;
    CheckBox cycling;
    CheckBox skateboarding;
    CheckBox nordicwalking;
    private APIService mAPIService;
    private Person person;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public int year;
    public int month;
    public int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = findViewById(R.id.nameText);
        description = findViewById(R.id.descriptionText);

        running = findViewById(R.id.runningCheckBox);
        skateboarding = findViewById(R.id.skateboardingCheckBox);
        cycling = findViewById(R.id.cyclingCheckBox);
        nordicwalking = findViewById(R.id.nordicwalkingCheckBox);

        final Button mDisplayDate = findViewById(R.id.Birthday);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        MyProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
            }
        };
        Button button = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dateval = formatter.parse(year+"-"+month+"-"+day);
                    person.setAge(dateval);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                person.setName(name.getText().toString());
                person.setDescription(description.getText().toString());


                ArrayList<Person.Activities> likednew = new ArrayList<>();

                if (running.isChecked())
                    likednew.add(Person.Activities.Running);

                if (skateboarding.isChecked())
                    likednew.add(Person.Activities.Skateboarding);

                if (cycling.isChecked())
                    likednew.add(Person.Activities.Cycling);


                if (nordicwalking.isChecked())
                    likednew.add(Person.Activities.NordicWalking);


                person.setLikedActivities(likednew);
                mAPIService = APIUtils.getAPIService();
                mAPIService.pushProfile(Utils.getMyId(getBaseContext()), person).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()) {
                            AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
                            alertDialog.setTitle("Saved successfully");
                            alertDialog.setMessage("");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
                            alertDialog.setTitle("Error while setting profile");
                            alertDialog.setMessage("");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }


                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
                        alertDialog.setTitle("Error while setting profile");
                        alertDialog.setMessage("");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }


                });
            }
        });


        mAPIService = APIUtils.getAPIService();

        mAPIService.getProfile(Utils.getMyId(getBaseContext())).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                person = response.body();


               /* ArrayList<Person.Activities> likedActivities = new ArrayList<>();
                likedActivities.add(Person.Activities.Running);
               person = new Person("test", 12, "test description", 1.05,10.5, Person.Activities.Running, likedActivities, null, "1122");*/

                if (person != null) {
                    name.setText(person.getName());
                    description.setText(person.getDescription());

                    try {
                        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String s = formatter.format(person.getAge());

                        String date = s;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (person.getLikedActivities() != null) {
                        for (Person.Activities activity : person.getLikedActivities()) {
                            switch (activity) {
                                case Cycling:
                                    cycling.setChecked(true);
                                    break;
                                case Running:
                                    running.setChecked(true);
                                    break;
                                case Skateboarding:
                                    skateboarding.setChecked(true);
                                    break;
                                case NordicWalking:
                                    nordicwalking.setChecked(true);
                                    break;
                            }
                        }
                    }


                } else {
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
