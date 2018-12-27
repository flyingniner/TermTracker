package com.termtacker.utilities;

import java.time.LocalDate;

import androidx.room.TypeConverter;

public class Converters
{
    @TypeConverter
    public static LocalDate fromTimeStamp(Long value)
    {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(LocalDate date)
    {
        return date == null ? null : date.toEpochDay();
    }
}
