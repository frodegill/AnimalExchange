package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.content.Context;

import org.dyndns.gill_roxrud.frodeg.animalexchange.activities.MapFragment;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGift;

import java.util.Calendar;
import java.util.TimeZone;


public class GameState {

    private static GameState instance = null;

    private final AnimalGift animalGift;
    private final AnimalExchangeDBHelper db;

    private final Point<Double> currentPos = new Point<>(AnimalExchangeApplication.EAST+1.0, AnimalExchangeApplication.NORTH+1.0);


    private GameState() {
        Context context = AnimalExchangeApplication.getContext();
        animalGift = new AnimalGift();
        db = new AnimalExchangeDBHelper(context);
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public AnimalGift getAnimalGift() {
        return animalGift;
    }

    public AnimalExchangeDBHelper getDB() {
        return db;
    }

    public boolean getUseDataConnection() {
        return true; //TODO
    }

    public boolean getSnapToCentre() {
        return true; //TODO
    }

    public Point<Double> getCurrentPos() {
        return currentPos;
    }

    public void onPositionChangedT(MapFragment mapFragment, double x_pos, double y_pos) {
        currentPos.set(x_pos, y_pos);
    }

    public int getDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.get(Calendar.YEAR)*366 + calendar.get(Calendar.DAY_OF_YEAR);
    }

}
