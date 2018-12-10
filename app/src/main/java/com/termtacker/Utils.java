package com.termtacker;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class Utils
{
    public static DateTimeFormatter dateFormatter_MMMddyyyy = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static DateTimeFormatter dateFormatter_MMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//    public static NumberFormat phoneNumber = new Formatter().format("(000) 000-0000");

    public static String formatPhoneNumber(long phoneNumber)
    {
        return String.valueOf(phoneNumber).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
    }
}
