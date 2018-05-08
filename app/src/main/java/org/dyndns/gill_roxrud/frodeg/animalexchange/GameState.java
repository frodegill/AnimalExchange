package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.content.Context;

import org.dyndns.gill_roxrud.frodeg.animalexchange.activities.MapFragment;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGiftManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;

import java.util.Calendar;
import java.util.TimeZone;


public class GameState {

    private static GameState instance = null;

    private final AnimalManager animalManager;
    private final AnimalGiftManager animalGiftManager;
    private final AnimalExchangeDBHelper db;

    private final Point<Double> currentPos = new Point<>(AnimalExchangeApplication.EAST+1.0, AnimalExchangeApplication.NORTH+1.0);


    private GameState() {
        Context context = AnimalExchangeApplication.getContext();
        animalManager = new AnimalManager();
        animalGiftManager = new AnimalGiftManager();
        db = new AnimalExchangeDBHelper(context);
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public AnimalManager getAnimalManager() {
        return animalManager;
    }

    public AnimalGiftManager getAnimalGiftManager() {
        return animalGiftManager;
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
        try {
            AnimalManager.MovementInfo movementInfo = animalManager.requestFoodT(currentPos);

            if (null!=animalGiftManager.requestAnimalGiftT(currentPos, getDay())) {
            }
            mapFragment.onScoreUpdated(movementInfo.speed);
        } catch (InvalidPositionException e) {
        }
    }

    public int getDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.get(Calendar.YEAR)*366 + calendar.get(Calendar.DAY_OF_YEAR);
    }

}
