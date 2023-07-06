package org.dspace.app.rest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static void main(String[] args) throws ParseException {
        Date d=new Date();
        System.out.println(getFinancialYear());
        System.out.println(getShortName("Computer Application"));
       // System.out.println("test::"+strDateToString("2023-06-17 16:09:41.481"))






    }
    
    public  static  String getShortName(String str){
        StringBuilder initials = new StringBuilder();
        for (String word : str.split(" ")) {
            initials.append(word.charAt(0));
        }
        return initials.toString();
    }

    public static String getFinancialYear (){
        LocalDate today = LocalDate.now();
        System.out.println("todate date"+today);
        int year = today.getYear();
        int month = today.getMonthValue();
        String financialYear;
        String financialYears;
        if (month <= 3) {
            financialYear = String.format("%d-%d", year - 1, year);
        } else {
            financialYear = String.format("%d-%d", year, year + 1);
        }
        String s[]=financialYear.split("-");
        financialYears=s[0].toString().substring(2)+"-"+s[1].toString().substring(2);
        return  financialYears;
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
