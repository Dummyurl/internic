package com.uca.devceargo.internic.classes;

public class HourString {
    private Double seconds;

    public HourString (Double seconds){
        this.seconds = seconds;
    }

    public String getHourInString(){
        String[] hourSplit = seconds.toString().split(".");
        String hour = "";
        if(hourSplit.length > 1){
            if(Integer.parseInt(hourSplit[0]) > 1){
                hour = hourSplit[0];
                int min = Integer.parseInt(hourSplit[1].substring(0,1));
                min = (60 * min)/100;
                hour += ":"+min;
            }
        }
        return hour;
    }
}
