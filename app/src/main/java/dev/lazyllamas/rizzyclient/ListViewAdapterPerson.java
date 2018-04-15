package dev.lazyllamas.rizzyclient;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.R;

public class ListViewAdapterPerson extends ArrayAdapter<Person> {

    private final Context context;
    private final ArrayList<Person> person;
    private int counter = 0;

    public ListViewAdapterPerson(Context context, ArrayList<Person> person) {
        super(context, 0, person);
        this.context = context;
        this.person = person;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        counter = 0;
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.listtextview, parent, false);

        Person curr_person = person.get(position);
        //TextView textView = listItem.findViewById(R.id.textView3);
        //ImageView imageView = listItem.findViewById(R.id.imageView4);
        TextView textView = listItem.findViewById(R.id.textView4);
        textView.setText(curr_person.getDescription());
        //textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setPadding(16,16,8,8);
        LinearLayout linearLayout = listItem.findViewById(R.id.linearayout1);
        linearLayout.removeAllViews();

        Button poke = listItem.findViewById(R.id.button5);
        if (curr_person.isPokedBySomeoneElse())
            poke.setText("Accept");

        for (int i=0; i<curr_person.getLikedActivities().size(); i++) {
            boolean yes = false;
            String text;
            if(curr_person.getLikedActivities().get(i) == curr_person.getCurrentActivities()){
                text = new String("¤ "+curr_person.getCurrentActivities().toString());
                yes  = true;
            }
            else{
                text = new String(curr_person.getLikedActivities().get(i).toString());
                yes = false;
            }
            TextView textView1 = new TextView(listItem.getContext());
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
            linearLayout.addView(textView1);
            counter++;
        }
        return listItem;
    }

    private static class ViewHolder {
        private TextView itemView;
        private ImageView imageView;
    }

}
