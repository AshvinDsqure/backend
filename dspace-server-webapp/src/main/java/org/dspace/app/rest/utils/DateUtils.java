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
        System.out.println("test::"+DateToSTRDDMMYYYHHMMSS(d));

        System.out.println("test::"+strDateToString("2023-06-17 16:09:41.481"));

    }

    public static String DateToSTRDDMMYYYHHMMSS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return formatter.format(date);
    }
    public static String DateFormateDDMMYYYY(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
    public static String strDateToString(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return DateFormateDDMMYYYY(dateFormat.parse(date));
    }
    public static Date DateToDateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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
