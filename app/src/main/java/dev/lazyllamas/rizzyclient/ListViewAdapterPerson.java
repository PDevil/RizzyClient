package dev.lazyllamas.rizzyclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dev.lazyllamas.rizzyclient.Business.Person;
import dev.lazyllamas.rizzyclient.R;

public class ListViewAdapterPerson extends ArrayAdapter<Person> {

    private final Context context;
    private final ArrayList<Person> person;

    public ListViewAdapterPerson(Context context, ArrayList<Person> person) {
        super(context, 0, person);
        this.context = context;
        this.person = person;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.listtextview, parent, false);

        Person curr_person = person.get(position);

        TextView textView = listItem.findViewById(R.id.textView3);
        ImageView imageView = listItem.findViewById(R.id.imageView4);

        return listItem;
    }

    private static class ViewHolder {
        private TextView itemView;
        private ImageView imageView;
    }

}
