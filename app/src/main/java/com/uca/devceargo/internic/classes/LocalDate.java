package com.uca.devceargo.internic.classes;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocalDate {

    public LocalDate() {
    }

    public String getDateInString(String dateISOformat){

       if(dateISOformat != null ){
           if(!dateISOformat.isEmpty()){
               DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.sssZ");
               DateTime time = parser1.parseDateTime(dateISOformat);
               Date date = time.toDate();

               return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(date);
           }
       }
       return "";
    }

    public String getDateInStringFormat(String simpleDate){

        if(simpleDate == null){
            simpleDate = "1900-1-1";
        }
        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime time = parser1.parseDateTime(simpleDate);
        Date date = time.toDate();
        return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(date);
    }

    public String getDateInStringWithHour(String dateISOformat){

        if(dateISOformat != null){
            if(!dateISOformat.isEmpty()){
                System.out.println("InterNIC "+dateISOformat);
                DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.sssZ");
                DateTime time = parser1.parseDateTime(dateISOformat);
                Date date = time.toDate();
                return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a",
                        new Locale("es", "ES")).format(date);
            }
        }

        return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a",
                new Locale("es", "ES")).format(new Date());
    }

    public String getDateISOformat(String simpleDate){

        if(simpleDate == null){
            simpleDate = "1900-01-01";
        }
        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime time = parser1.parseDateTime(simpleDate);
        Date date = time.toDate();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ", new Locale("es", "ES")).format(date);
    }

    public String getHourInString(double value){
        int day, minutes, seconds, hours, number;
        String result = "";

        number = (int) value;
        day = number / 86400;
        hours = (number - (86400 * day)) / 3600;
        minutes = (number - (day * 86400) - (hours * 3600)) / 60;
        seconds = (number - (day * 86400)  - (hours * 3600) - (minutes*60));

        if(day > 0){
            result = result.concat(day + " d ");
        }

        if(hours > 0){
            result = result.concat(hours + " h ");
        }

        if(minutes > 0){
            result = result.concat(minutes + " min ");
        }

        if (seconds > 0){
            result = result.concat(seconds + " seg");
        }

        System.out.println(result);

        return result;
    }
}
