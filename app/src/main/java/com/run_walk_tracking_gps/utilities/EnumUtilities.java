package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtilities {

    private final static String TAG = EnumUtilities.class.getName();

    private final static String VALUES = "values";
    private final static String STRID = "getStrId";
    private final static String ICONID ="getIconId";


    private static int description(Class<?> enumeration){
        if(enumeration== Target.class){
            return R.string.target;
        }
        if(enumeration== Gender.class){
            return R.string.gender;
        }
        if(enumeration== Sport.class){
            return R.string.sport;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Integer[] valuesStrId(Class<?> enumeration) {
        List<Integer> enumStrIds = new ArrayList<>();
        try {
            final Method valuesMethod = enumeration.getDeclaredMethod(VALUES);
            final Method getStrIdMethod = enumeration.getMethod(STRID);

            enumStrIds = Arrays.asList((Object[]) valuesMethod.invoke(null))
                    .stream().map(e -> {
                        Integer strId =null;
                        try {
                            strId = (Integer) getStrIdMethod.invoke(e);
                        } catch (IllegalAccessException e1) {
                            Log.e(TAG, e1.getMessage());
                        } catch (InvocationTargetException e1) {
                            Log.e(TAG, e1.getMessage());
                        }
                        return strId;
                    }).collect(Collectors.toList());

        }catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
        }catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage());
        }
        return enumStrIds.stream().toArray(Integer[]::new);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Integer[] valuesWithDescription(Class<?> enumeration) {
       final List<Integer> values = new ArrayList<>(Arrays.asList(valuesStrId(enumeration)));
       values.add(0, description(enumeration));
       return values.stream().toArray(Integer[]::new);
    }

     @RequiresApi(api = Build.VERSION_CODES.N)
    public static Enum<?> getEnumFromStrId(Class<?> enumeration, int strId){
        Enum genEnum =null;
        try {
            final Method valuesMethod = enumeration.getDeclaredMethod(VALUES);
            final Method getStrIdMethod = enumeration.getMethod(STRID);
            genEnum = (Enum)Arrays.asList((Object[])valuesMethod.invoke(null)).stream()
                    .filter( e -> {
                        Integer s =null;
                        try {
                            s = (Integer) getStrIdMethod.invoke(e);
                        } catch (IllegalAccessException e1) {
                            Log.e(TAG, e1.getMessage());
                        } catch (InvocationTargetException e1) {
                            Log.e(TAG, e1.getMessage());
                        }
                        return s==strId;
                    }).findFirst().orElse(null);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return genEnum;
    }


    public static boolean isNotDescription(Class<?> enumeration, int valueStrId) {
        return valueStrId != description(enumeration);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int iconOfStrId(Class<?> enumeration, int strId) {
        Integer iconId = null;
        try {
            final Method valuesMethod = enumeration.getDeclaredMethod(VALUES);
            final Method getStrIdMethod = enumeration.getMethod(STRID);
            final Method getIconIdMethod = enumeration.getMethod(ICONID);


            iconId = Arrays.asList((Object[]) valuesMethod.invoke(null))
                    .stream().filter(e -> {
                        boolean match = false;
                        try {
                            match = (int)getStrIdMethod.invoke(e)==strId;
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        }
                        return match;
                    })
                    .findFirst()
                    .map(en -> {
                        Integer i = null;
                        try {
                            i = (Integer)getIconIdMethod.invoke(en);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return i;
                    })
                    .orElse(null);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return iconId;
    }


    public static Enum<?> getEnumFromString(Class<?> enumeration, Context context, String string){
        return getEnumFromStrId(enumeration, Stream.of(valuesStrId(enumeration)).filter(i -> context.getString(i).equals(string)).findFirst()
                .orElse(0));
    }

}
