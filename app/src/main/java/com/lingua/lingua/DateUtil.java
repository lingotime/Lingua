package com.lingua.lingua;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
Class with helper methods for changing date formats
*/


public class DateUtil {

    // Converts from date format "Tue Jul 09 17:22:36 PDT 2019" to "5 min. ago"
    public static String getRelativeTimeAgo(String rawDate) {
        String parseFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(parseFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getHourAndMinuteFormat(String rawDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            Date timestamp = dateFormat.parse(rawDate);
            DateFormat hourFormat = new SimpleDateFormat("hh:mm a");
            String result = hourFormat.format(timestamp);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
