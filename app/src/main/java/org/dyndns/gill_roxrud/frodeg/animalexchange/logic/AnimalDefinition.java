package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import android.content.Context;
import android.graphics.Bitmap;


public class AnimalDefinition {

    private final int level;
    private final int stringResourceId;
    private final int squareDrawableId;
    private final int roundedDrawableId;
    private final int groupStringResourceId;
    private final long distributionFrom;
    private final long distributionTo;
    private final int foodRequired;

    private Bitmap cachedRoundedBitmap = null;
    private int cachedRoundedBitmapSize = -1;
    private Bitmap cachedSquareBitmap = null;
    private int cachedSquareBitmapSize = -1;

    AnimalDefinition(final int level,
                     final int stringResourceId,
                     final int squareDrawableId,
                     final int roundedDrawableId,
                     final int groupStringResourceId,
                     final long distributionFrom,
                     final long distributionTo,
                     final int foodRequired) {
        this.level = level;
        this.stringResourceId = stringResourceId;
        this.squareDrawableId = squareDrawableId;
        this.roundedDrawableId = roundedDrawableId;
        this.groupStringResourceId = groupStringResourceId;
        this.distributionFrom = distributionFrom;
        this.distributionTo = distributionTo;
        this.foodRequired = foodRequired;
    }

    public String getName(final Context ctx) {
        return ctx.getString(this.stringResourceId);
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

    public int getFoodRequired() {
        return foodRequired;
    }

    public boolean containsDistributionValue(final long distributionValue) {
        return (distributionValue>=distributionFrom && distributionValue<=distributionTo);
    }
}
