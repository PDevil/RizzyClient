package dev.lazyllamas.rizzyclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dev.lazyllamas.rizzyclient.Business.APIService;
import dev.lazyllamas.rizzyclient.Business.APIUtils;
import dev.lazyllamas.rizzyclient.Business.ErrorState;
import dev.lazyllamas.rizzyclient.Business.NewUser;
import dev.lazyllamas.rizzyclient.Business.Person;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private APIService mAPIService;

    public void sendPOST(String email, String password, final Context context) {
        NewUser newUser = new NewUser(email, password);
        mAPIService.userSignUp(newUser).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 201) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                    alertDialog.setTitle(context.getString(R.string.accountcreatedSuccessfully));
                    alertDialog.setMessage(context.getString(R.string.accountcreatedSub));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    String location = response.headers().get("Location");
                    String value = location.split("profile/")[1];

                    Log.e("UUID", value);

                    SharedPreferences pref = getSharedPreferences(context.getString(R.string.tokenPrefKey), MODE_PRIVATE);
                    pref.edit().putString(context.getString(R.string.token), value);
                    pref.edit().commit();
                } else {
                    Gson gson = new GsonBuilder().create();
                    ErrorState err = new ErrorState();
                    try {
                        err = gson.fromJson(response.errorBody().string(), ErrorState.class);
                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage(err.getError_message());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } catch (IOException e) {
                        // handle failure to read error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Internal error. Check internet connection");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mAPIService = APIUtils.getAPIService();

        Button register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailRegister = findViewById(R.id.emailRegister);
                EditText passwordRegister = findViewById(R.id.passwordRegister);

                sendPOST(emailRegister.getText().toString(), passwordRegister.getText().toString(), getBaseContext());
            }

        });




    }

}
