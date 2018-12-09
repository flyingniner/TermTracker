package com.termtacker;

import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public class Utils
{
    public static DateTimeFormatter dateFormatter_MMMddyyyy = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static DateTimeFormatter dateFormatter_MMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static Formatter phoneNumber = new Formatter().format("(000) 000-0000");
}
