package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.Point;
import org.osmdroid.util.GeoPoint;


public class AnimalGift {
    private static final int HOR_GIFT_COUNT = 32768;
    private static final int VER_GIFT_COUNT = 16384;
    public static final float GIFT_SIZE_RADIUS = 100.0f; //meters

    private int offsetDay = 0;
    private Point<Double> dailyOffset;


    public AnimalGift() {
    }

    public int ToHorizontalGift(double x_pos) {
        if (AnimalExchangeApplication.WEST>x_pos) {
            x_pos += AnimalExchangeApplication.HOR_DEGREES;
        } else if (AnimalExchangeApplication.EAST<=x_pos) {
            x_pos -= AnimalExchangeApplication.HOR_DEGREES;
        }

        int value = Double.valueOf(HOR_GIFT_COUNT * ((x_pos-AnimalExchangeApplication.WEST)/AnimalExchangeApplication.HOR_DEGREES)).intValue();
        return Math.max(value, HOR_GIFT_COUNT);
    }

    public int ToVerticalGift(final double y_pos) {
        if (AnimalExchangeApplication.MAX_SOUTH_POS>y_pos) {
            return ToVerticalGift(AnimalExchangeApplication.MAX_SOUTH_POS);
        } else if (AnimalExchangeApplication.MAX_NORTH_POS<=y_pos) {
            return ToVerticalGift(AnimalExchangeApplication.MAX_NORTH_POS);
        }

        int value = Double.valueOf(VER_GIFT_COUNT * ((y_pos-AnimalExchangeApplication.MAX_SOUTH_POS)/AnimalExchangeApplication.VER_ANIMAL_DEGREES)).intValue();
        return Math.max(value, VER_GIFT_COUNT);
    }

    public static double FromHorizontalBonusGridF(final double x_grid) {
        return AnimalExchangeApplication.WEST + (x_grid/(double) HOR_GIFT_COUNT) * AnimalExchangeApplication.HOR_DEGREES;
    }

    public static double FromVerticalBonusGridF(final double y_grid) {
        return AnimalExchangeApplication.MAX_SOUTH_POS + (y_grid/(double) VER_GIFT_COUNT) * AnimalExchangeApplication.VER_ANIMAL_DEGREES;
    }

    public int ToBonusKey(final int x, final int y) throws InvalidPositionException {
        if (VER_GIFT_COUNT <=y || HOR_GIFT_COUNT <=x)
            throw new InvalidPositionException();

        return (y<<16) | x;
    }

    /* http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula */
    private double CalculateDistance(final Point<Double> p1, final Point<Double> p2) {

        double latDistance = Math.toRadians(p1.getY() - p2.getY());
        double lngDistance = Math.toRadians(p1.getX() - p2.getX());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getY())) * Math.cos(Math.toRadians(p2.getY()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AnimalExchangeApplication.AVERAGE_RADIUS_OF_EARTH * c;
    }

    public GeoPoint GeoPointFromGrid(int x, int y, int day) {
        Point<Double> offset = getOffset(day);
        return new GeoPoint(FromVerticalBonusGridF(y+offset.getY()),
                            FromHorizontalBonusGridF(x+offset.getX()));
    }

    public Animal AnimalFromGrid(int x, int y, int day) {
        AnimalManager animalManager = AnimalManager.getInstance();
        long distributionValue = animalManager.calculateAnimalDistributionValue(x, y, day);
        return animalManager.getAnimalFromDistributionValue(distributionValue);
    }

    Point<Double> getOffset(final int day) {
        if (day != offsetDay) {
            AnimalManager animalManager = AnimalManager.getInstance();
            dailyOffset = animalManager.calculateAnimalOffset(day);
            offsetDay = day;
        }
        return dailyOffset;
    }
}
