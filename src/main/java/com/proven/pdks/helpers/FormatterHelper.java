package com.proven.pdks.helpers;

import java.text.DecimalFormat;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatterHelper {
    public static final DecimalFormat formatter = new DecimalFormat("00");
    public static final Locale locale = Locale.of("tr","TR");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public static String textifyMonth(Month month){
        return switch (month){
            case JANUARY -> "Ocak";
            case FEBRUARY -> "Şubat";
            case MARCH -> "Mart";
            case APRIL -> "Nisan";
            case MAY -> "Mayıs";
            case JUNE -> "Haziran";
            case JULY -> "Temmuz";
            case AUGUST -> "Ağustos";
            case SEPTEMBER -> "Eylül";
            case OCTOBER -> "Ekim";
            case NOVEMBER -> "Kasım";
            case DECEMBER -> "Aralık";
        };
    }

}
