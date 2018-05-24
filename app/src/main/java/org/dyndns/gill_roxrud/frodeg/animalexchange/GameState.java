package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.content.Context;

import org.dyndns.gill_roxrud.frodeg.animalexchange.activities.MapFragment;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGiftManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;

import java.util.Calendar;
import java.util.TimeZone;


public class GameState {

    private static GameState instance = null;

    private static AnimalManager animalManager;
    private static AnimalGiftManager animalGiftManager;
    private static AnimalExchangeDBHelper db;
    private static SyncQueueManager syncQueueManager;

    private final Point<Double> currentPos = new Point<>(AnimalExchangeApplication.EAST+1.0, AnimalExchangeApplication.NORTH+1.0);


    private GameState() {
    }

    public static GameState getInstance() {
        if (GameState.instance == null) {
            GameState.instance = new GameState();
            GameState.animalManager = new AnimalManager();
            GameState.animalGiftManager = new AnimalGiftManager();
            GameState.db = new AnimalExchangeDBHelper(AnimalExchangeApplication.getContext());
            GameState.syncQueueManager = new SyncQueueManager();
        }
        return GameState.instance;
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

    public SyncQueueManager getSyncQueueManager() {
        return syncQueueManager;
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
            //Two separate transactions, but that is OK. Failing one should not fail both.
            AnimalManager.MovementInfo movementInfo = animalManager.requestFoodT(currentPos);
            animalGiftManager.requestAnimalGiftT(currentPos, getDay());

            mapFragment.onScoreUpdated(movementInfo.speed);
        } catch (InvalidPositionException e) {
        }
    }

    public int getDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.get(Calendar.YEAR)*366 + calendar.get(Calendar.DAY_OF_YEAR);
    }

}
