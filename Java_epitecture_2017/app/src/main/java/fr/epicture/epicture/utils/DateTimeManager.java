package fr.epicture.epicture.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.util.Date;

import fr.epicture.epicture.R;

public class DateTimeManager {

    private static String[] MonthArray = new String[] {
            "Jan", "Fev", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
    };

    private static String[] DaysSuffix = new String[] {
            "st", "nd", "rd"
    };

    public static String getUserFriendlyDateTime(Context context, long ms) {
        String ret = "null";

        try {
            Date date = new Date(ms * 1000L);
            DateTime dateTime = new DateTime(date.getTime());
            DateTime today = new DateTime();
            DateTime tomorrow = today.plusDays(1);

            int nbHours = Hours.hoursBetween(today, dateTime).getHours();
            int nbMinutes = Minutes.minutesBetween(today, dateTime).getMinutes();

            String tmpHour = dateTime.getHourOfDay() + "";
            String tmpMin = dateTime.getMinuteOfHour() + "";
            if (tmpHour.length() == 1) {
                tmpHour = "0" + tmpHour;
            }
            if (tmpMin.length() == 1) {
                tmpMin = "0" + tmpMin;
            }

            if (nbHours == 0) {
                if (nbMinutes == 0) {
                    ret = context.getString(R.string.date_transform_seconds);
                } else if (nbMinutes > 0) {
                    ret = context.getString(R.string.date_transform_minutes, nbMinutes);
                } else if (nbMinutes < 0) {
                    ret = "Today, " + tmpHour + ":" + tmpMin;
                }
            } else if (dateTime.toLocalDate().equals(today.toLocalDate())) {
                ret = "Today, " + tmpHour + ":" + tmpMin;
            } else if (dateTime.toLocalDate().equals(tomorrow.toLocalDate())) {
                ret = "Tomorrow, " + tmpHour + ":" + tmpMin;
            } else {
                String stringYear = String.valueOf(dateTime.getYear());
                String stringMonth = getMonth(dateTime.getMonthOfYear());
                String stringDay = getDay(dateTime.getDayOfMonth());

                ret = stringDay + " " + stringMonth + " " + stringYear + ", " + tmpHour + ":" + tmpMin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

    public static String getMonth(int month) {
        return (MonthArray[month - 1]);
    }

    public static String getYear(int year) {
        return (String.valueOf(year));
    }

    public static String getDay(int day) {
        String str = String.valueOf(day);

        if (day - 1 < DaysSuffix.length) {
            str += DaysSuffix[day - 1];
        } else {
            str += "th";
        }
        return (str);
    }
}
