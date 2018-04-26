package org.dyndns.gill_roxrud.frodeg.animalexchange.overlays;


import android.graphics.*;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.Animal;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGiftManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.HashSet;


public class AnimalGiftOverlay extends Overlay {

    static final private int MINIMUM_DRAW_LEVEL = 12;

    private static final Paint black = new Paint();


    public AnimalGiftOverlay() {
        super();
        black.setColor(Color.argb(0x80, 0x00, 0x00, 0x00));
        black.setStyle(Paint.Style.STROKE);
    }

    private void draw(Canvas canvas, MapView mapView, IGeoPoint ne, IGeoPoint sw) {
        int day = GameState.getInstance().getDay();

        AnimalManager animalManager = GameState.getInstance().getAnimalManager();
        AnimalGiftManager animalGiftManager = GameState.getInstance().getAnimalGiftManager();

        int drawLevel = mapView.getZoomLevel();
        if (MINIMUM_DRAW_LEVEL > drawLevel) {
            return;
        }

        int topGift = animalGiftManager.ToVerticalGift(ne.getLatitude());
        int leftGift = animalGiftManager.ToHorizontalGift(sw.getLongitude());
        int bottomGift = animalGiftManager.ToVerticalGift(sw.getLatitude());
        int rightGift = animalGiftManager.ToHorizontalGift(ne.getLongitude());

        android.graphics.Point point = null;
        GeoPoint geoPoint;

        Projection projection = mapView.getProjection();
        float radius = projection.metersToPixels(AnimalGiftManager.GIFT_SIZE_RADIUS);
        int animalImageSize = (int)(radius*2);

        float halfStrokeWidth = radius/50;
        black.setStrokeWidth(2*halfStrokeWidth);

        HashSet<Integer> drawnBonuses = new HashSet<>();
        int x, y, key;
        for (y=bottomGift; y<=(topGift+1); y++) {
            for (x=leftGift; x<=(rightGift+1); x++) {
                try {
                    key = animalGiftManager.ToBonusKey(x, y);
                } catch (InvalidPositionException e) {
                    continue;
                }

                if (animalGiftManager.isAwardedT(key, day) || drawnBonuses.contains(key)) {
                    continue;
                }

                geoPoint = animalGiftManager.GeoPointFromGrid(x, y, day);
                point = projection.toProjectedPixels(geoPoint, point);
                point = projection.toPixelsFromProjected(point, point);

                long distributionValue = AnimalManager.calculateAnimalDistributionValue(x, y, day);
                Animal animal = animalManager.getAnimalFromDistributionValue(distributionValue);
                Bitmap animalBitmap;
                if (animalManager.isHiddenAnimalGift(distributionValue)) {
                    animalBitmap = animalManager.getHiddenAnimalGiftBitmap(AnimalExchangeApplication.getContext(), animalImageSize);
                } else {
                    animalBitmap = animal.getRoundedBitmap(AnimalExchangeApplication.getContext(), animalImageSize);
                }
                canvas.drawBitmap(animalBitmap, point.x-radius, point.y-radius, null);

                canvas.drawCircle(point.x, point.y, radius+halfStrokeWidth, black);

                drawnBonuses.add(key);
            }
        }
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) {
            return;
        }

        Projection projection = mapView.getProjection();
        IGeoPoint ne = projection.getNorthEast();
        IGeoPoint sw = projection.getSouthWest();
        if (AnimalExchangeApplication.MAX_NORTH_POS < sw.getLatitude() ||
            AnimalExchangeApplication.MAX_SOUTH_POS > ne.getLatitude()) {
            return;
        }

        if (ne.getLongitude() < sw.getLongitude()) { //Across date-line?
            GeoPoint neEastBorder = new GeoPoint(ne.getLatitude(), AnimalExchangeApplication.EAST);
            GeoPoint swWestBorder = new GeoPoint(sw.getLatitude(), AnimalExchangeApplication.WEST);
            draw(canvas, mapView, neEastBorder, sw);
            draw(canvas, mapView, ne, swWestBorder);
        } else {
            draw(canvas, mapView, ne, sw);
        }
    }

}
