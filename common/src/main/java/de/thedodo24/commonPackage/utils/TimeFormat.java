package de.thedodo24.commonPackage.utils;

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
