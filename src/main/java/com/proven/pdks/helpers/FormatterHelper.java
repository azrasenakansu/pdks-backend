package com.proven.pdks.helpers;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatterHelper {
    public static final DecimalFormat formatter = new DecimalFormat("00");
    public static final Locale locale = Locale.of("tr","TR");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


}
