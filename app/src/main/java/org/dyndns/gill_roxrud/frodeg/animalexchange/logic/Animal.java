package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;


public class Animal {

    private final int level;
    private final int id;
    private final int squareDrawableId;
    private final int roundedDrawableId;
    private final int animalGroupId;
    private final long distributionFrom;
    private final long distributionTo;
    private final int food;

    private Bitmap cachedRoundedBitmap = null;
    private int cachedRoundedBitmapSize = -1;
    private Bitmap cachedSquareBitmap = null;
    private int cachedSquareBitmapSize = -1;

    Animal(final int level,
           final int id,
           final int squareDrawableId,
           final int roundedDrawableId,
           final int animalGroupId,
           final long distributionFrom,
           final long distributionTo,
           final int food) {
        this.level = level;
        this.id = id;
        this.squareDrawableId = squareDrawableId;
        this.roundedDrawableId = roundedDrawableId;
        this.animalGroupId = animalGroupId;
        this.distributionFrom = distributionFrom;
        this.distributionTo = distributionTo;
        this.food = food;
    }

    public String getName(final Context ctx) {
        return ctx.getString(this.id);
    }

    public Bitmap getRoundedBitmap(final Context ctx, final int size) {
        if (cachedRoundedBitmapSize != size) {
            if ((cachedRoundedBitmap = AnimalManager.getBitmap(ctx, this.roundedDrawableId, size)) !=null) {
                cachedRoundedBitmapSize = size;
            }
        }
        return cachedRoundedBitmap;
    }

    public Bitmap getSquareBitmap(final Context ctx, final int size) {
        if (cachedSquareBitmapSize != size) {
            if ((cachedSquareBitmap = AnimalManager.getBitmap(ctx, this.squareDrawableId, size)) != null) {
                cachedSquareBitmapSize = size;
            }
        }
        return cachedSquareBitmap;
    }

    public int getLevel() {
        return level;
    }

    public boolean containsDistributionValue(final long distributionValue) {
        return (distributionValue>=distributionFrom && distributionValue<=distributionTo);
    }
}
