package com.irfsf.todo808956;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TheDateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm, dd/MMM/yy");

    /**
     * @param dateString string
     * @return a formatted date
     */
    public static Date getDateFromString(CharSequence dateString) {

        try {
            Date newDate = dateFormat.parse(String.valueOf(dateString));
            return newDate;
        } catch (ParseException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * @param date is the Date to be converted
     * @return a formatted String for the passed Date according to dateFormat
     */
    public static String getStringFromDate(Date date) {
        return dateFormat.format(date);
    }

}
