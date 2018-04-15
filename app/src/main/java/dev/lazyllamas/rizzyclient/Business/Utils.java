package dev.lazyllamas.rizzyclient.Business;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

import dev.lazyllamas.rizzyclient.R;

public class Utils {

    public static String getMyId(Context context) {

        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.tokenPrefKey), Context.MODE_PRIVATE);
        String token = pref.getString(context.getString(R.string.token), null);

        return token;
    }

    public static int getAge(Date dateOfBirth) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar birthDate = Calendar.getInstance();

            int age = 0;

            birthDate.setTime(dateOfBirth);
            if (birthDate.after(today)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }

            age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
            if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                    (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
                age--;

                // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
            } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                    (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }
}
