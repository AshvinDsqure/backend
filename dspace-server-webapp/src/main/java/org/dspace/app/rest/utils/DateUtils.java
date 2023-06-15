package org.dspace.app.rest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static void main(String[] args) throws ParseException {

        Date d=new Date();
        System.out.println("test::"+DateFormate(d));
        System.out.println("test::"+DateToDateFormate(d));

    }

    private static String DateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return formatter.format(date);
    }
    private static Date DateToDateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date1=null;
        String datestr=formatter.format(date);
        System.out.println(datestr);
        try {
            date1=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(datestr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date1;
    }
}
