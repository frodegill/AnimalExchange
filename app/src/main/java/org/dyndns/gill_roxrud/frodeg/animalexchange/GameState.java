package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.MapFragment;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGiftManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.Point;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;

import java.util.Calendar;
import java.util.TimeZone;


public class GameState {

    private static GameState instance = null;

    private static AnimalManager animalManager;
    private static AnimalGiftManager animalGiftManager;
    private static AnimalExchangeDBHelper db;
    private static SyncQueueManager syncQueueManager;

    private final Point<Double> currentPos = new Point<>(AnimalExchangeApplication.EAST+1.0, AnimalExchangeApplication.NORTH+1.0);
    private double currentSpeed = 0.0;


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

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void savePositionT(final MapView mapView) {
        IGeoPoint center = mapView.getMapCenter();
        double x = center.getLongitude();
        double y = center.getLatitude();
        if (0.0!=x && 0.0!=y) {
            boolean successful = true;
            SQLiteDatabase dbInTransaction = db.StartTransaction();
            try {
                db.SetDoubleProperty(dbInTransaction, AnimalExchangeDBHelper.PROPERTY_X_POS, x);
                db.SetDoubleProperty(dbInTransaction, AnimalExchangeDBHelper.PROPERTY_Y_POS, y);
                db.SetDoubleProperty(dbInTransaction, AnimalExchangeDBHelper.PROPERTY_ZOOM_LEVEL, mapView.getZoomLevelDouble());
            } catch (SQLException e) {
                successful = false;
                Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            db.EndTransaction(dbInTransaction, successful);
        }
    }

    public void loadPosition(final MapFragment mapFragment, final MapView mapView) {
        double x = db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_X_POS);
        double y = db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_Y_POS);
        if (0.0!=x && 0.0!=y) {
            mapView.getController().setZoom(db.GetDoubleProperty(AnimalExchangeDBHelper.PROPERTY_ZOOM_LEVEL));
            onPositionChangedT(mapFragment, x, y);
        }
    }

    public void onPositionChangedT(final MapFragment mapFragment, double x_pos, double y_pos) {
        currentPos.set(x_pos, y_pos);
        try {
            //Two separate transactions, but that is OK. Failing one should not fail both.
            AnimalManager.MovementInfo movementInfo = animalManager.requestFoodT(currentPos); //Transaction #1
            currentSpeed = movementInfo.speed;
            if (AnimalExchangeApplication.MAX_ALLOWED_SPEED>=movementInfo.speed) {
                animalGiftManager.requestAnimalGiftT(currentPos, getDay()); //Transaction #2
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
