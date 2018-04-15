package dev.lazyllamas.rizzyclient.Business;

import android.content.Context;
import android.content.SharedPreferences;

import dev.lazyllamas.rizzyclient.R;

public class Utils {

    public static String getMyId(Context context) {

        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.tokenPrefKey), Context.MODE_PRIVATE);
        String token = pref.getString(context.getString(R.string.token), null);

        return token;
    }
}
