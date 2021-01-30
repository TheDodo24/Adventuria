package de.thedodo24.commonPackage.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormat {

    public static String getString(long time) {
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;
        long hours = (time / (1000 * 60 * 60)) % 24;
        long days = (time / (1000*60*60*24));
        String day = "";
        String hour = "";
        String minute = "";
        String second = "";
        if(days == 1)
            day = days + " Tag";
        else if(days > 1)
            day = days + " Tage";
        if(hours == 1)
            hour = hours + " Stunde";
        else if(hours > 1)
            hour  = hours + " Stunden";
        if(minutes == 1)
            minute = minutes + " Minute";
        else if(minutes > 1)
            minute = minutes + " Minuten";
        if(seconds == 1)
            second = seconds + " Sekunde";
        else if(seconds > 1)
            second = seconds + " Sekunden";
        return (!day.equalsIgnoreCase("") ? day + ", " : "") +  (!hour.equalsIgnoreCase("") ? hour + ", " : "") +
                (!minute.equalsIgnoreCase("") ? minute + " und " : "") + (!second.equalsIgnoreCase("") ? second : "0 Sekunden");
    }

    public static String getOutOfSeconds(int sec) {
        int minutes = sec / 60;
        return (minutes == 1 ? minutes + " Minute" : minutes + " Minuten");
    }

    public static double getInMinutesString(long time) {
        long minutes = ((long) (((double) time / (1000 * 60)) * 100));
        return ((Long) minutes).doubleValue() / 100;
    }

    public static int getInHours(long time) {
        return ((int) (((double) time / (1000 * 60 * 60))));
    }

    public static double getInHoursDouble(long time) {
        long h =  ((int) (((double) time / (1000 * 60 * 60)) * 100));
        return ((Long) h).doubleValue() / 100;
    }

    public static int getInMinutes(long time) {
        long minutes = ((long) (((double) time / (1000 * 60)) * 100));
        return (int) (((Long) minutes).doubleValue() / 100);
    }

    public static String getInDays(long time) {
        long hours = ((long) (((double) time / (1000 * 60 * 60)) * 100));
        String hour;
        if(hours == 100)
            hour = " Stunde";
        else
            hour = " Stunden";
        return ((Long) hours).doubleValue() / 100 + hour;
    }

    public static final int ticksAtMidnight = 18000;
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = 1000;
    public static final double ticksPerMinute = 1000d / 60d;
    public static final double ticksPerSecond = 1000d / 60d / 60d;

    public static Date ticksToDate(long ticks)
    {
        // Assume the server time starts at 0. It would start on a day.
        // But we will simulate that the server started with 0 at midnight.
        ticks = ticks - ticksAtMidnight + ticksPerDay;

        // How many ingame days have passed since the server start?
        final long days = ticks / ticksPerDay;
        ticks -= days * ticksPerDay;

        // How many hours on the last day?
        final long hours = ticks / ticksPerHour;
        ticks -= hours * ticksPerHour;

        // How many minutes on the last day?
        final long minutes = (long)Math.floor(ticks / ticksPerMinute);
        final double dticks = ticks - minutes * ticksPerMinute;

        // How many seconds on the last day?
        final long seconds = (long)Math.floor(dticks / ticksPerSecond);

        // Now we create an english GMT calendar (We wan't no daylight savings)
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.GERMAN);
        cal.setLenient(true);

        // And we set the time to 0! And append the time that passed!
        cal.set(0, Calendar.JANUARY, 1, 0, 0, 0);
        cal.add(Calendar.DAY_OF_YEAR, (int)days);
        cal.add(Calendar.HOUR_OF_DAY, (int)hours);
        cal.add(Calendar.MINUTE, (int)minutes);
        cal.add(Calendar.SECOND, (int)seconds + 1); // To solve rounding errors.

        return cal.getTime();
    }

    /*public static String getString(long time) {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        while(time > 1000L) {
            time -= 1000L;
            seconds++;
        }
        while(seconds > 60) {
            seconds -= 60;
            minutes++;
        }
        while(minutes > 60) {
            minutes -= 60;
            hours++;
        }
        while(hours > 24) {
            hours -= 24;
            days++;
        }
        String day = "";
        String hour = "";
        String minute = "";
        String second = "";
        if(days == 1)
            day = days + " Tag";
        else if(days > 1)
            day = days + " Tage";
        if(hours == 1)
            hour = hours + " Stunde";
        else if(hours > 1)
            hour  = hours + " Stunden";
        if(minutes == 1)
            minute = minutes + " Minute";
        else if(minutes > 1)
            minute = minutes + " Minuten";
        if(seconds == 1)
            second = seconds + " Sekunde";
        else if(seconds > 1)
            second = seconds + " Sekunden";
        return (!day.equalsIgnoreCase("") ? day + ", " : "") +  (!hour.equalsIgnoreCase("") ? hour + ", " : "") +
                (!minute.equalsIgnoreCase("") ? minute + " und " : "") + (!second.equalsIgnoreCase("") ? second : "Keine Ontime");
    }*/

}
