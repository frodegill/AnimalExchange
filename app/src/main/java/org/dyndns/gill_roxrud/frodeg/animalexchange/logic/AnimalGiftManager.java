package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeDBHelper;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.Point;
import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.Set;


public class AnimalGiftManager {
    private static final int HOR_GIFT_COUNT = 32768;
    private static final int VER_GIFT_COUNT = 16384;
    public static final float GIFT_SIZE_RADIUS = 75.0f; //meters
    public static final double HOR_GIFT_DEGREE = AnimalExchangeApplication.HOR_DEGREES/HOR_GIFT_COUNT;
    public static final double VER_GIFT_DEGREE = AnimalExchangeApplication.VER_DEGREES/VER_GIFT_COUNT;
    public static final double HALF_HOR_GIFT_DEGREE = HOR_GIFT_DEGREE/2.0;
    public static final double HALF_VER_GIFT_DEGREE = VER_GIFT_DEGREE/2.0;

    private int offsetDay = 0;
    private Point<Double> dailyOffset;

    private int lastPurgeDay = 0;

    private Set<Integer> giftsAwarded = null;
    private int awardDay = 0;


    public AnimalGiftManager() {
    }

    public void requestAnimalGiftT(final Point<Double> pos, final int day) throws InvalidPositionException {
        Point<Double> offset = getOffset(day); //-0.5 to 0.5
        double horizontal_pos_rounded = pos.getX() + HALF_HOR_GIFT_DEGREE - HOR_GIFT_DEGREE*offset.getX();
        if (AnimalExchangeApplication.EAST<=horizontal_pos_rounded) {
            horizontal_pos_rounded -= AnimalExchangeApplication.HOR_DEGREES;
        }

        double vertical_pos_rounded = pos.getY() + HALF_VER_GIFT_DEGREE - VER_GIFT_DEGREE*offset.getY();

        Point<Integer> p = new Point<>(ToHorizontalGift(horizontal_pos_rounded),
                                       ToVerticalGift(vertical_pos_rounded));

        Point<Double> nearest_gift_pos = new Point<>(FromHorizontalGift(p.getX() + offset.getX()),
                                                     FromVerticalGift(p.getY() + offset.getY()));

        if (GIFT_SIZE_RADIUS >= AnimalManager.CalculateDistance(pos, nearest_gift_pos)) {
            GameState gameState = GameState.getInstance();
            AnimalExchangeDBHelper db = gameState.getDB();

            if (giftsAwarded == null || awardDay != day) {
                giftsAwarded = new HashSet<>();
                awardDay = day;
            }

            int animalGiftKey = ToAnimalGiftKey(p.getX(), p.getY());

            AnimalDefinition animalDefinition = null;
            boolean successful;
            SQLiteDatabase dbInTransaction = db.StartTransaction();
            try {
                successful = db.PersistAnimalGift(dbInTransaction, animalGiftKey, day);

                if (successful) {
                    long distributionValue = AnimalManager.calculateAnimalDistributionValue(p.getX(), p.getY(), day);
                    animalDefinition = gameState.getAnimalManager().getAnimalFromDistributionValue(distributionValue);
                    successful = gameState.getSyncQueueManager().append(dbInTransaction,
                                                                        SyncQueueEvent.RECEIVE_GIFT,
                                                                        animalDefinition.getLevel(),
                                                                        SyncQueueEvent.IGNORE_V2);

                    Context context = AnimalExchangeApplication.getContext();
                    Toast.makeText(context, "You got a " + animalDefinition.getName(context), Toast.LENGTH_LONG).show(); //TODO: i18n
                }

                if (successful) {
                    giftsAwarded.add(animalGiftKey);
                }
            } catch (SQLException e) {
                successful = false;
                Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            db.EndTransaction(dbInTransaction, successful);
        }
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
        } else if (AnimalExchangeApplication.MAX_NORTH_POS<y_pos) {
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

    public GeoPoint GeoPointFromGrid(final int x, final int y, final int day) {
        Point<Double> offset = getOffset(day);
        return new GeoPoint(FromVerticalGift(y+offset.getY()), FromHorizontalGift(x+offset.getX()));
    }

    public Point<Double> getOffset(final int day) {
        if (day != offsetDay) {
            AnimalManager animalManager = GameState.getInstance().getAnimalManager();
            dailyOffset = animalManager.calculateAnimalOffset(day);
            offsetDay = day;
        }
        return dailyOffset;
    }
}
