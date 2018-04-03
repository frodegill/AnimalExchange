package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class AnimalExchangeApplication extends Application {

    public static final String HELP_URL = "https://gill-roxrud.dyndns.org/animalexchange";

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
