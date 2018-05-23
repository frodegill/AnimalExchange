package org.dyndns.gill_roxrud.frodeg.animalexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueEvent;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class AnimalExchangeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME         = "AnimalExchange.db";

    private static final String ANIMAL_TABLE_NAME     = "animal";
    private static final String ANIMAL_COLUMN_ID      = "id";
    private static final String ANIMAL_COLUMN_TYPE    = "type";
    private static final String ANIMAL_COLUMN_FOOD    = "food"; //Food remaining until full
    private static final String ANIMAL_COLUMN_PRICE   = "price"; //Sell price. NULL if Not For Sale

    private static final String ANIMALGIFT_TABLE_NAME = "animalgift";
    private static final String ANIMALGIFT_COLUMN_KEY = "key";
    private static final String ANIMALGIFT_COLUMN_DAY = "day";

    private static final String SYNCQUEUE_TABLE_NAME  = "syncqueue";
    private static final String SYNCQUEUE_COLUMN_ID   = "id";
    private static final String SYNCQUEUE_COLUMN_TYPE = "type";
    private static final String SYNCQUEUE_COLUMN_VALUE1 = "value1";
    private static final String SYNCQUEUE_COLUMN_VALUE2 = "value2";

    private static final String PROPERTY_TABLE_NAME   = "properties";
    private static final String PROPERTY_COLUMN_KEY   = "key";
    private static final String PROPERTY_COLUMN_VALUE = "value";

    public static final String PROPERTY_X_POS         = "x_pos";
    public static final String PROPERTY_Y_POS         = "y_pos";
    public static final String PROPERTY_ZOOM_LEVEL    = "zoom_level";
    public static final String PROPERTY_FOOD          = "food";


    public AnimalExchangeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        boolean successful = true;

        db.beginTransaction();
        try {
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)",
                                     PROPERTY_TABLE_NAME, PROPERTY_COLUMN_KEY, PROPERTY_COLUMN_VALUE));

            ContentValues contentValues = new ContentValues();
            contentValues.put(PROPERTY_COLUMN_KEY, PROPERTY_X_POS);
            contentValues.put(PROPERTY_COLUMN_VALUE, 0);
            successful &= (-1 != db.insert(PROPERTY_TABLE_NAME, null, contentValues));

            contentValues = new ContentValues();
            contentValues.put(PROPERTY_COLUMN_KEY, PROPERTY_Y_POS);
            contentValues.put(PROPERTY_COLUMN_VALUE, 0);
            successful &= (-1 != db.insert(PROPERTY_TABLE_NAME, null, contentValues));

            contentValues = new ContentValues();
            contentValues.put(PROPERTY_COLUMN_KEY, PROPERTY_ZOOM_LEVEL);
            contentValues.put(PROPERTY_COLUMN_VALUE, 11);
            successful &= (-1 != db.insert(PROPERTY_TABLE_NAME, null, contentValues));

            contentValues = new ContentValues();
            contentValues.put(PROPERTY_COLUMN_KEY, PROPERTY_FOOD);
            contentValues.put(PROPERTY_COLUMN_VALUE, 0.0);
            successful &= (-1 != db.insert(PROPERTY_TABLE_NAME, null, contentValues));

            db.execSQL("CREATE TABLE "+ANIMALGIFT_TABLE_NAME+"("+ANIMALGIFT_COLUMN_KEY+" INTEGER NOT NULL, "
                                                                +ANIMALGIFT_COLUMN_DAY+" INTEGER NOT NULL, "
                      +"PRIMARY KEY ("+ANIMALGIFT_COLUMN_KEY+","+ANIMALGIFT_COLUMN_DAY+"))");

            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                                                      "%s INTEGER NOT NULL, "+
                                                      "%s REAL NOT NULL, "+
                                                      "%s INTEGER NULL)",
                    ANIMAL_TABLE_NAME,
                    ANIMAL_COLUMN_ID, ANIMAL_COLUMN_TYPE, ANIMAL_COLUMN_FOOD, ANIMAL_COLUMN_PRICE));

            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                                                      "%s INTEGER NOT NULL, "+
                                                      "%s INTEGER NOT NULL, "+
                                                      "%s REAL NOT NULL)",
                    SYNCQUEUE_TABLE_NAME,
                    SYNCQUEUE_COLUMN_ID, SYNCQUEUE_COLUMN_TYPE, SYNCQUEUE_COLUMN_VALUE1, SYNCQUEUE_COLUMN_VALUE2));

        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        EndTransaction(db, successful);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        boolean successful = true;

        if (oldVersion < DATABASE_VERSION) {
            db.beginTransaction();
        }

        try {
            if (oldVersion < 2) {
                db.execSQL("CREATE TABLE "+ANIMALGIFT_TABLE_NAME+"("+ANIMALGIFT_COLUMN_KEY+" INTEGER NOT NULL, "
                                                                    +ANIMALGIFT_COLUMN_DAY+" INTEGER NOT NULL, "
                          +"PRIMARY KEY ("+ANIMALGIFT_COLUMN_KEY+","+ANIMALGIFT_COLUMN_DAY+"))");
            }

            if (oldVersion < 3) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PROPERTY_COLUMN_KEY, PROPERTY_FOOD);
                contentValues.put(PROPERTY_COLUMN_VALUE, 0.0);
                successful &= (-1 != db.insert(PROPERTY_TABLE_NAME, null, contentValues));
            }

            if (oldVersion < 4) {
                db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                                                          "%s INTEGER NOT NULL, "+
                                                          "%s REAL NOT NULL, "+
                                                          "%s INTEGER NULL)",
                        ANIMAL_TABLE_NAME,
                        ANIMAL_COLUMN_ID, ANIMAL_COLUMN_TYPE, ANIMAL_COLUMN_FOOD, ANIMAL_COLUMN_PRICE));
            }

            if (oldVersion < 5) {
                db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                                                          "%s INTEGER NOT NULL, "+
                                                          "%s INTEGER NOT NULL, "+
                                                          "%s REAL NOT NULL)",
                        SYNCQUEUE_TABLE_NAME,
                        SYNCQUEUE_COLUMN_ID, SYNCQUEUE_COLUMN_TYPE, SYNCQUEUE_COLUMN_VALUE1, SYNCQUEUE_COLUMN_VALUE2));
            }
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (oldVersion < DATABASE_VERSION) {
            EndTransaction(db, successful);
        }
    }

    public SQLiteDatabase StartTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        return db;
    }

    public void EndTransaction(SQLiteDatabase db, boolean successful) {
        if (successful) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private boolean PersistFoodT(final double food) {
        boolean successful = true;
        SQLiteDatabase dbInTransaction = StartTransaction();
        try {
            dbInTransaction.execSQL("UPDATE "+PROPERTY_TABLE_NAME
                                   +" SET "+PROPERTY_COLUMN_VALUE+" = "+PROPERTY_COLUMN_VALUE+"+?"
                                   +" WHERE "+PROPERTY_COLUMN_KEY+"=?",
                    new String[] {Double.toString(food), PROPERTY_FOOD});
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        EndTransaction(dbInTransaction, successful);

        return successful;
    }

    public boolean PersistAnimal(final SQLiteDatabase dbInTransaction, final int giftKey, final int day) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANIMALGIFT_COLUMN_KEY, giftKey);
        contentValues.put(ANIMALGIFT_COLUMN_DAY, day);
        return (-1 != dbInTransaction.insert(ANIMALGIFT_TABLE_NAME, null, contentValues));
    }

    private void purgeOldAnimalGifts(final SQLiteDatabase dbInTransaction, final int day) throws SQLException {
        dbInTransaction.execSQL("DELETE FROM "+ANIMALGIFT_TABLE_NAME
                               +" WHERE " + ANIMALGIFT_COLUMN_DAY + "<"+Integer.toString(day));
    }

    public void purgeOldAnimalGiftsT(final int day) throws SQLException {
        boolean successful = true;
        SQLiteDatabase dbInTransaction = StartTransaction();

        try {
            purgeOldAnimalGifts(dbInTransaction, day);
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        EndTransaction(dbInTransaction, successful);
    }

    public Set<Integer> fetchAwardedGifts(final int day) throws SQLException {
        HashSet<Integer> result = new HashSet<>();

        Cursor cursor = null;
        try {
            cursor = this.getReadableDatabase()
                    .rawQuery("SELECT " + ANIMALGIFT_COLUMN_KEY
                                    + " FROM " + ANIMALGIFT_TABLE_NAME
                                    + " WHERE " + ANIMALGIFT_COLUMN_DAY + "=?",
                            new String[]{Integer.toString(day)});
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(cursor.getInt(0));
                    cursor.moveToNext();
                }
            }
            return result;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean addEvent(final SQLiteDatabase dbInTransaction, SyncQueueEvent event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNCQUEUE_COLUMN_TYPE, event.getEventType());
        contentValues.put(SYNCQUEUE_COLUMN_VALUE1, event.getValue1());
        contentValues.put(SYNCQUEUE_COLUMN_VALUE2, event.getValue2());
        event.setId(dbInTransaction.insert(SYNCQUEUE_TABLE_NAME, null, contentValues));
        return -1L != event.getId();
    }

    public void updateEvent(final SQLiteDatabase dbInTransaction, SyncQueueEvent event) {
        dbInTransaction.execSQL("UPDATE "+SYNCQUEUE_TABLE_NAME
                               +" SET "+SYNCQUEUE_COLUMN_VALUE1+" = ?,"
                                       +SYNCQUEUE_COLUMN_VALUE2+" = ?"
                               +" WHERE "+SYNCQUEUE_COLUMN_ID+"=?",
                new String[] {Integer.toString(event.getValue1()),
                              Double.toString(event.getValue2()),
                              Long.toString(event.getId())});
    }

    public List<SyncQueueEvent> getSyncQueue() {
        List<SyncQueueEvent> result = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = this.getReadableDatabase()
                    .rawQuery("SELECT " + SYNCQUEUE_COLUMN_ID
                              +", " + SYNCQUEUE_COLUMN_TYPE
                              +", " + SYNCQUEUE_COLUMN_VALUE1
                              +", " + SYNCQUEUE_COLUMN_VALUE2
                              +" FROM " + SYNCQUEUE_TABLE_NAME
                              +" ORDER BY " + SYNCQUEUE_COLUMN_ID + " ASC",
                            null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    SyncQueueEvent event = new SyncQueueEvent(cursor.getInt(1),
                                                              cursor.getInt(2),
                                                              cursor.getDouble(3));
                    event.setId(cursor.getInt(0));
                    result.add(event);
                    cursor.moveToNext();
                }
            }
            return result;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int GetIntProperty(final String property) {
        Cursor cursor = null;
        try {
            cursor = this.getReadableDatabase()
                    .rawQuery(String.format("SELECT %s FROM %s WHERE %s=?",
                            PROPERTY_COLUMN_VALUE, PROPERTY_TABLE_NAME, PROPERTY_COLUMN_KEY),
                            new String[]{property});
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.isAfterLast() ? 0 : cursor.getInt(0);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public double GetDoubleProperty(final String property) {
        Cursor cursor = null;
        try {
            cursor = this.getReadableDatabase()
                    .rawQuery(String.format("SELECT %s FROM %s WHERE %s=?",
                            PROPERTY_COLUMN_VALUE, PROPERTY_TABLE_NAME, PROPERTY_COLUMN_KEY),
                            new String[]{property});
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.isAfterLast() ? 0 : cursor.getDouble(0);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String GetStringProperty(final String property) {
        Cursor cursor = null;
        try {
            cursor = this.getReadableDatabase()
                    .rawQuery(String.format("SELECT %s FROM %s WHERE %s=?",
                                            PROPERTY_COLUMN_VALUE, PROPERTY_TABLE_NAME, PROPERTY_COLUMN_KEY),
                              new String[]{property});
            if (!cursor.moveToFirst()) {
                return "";
            }
            return cursor.isAfterLast() ? "" : cursor.getString(0);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void SetIntProperty(final SQLiteDatabase dbInTransaction, final String property, final int value) throws SQLException {
        dbInTransaction.execSQL(String.format("UPDATE %s SET %s=? WHERE %s=?",
                PROPERTY_TABLE_NAME, PROPERTY_COLUMN_VALUE, PROPERTY_COLUMN_KEY),
                new String[] {Integer.toString(value), property});
    }

    public void SetDoubleProperty(final SQLiteDatabase dbInTransaction, final String property, final double value) throws SQLException {
        dbInTransaction.execSQL(String.format("UPDATE %s SET %s=? WHERE %s=?",
                PROPERTY_TABLE_NAME, PROPERTY_COLUMN_VALUE, PROPERTY_COLUMN_KEY),
                new String[] {Double.toString(value), property});
    }

    public void SetPropertyT(final String property, final int value) {
        boolean successful = true;
        SQLiteDatabase dbInTransaction = StartTransaction();

        try {
            SetIntProperty(dbInTransaction, property, value);
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        EndTransaction(dbInTransaction, successful);
    }

    private void SetStringProperty(final SQLiteDatabase dbInTransaction, final String property, final String value) throws SQLException {
        dbInTransaction.execSQL(String.format("UPDATE %s SET %s=? WHERE %s=?",
                                              PROPERTY_TABLE_NAME, PROPERTY_COLUMN_VALUE, PROPERTY_COLUMN_KEY),
                                new String[] {value, property});
    }

    void SetStringPropertyT(final String property, final String value) {
        boolean successful = true;
        SQLiteDatabase dbInTransaction = StartTransaction();

        try {
            SetStringProperty(dbInTransaction, property, value);
        } catch (SQLException e) {
            successful = false;
            Toast.makeText(AnimalExchangeApplication.getContext(), "ERR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        EndTransaction(dbInTransaction, successful);
    }

}
