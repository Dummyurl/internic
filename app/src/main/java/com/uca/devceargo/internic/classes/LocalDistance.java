package com.uca.devceargo.internic.classes;

public class LocalDistance {

    public LocalDistance() {
    }

    public String getStringDistance(Double value){
        String result = "";
        int meters = value.intValue();
        int kilometers;

        kilometers = meters / 1000;
        meters =(meters - (kilometers * 1000));

        if(kilometers > 0){
            result = result.concat(kilometers+" km ");
        }

        if(meters > 0){
            result = result.concat(meters + " m ");
        }

        return result;
    }
}
