package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeDBHelper;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;

import java.util.ArrayList;
import java.util.List;


public class SyncQueueManager {

    private List<SyncQueueEvent> pendingEvents = new ArrayList<>();

    private static final int FED = 0;
    private static final int HUNGRY = 1;
    private int animals[][] = new int[AnimalManager.getAnimalDefinitionCount()][2/*FED or HUNGRY*/];
    private double cachedFood;


    public SyncQueueManager() {
        initialize();
    }

    private void initialize() {
        for (int i=0; i<AnimalManager.getAnimalDefinitionCount(); i++) {
            animals[i][FED] = animals[i][HUNGRY] = 0;
        }

        AnimalExchangeDBHelper db = GameState.getInstance().getDB();
        cachedFood = db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_FOOD);

        pendingEvents = db.getSyncQueue();

        //Update food cache
        for (SyncQueueEvent event : pendingEvents) {
            addEventToFoodCache(event.getEventType(), event.getValue2());
        }
    }

    public void sync() {
    }

    public boolean appendT(final int eventType, final int v1, final double v2) {
        boolean successful;
        AnimalExchangeDBHelper db = GameState.getInstance().getDB();
        SQLiteDatabase dbInTransaction = db.StartTransaction();

        try {
            successful = append(dbInTransaction, eventType, v1, v2);
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        db.EndTransaction(dbInTransaction, successful);
        return successful;
    }

    public boolean append(final SQLiteDatabase dbInTransaction, final int eventType, final int v1, final double v2) {
        AnimalExchangeDBHelper db = GameState.getInstance().getDB();

        SyncQueueEvent previousEvent = pendingEvents.isEmpty() ? null : pendingEvents.get(pendingEvents.size()-1);

        if (SyncQueueEvent.RECEIVE_FOOD==eventType &&
            previousEvent!=null && SyncQueueEvent.RECEIVE_FOOD==previousEvent.getEventType()) {
            try {
                previousEvent.incrementValue2(v2);
                db.updateEvent(dbInTransaction, previousEvent);
            } catch (Exception e) {
                previousEvent.decrementValue2(v2); //If storing failed, revert to be in sync
                throw e;
            }
        } else {
            SyncQueueEvent newEvent = new SyncQueueEvent(eventType, v1, v2);
            if (!db.addEvent(dbInTransaction, newEvent)) {
                return false;
            }
            pendingEvents.add(newEvent);
        }

        addEventToFoodCache(eventType, v2);

        return true;
    }

    public int getAnimalCount(final int animalType) {
        if (animalType<0 || animalType>=AnimalManager.getAnimalDefinitionCount()) {
            return 0;
        }
        return animals[animalType][FED] + animals[animalType][HUNGRY];
    }

    public double getFood() {
        return cachedFood;
    }

    private void addEventToFoodCache(final int eventType, final double v2) {
        switch (eventType) {
            case SyncQueueEvent.RECEIVE_FOOD: cachedFood += v2; break;
            case SyncQueueEvent.FEED_ANIMAL:  cachedFood -= v2; break;
            default: break;
        }
    }
}
