package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeDBHelper;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.Point;
import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.Set;


public class AnimalGiftManager {
    private static final int HOR_GIFT_COUNT = 32768;
    private static final int VER_GIFT_COUNT = 16384;
    public static final float GIFT_SIZE_RADIUS = 75.0f; //meters
    static final double HALF_HOR_GIFT_DEGREE = (AnimalExchangeApplication.HOR_DEGREES/HOR_GIFT_COUNT)/2; //Used for rounding
    static final double HALF_VER_GIFT_DEGREE = (AnimalExchangeApplication.VER_DEGREES/VER_GIFT_COUNT)/2; //Used for rounding

    private int offsetDay = 0;
    private Point<Double> dailyOffset;

    private int lastPurgeDay = 0;

    private Set<Integer> giftsAwarded = null;
    private int awardDay = 0;


    public AnimalGiftManager() {
    }

    public Animal requestAnimalGift(final Point<Double> pos, final int day) throws InvalidPositionException {
        double horizontal_pos_rounded = pos.getX() + HALF_HOR_GIFT_DEGREE;
        if (AnimalExchangeApplication.EAST<=horizontal_pos_rounded) {
            horizontal_pos_rounded -= AnimalExchangeApplication.HOR_DEGREES;
        }

        double vertical_pos_rounded = pos.getY() + HALF_VER_GIFT_DEGREE;

        Point<Integer> p = new Point<>(ToHorizontalGift(horizontal_pos_rounded),
                                       ToVerticalGift(vertical_pos_rounded));

        Point<Double> offset = getOffset(day); //-0.5 to 0.5
        Point<Double> nearest_gift_pos = new Point<>(FromHorizontalGift(p.getX() + offset.getX()),
                                                     FromVerticalGift(p.getY() + offset.getY()));

        if (GIFT_SIZE_RADIUS >= CalculateDistance(pos, nearest_gift_pos)) {
            if (giftsAwarded==null || awardDay!=day) {
                giftsAwarded = new HashSet<>();
                awardDay = day;
            }
            giftsAwarded.add(ToAnimalGiftKey(p.getX(), p.getY()));

            long distributionValue = AnimalManager.calculateAnimalDistributionValue(p.getX(), p.getY(), day);
            return GameState.getInstance().getAnimalManager().getAnimalFromDistributionValue(distributionValue);
        }

        return null;
    }

    public boolean isAwardedT(final int key, final int day) {
        try {
            if (giftsAwarded!=null && awardDay==day) {
                return giftsAwarded.contains(key);
            }

            AnimalExchangeDBHelper db = GameState.getInstance().getDB();
            if (lastPurgeDay < day) {
                db.purgeOldAnimalGiftsT(day);
                lastPurgeDay = day;
            }

            if (giftsAwarded==null || awardDay!=day) {
                giftsAwarded = db.fetchAwardedGifts(day);
            }
            return (giftsAwarded==null || giftsAwarded.contains(key));
        }
        catch(Exception e){
            return true;
        }
    }

    public int ToHorizontalGift(double x_pos) {
        if (AnimalExchangeApplication.WEST>x_pos) {
            x_pos += AnimalExchangeApplication.HOR_DEGREES;
        } else if (AnimalExchangeApplication.EAST<=x_pos) {
            x_pos -= AnimalExchangeApplication.HOR_DEGREES;
        }

        int value = Double.valueOf(HOR_GIFT_COUNT * ((x_pos-AnimalExchangeApplication.WEST)/AnimalExchangeApplication.HOR_DEGREES)).intValue();
        return Math.min(value, HOR_GIFT_COUNT);
    }

    public int ToVerticalGift(final double y_pos) {
        if (AnimalExchangeApplication.MAX_SOUTH_POS>y_pos) {
            return ToVerticalGift(AnimalExchangeApplication.MAX_SOUTH_POS);
        } else if (AnimalExchangeApplication.MAX_NORTH_POS<=y_pos) {
            return ToVerticalGift(AnimalExchangeApplication.MAX_NORTH_POS);
        }

        int value = Double.valueOf(VER_GIFT_COUNT * ((y_pos-AnimalExchangeApplication.MAX_SOUTH_POS)/AnimalExchangeApplication.VER_ANIMAL_DEGREES)).intValue();
        return Math.min(value, VER_GIFT_COUNT);
    }

    public static double FromHorizontalGift(final double x_grid) {
        return AnimalExchangeApplication.WEST + (x_grid/(double) HOR_GIFT_COUNT) * AnimalExchangeApplication.HOR_DEGREES;
    }

    public static double FromVerticalGift(final double y_grid) {
        return AnimalExchangeApplication.MAX_SOUTH_POS + (y_grid/(double) VER_GIFT_COUNT) * AnimalExchangeApplication.VER_ANIMAL_DEGREES;
    }

    public int ToAnimalGiftKey(final int x, final int y) throws InvalidPositionException {
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

    public GeoPoint GeoPointFromGrid(final int x, final int y, final int day) {
        Point<Double> offset = getOffset(day);
        return new GeoPoint(FromVerticalGift(y+offset.getY()), FromHorizontalGift(x+offset.getX()));
    }

    Point<Double> getOffset(final int day) {
        if (day != offsetDay) {
            AnimalManager animalManager = GameState.getInstance().getAnimalManager();
            dailyOffset = animalManager.calculateAnimalOffset(day);
            offsetDay = day;
        }
        return dailyOffset;
    }
}
