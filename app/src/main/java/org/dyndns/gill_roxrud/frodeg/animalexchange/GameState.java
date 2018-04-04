package org.dyndns.gill_roxrud.frodeg.animalexchange;

import org.dyndns.gill_roxrud.frodeg.animalexchange.activities.MapFragment;
import org.osmdroid.api.IGeoPoint;


public class GameState {

    private static GameState instance = null;

    private IGeoPoint positionHint = null;

    private final Point<Double> currentPos = new Point<>(MapFragment.EAST+1.0, MapFragment.NORTH+1.0);


    private GameState() {
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public Point<Double> getCurrentPos() {
        return currentPos;
    }

    public void onPositionChangedT(MapFragment mapFragment, double x_pos, double y_pos) {
        currentPos.set(x_pos, y_pos);
    }

    public void pushPositionHint(final IGeoPoint positionHint) {
        this.positionHint = positionHint;
    }

    public IGeoPoint popPositionHint() {
        IGeoPoint tmp = this.positionHint;
        this.positionHint = null;
        return tmp;
    }

}
