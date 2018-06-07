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

    public static final int FED = 0;
    public static final int HUNGRY = 1;
    public static final int FOR_SALE = 2;
    private int animals[][] = new int[AnimalManager.getAnimalDefinitionCount()][3/*FED, HUNGRY and FOR_SALE*/];
    private double cachedFood;
    private double money;


    public SyncQueueManager() {
        initialize();
    }

    private void initialize() {
        AnimalExchangeDBHelper db = GameState.getInstance().getDB();
        db.initializeAnimalCache(animals);
        cachedFood = db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_FOOD);

        pendingEvents = db.getSyncQueue();

        for (SyncQueueEvent event : pendingEvents) {
            updateCounters(event.getEventType(), event.getValue2());
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

        updateCounters(eventType, v2);

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

    private void updateCounters(final int v1, final double v2) {
        switch (v1) {
            case SyncQueueEvent.RECEIVE_GIFT: {
                animals[v1][HUNGRY]++;
                break;
            }

            case SyncQueueEvent.RECEIVE_FOOD: {
                cachedFood += v2;
                break;
            }

            case SyncQueueEvent.FEED_ANIMAL: {
                AnimalDefinition animalDef = GameState.getInstance().getAnimalManager().getAnimalDefinitionByType(v1);
                cachedFood -= animalDef.getFoodRequired();
                animals[v1][HUNGRY]--;
                animals[v1][FED]++;
                break;
            }

            case SyncQueueEvent.CONFIRM_BUY_ANIMAL: {
                animals[v1][FED]++;
                money -= v2;
                break;
            }

            case SyncQueueEvent.CONFIRM_SELL_ANIMAL: {
                animals[v1][FED]--;
                money += v2;
                break;
            }

            case SyncQueueEvent.CONFIRM_SELL_ANIMALGROUP: {
                AnimalGroup animalGroup = GameState.getInstance().getAnimalManager().getAnimalGroupByLevel(v1);
                for (AnimalDefinition animal : animalGroup.getAnimalDefinitionList()) {
                    animals[animal.getLevel()][FED]--;
                }
                money += v2;
                break;
            }

            case SyncQueueEvent.CONFIRM_BUY_FOOD: {
                cachedFood += v1;
                money -= v2;
                break;
            }

            case SyncQueueEvent.CONFIRM_SELL_FOOD: {
                cachedFood -= v1;
                money += v2;
                break;
            }

            case SyncQueueEvent.REQUEST_BUY_ANIMAL:
            case SyncQueueEvent.CANCEL_BUY_ANIMAL:
            case SyncQueueEvent.CONFIRM_CANCEL_BUY_ANIMAL:
            case SyncQueueEvent.REQUEST_SELL_ANIMAL:
            case SyncQueueEvent.CANCEL_SELL_ANIMAL:
            case SyncQueueEvent.CONFIRM_CANCEL_SELL_ANIMAL:
            case SyncQueueEvent.REQUEST_SELL_ANIMALGROUP:
            case SyncQueueEvent.REQUEST_BUY_FOOD:
            case SyncQueueEvent.REQUEST_SELL_FOOD:
            default: break;
        }
    }
}
