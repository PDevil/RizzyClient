package dev.lazyllamas.rizzyclient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.lazyllamas.rizzyclient.Business.Person;

public class ActivitiesActivity extends AppCompatActivity {

    private Person person;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        LinearLayout linearLayout = findViewById(R.id.activitiesID);
        for(int i=0; i<person.getLikedActivities().size(); i++){
            boolean yes = false;
            String text;
            if(person.getLikedActivities().get(i) == person.getCurrentActivities()){
                text = new String("¤ "+person.getCurrentActivities().toString());
                yes  = true;
            }
            else{
                text = new String(person.getLikedActivities().get(i).toString());
                yes = false;
            }
            TextView textView1 = new TextView(getApplicationContext());
            if(yes == true){
                textView1.setTextColor(ContextCompat.getColor(context,R.color.activeActivityColor));
            }
            else{
                textView1.setTextColor(ContextCompat.getColor(context,R.color.textColor));
            }
            textView1.setText(text);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView1.setGravity(Gravity.CENTER_VERTICAL);
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
            textView1.setPadding(8,8,8,8);
            ((LinearLayout) linearLayout).addView(textView1);
        }
    }
}