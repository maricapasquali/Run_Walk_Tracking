package com.run_walk_tracking_gps.utilities;
import java.util.regex.PatternSyntaxException;

public class DurationUtilities {


    public static Integer stringToSeconds(String duration){
        try{
            String[] split = duration.split(":");
            int hours = Integer.valueOf(split[0]);
            int minutes = Integer.valueOf(split[1]);
            int sec = Integer.valueOf(split[2]);
            return sec + (minutes*60)+(hours*3600);

        }catch (PatternSyntaxException e){
            return 0;
        }
    }

    public static String  format(int seconds){
        int hours = seconds / 3600;
        int minutes = (seconds /60) % 60;
        int sec = seconds%60;
        return String.format("%02d:%02d:%02d", hours, minutes, sec);

    }

}
