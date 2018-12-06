package com.termtacker;

import java.time.format.DateTimeFormatter;

public class Utils
{
    public static DateTimeFormatter dateFormatter_MMMddyyyy = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static DateTimeFormatter dateFormatter_MMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");
}
