package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


public class SyncQueueEvent {

    public static final int IGNORE_V1 = 0;
    public static final double IGNORE_V2 = 0.0;


    public static final int RECEIVE_GIFT               = 10; //V1=animaltype
    public static final int RECEIVE_FOOD               = 11; //V2=amount

    public static final int FEED_ANIMAL                = 20; //V1=animaltype

    public static final int REQUEST_BUY_ANIMAL         = 50; //V1=animaltype, V2=max price
    public static final int CONFIRM_BUY_ANIMAL         = 51; //V1=animaltype, V2=price
    public static final int CANCEL_BUY_ANIMAL          = 52; //V1=animaltype
    public static final int CONFIRM_CANCEL_BUY_ANIMAL  = 53; //V1=animaltype
    public static final int REQUEST_SELL_ANIMAL        = 54; //V1=animalid,   V2=min price
    public static final int CONFIRM_SELL_ANIMAL        = 55; //V1=animalid,   V2=price
    public static final int CANCEL_SELL_ANIMAL         = 56; //V1=animaltype
    public static final int CONFIRM_CANCEL_SELL_ANIMAL = 57; //V1=animalid

    public static final int REQUEST_SELL_ANIMALGROUP   = 70; //V1=animalgrouptype
    public static final int CONFIRM_SELL_ANIMALGROUP   = 71; //V1=animalgrouptype, V2=price

    public static final int REQUEST_BUY_FOOD           = 90; //(v2=100.0)
    public static final int CONFIRM_BUY_FOOD           = 91;
    public static final int REQUEST_SELL_FOOD          = 92; //(v2=100.0)
    public static final int CONFIRM_SELL_FOOD          = 93;

    private long id;
    private int eventType;
    private int v1;
    private double v2;

    public SyncQueueEvent(final int eventType, final int v1, final double v2) {
        this.id = 0L;
        this.eventType = eventType;
        this.v1 = v1;
        this.v2 = v2;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        if (0L==this.id || -1L==this.id) {
            this.id = id;
        }
    }

    public int getEventType() {
        return eventType;
    }

    public int getValue1() {
        return v1;
    }

    public double getValue2() {
        return v2;
    }

    public double incrementValue2(final double value) {
        v2 += value;
        return v2;
    }

    public double decrementValue2(final double value) {
        v2 -= value;
        return v2;
    }

}
