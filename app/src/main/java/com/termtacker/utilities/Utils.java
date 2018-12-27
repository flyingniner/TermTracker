package com.termtacker.utilities;

import java.text.NumberFormat;
import java.time.LocalDate;
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

    public static LocalDate convertStringDate(String dateString)
    {
        if (dateString.isEmpty())
            return null;

        String[] array = dateString.split("/");

        return LocalDate.of(
                Integer.parseInt(array[2]),
                Integer.parseInt(array[0]),
                Integer.parseInt(array[1]));
    }
}
