package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class AnimalExchangeApplication extends Application {

    public static final String HELP_URL = "https://gill-roxrud.dyndns.org/animalexchange";

    public static final long  LOCATION_UPDATE_INTERVAL = 30L; //seconds
    public static final float LOCATION_UPDATE_DISTANCE = 25.0f; //meters
    public static final float MAX_ALLOWED_SPEED = 20.0f; //km/h

    public static final double NORTH = 90.0; //degrees
    public static final double EAST = 180.0; //degrees
    public static final double SOUTH = -90.0; //degrees
    public static final double WEST = -180.0; //degrees
    public static final double VER_DEGREES = NORTH-SOUTH;
    public static final double HOR_DEGREES = EAST-WEST;

    public static final double AVERAGE_RADIUS_OF_EARTH = 6371000; //meters
    public static final double AVERAGE_CIRCUMFENCE_OF_EARTH = AVERAGE_RADIUS_OF_EARTH*2*Math.PI; //meters
    public static final double MAX_NORTH_POS = 80.0; //degrees
    public static final double MAX_SOUTH_POS = -80.0; //degrees

    public static final double MAX_ANIMAL_NORTH = 80.0; //degrees
    public static final double MAX_ANIMAL_SOUTH = -80.0; //degrees
    public static final double VER_ANIMAL_DEGREES = MAX_ANIMAL_NORTH - MAX_ANIMAL_SOUTH;


    private static AnimalExchangeApplication instance;

    public AnimalExchangeApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Thread.currentThread().setUncaughtExceptionHandler(new AnimalExchangeUncaughtExceptionHandler());
    }

    private static class AnimalExchangeUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("UncaughtException", "Got an uncaught exception: "+ex.toString());
            ex.printStackTrace();
        }
    }
}
