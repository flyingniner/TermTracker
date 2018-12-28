package com.termtacker.utilities;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class Utils
{
    public static DateTimeFormatter dateFormatter_MMMddyyyy = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static DateTimeFormatter dateFormatter_MMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    /**
     * Converts a phone number stored as a long to its string representation: (###) ###-####
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNumber(long phoneNumber)
    {
        return String.valueOf(phoneNumber).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
    }

    /**
     * Converts a String date "MM/dd/yyyy" to a LocalDate object
     * @param dateString
     * @return
     */
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

    /**
     * Converts a String date "MM/dd/yyyy" to it's repesentation in milliseconds from Epoch.
     * The time is set to 8:00AM of the default system date
     * @param localDateStr
     * @return
     */
    public static long convertStringDateToMillis(String localDateStr)
    {
        LocalDate ld = convertStringDate(localDateStr);
        LocalDateTime ldt = ld.atStartOfDay().plusHours(8);
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
