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

        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.sssZ");
        DateTime time = parser1.parseDateTime(dateISOformat);
        Date date = time.toDate();

        return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(date);
    }

    public String getDateInStringWithHour(String dateISOformat){

        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.sssZ");
        DateTime time = parser1.parseDateTime(dateISOformat);
        Date date = time.toDate();

        return new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a",
                new Locale("es", "ES")).format(date);
    }

    public String getDateISOformat(String simpleDate){
        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime time = parser1.parseDateTime(simpleDate);
        Date date = time.toDate();

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ", new Locale("es", "ES")).format(date);
    }
}
