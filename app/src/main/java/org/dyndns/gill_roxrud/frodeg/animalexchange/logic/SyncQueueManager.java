package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeDBHelper;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;

import java.util.ArrayList;
import java.util.List;


public class SyncQueueManager {

    private List<SyncQueueEvent> pendingEvents = new ArrayList<>();
    private double cachedFood;


    public SyncQueueManager() {
        initialize();
    }

    private void initialize() {
        AnimalExchangeDBHelper db = GameState.getInstance().getDB();
        cachedFood = db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_FOOD);

        pendingEvents = db.getSyncQueue();

        //Update food cache
        for (SyncQueueEvent event : pendingEvents) {
            switch (event.getEventType()) {
                case SyncQueueEvent.RECEIVE_FOOD: cachedFood += event.getValue2(); break;
                case SyncQueueEvent.FEED_ANIMAL:  cachedFood -= event.getValue2(); break;
                default: break;
            }
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
        return true;
    }

    public double getFood() {
        return cachedFood;
    }
}
