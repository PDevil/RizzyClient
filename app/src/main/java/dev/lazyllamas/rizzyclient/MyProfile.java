package dev.lazyllamas.rizzyclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyProfile extends AppCompatActivity {

    EditText name;
    EditText date;
    EditText description;
    CheckBox running;
    CheckBox cycling;
    CheckBox skateboarding;
    CheckBox nordicwalking;
    private APIService mAPIService;
    private Person person;

    public static final int GALLERY_REQUEST = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    uploadFile(selectedImage);
                    break;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.nameText);
        date = findViewById(R.id.Birthday);
        description = findViewById(R.id.descriptionText);

        running = findViewById(R.id.runningCheckBox);
        skateboarding = findViewById(R.id.skateboardingCheckBox);
        cycling = findViewById(R.id.cyclingCheckBox);
        nordicwalking = findViewById(R.id.nordicwalkingCheckBox);

        Button button_img = findViewById(R.id.button6);
        button_img.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }});


        Button button = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dateval = formatter.parse(date.getText().toString());
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

                        date.setText(s);
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void uploadFile(Uri fileUri) {

        //creating a file
        File file = new File(getRealPathFromURI(fileUri));

        //creating request body for file
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        APIService apiservice = APIUtils.getAPIService();


        Call<ResponseBody> call = apiservice.uploadImage(requestFile);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
