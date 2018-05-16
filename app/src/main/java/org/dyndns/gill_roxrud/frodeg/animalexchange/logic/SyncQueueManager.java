package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;

import java.util.ArrayList;
import java.util.List;


public class SyncQueueManager {

    private static final int RECEIVE_GIFT               = 10; //V1=animaltype
    private static final int RECEIVE_FOOD               = 11; //V2=amount
    private static final int FEED_ANIMAL                = 20; //V1=animalid,   V2=amount
    private static final int REQUEST_BUY_ANIMAL         = 30; //V1=animaltype, V2=max price
    private static final int REQUEST_SELL_ANIMAL        = 31; //V1=animalid,   V2=min price
    private static final int REQUEST_SELL_ANIMALGROUP   = 32; //V1=animalgrouptype
    private static final int CANCEL_BUY_ANIMAL          = 40; //V1=animaltype
    private static final int CANCEL_SELL_ANIMAL         = 41; //V1=animaltype
    private static final int CANCEL_SELL_ANIMALGROUP    = 42; //V1=animalgrouptype
    private static final int CONFIRM_BUY_ANIMAL         = 50; //V1=animaltype, V2=price
    private static final int CONFIRM_SELL_ANIMAL        = 51; //V1=animalid,   V2=price
    private static final int CONFIRM_SELL_ANIMALGROUP   = 52; //V1=animalgrouptype, V2=price
    private static final int CONFIRM_CANCEL_BUY_ANIMAL  = 60; //V1=animaltype
    private static final int CONFIRM_CANCEL_SELL_ANIMAL = 61; //V1=animalid
    private static final int CONFIRM_CANCEL_SELL_ANIMALGROUP = 62; //V1=animalgrouptype


    public class Event {
        private long id;
        private int eventType;
        private int v1;
        private double v2;

        public Event(final int eventType, final int v1, final double v2) {
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
    }

    private List<Event> pendingEvents = new ArrayList<>();

    SyncQueueManager() {
        initialize();
    }

    private void initialize() {
    }

    public void sync() {
    }

    public void append(final int eventType, final int v1, final double v2) {
        Event previousEvent = pendingEvents.isEmpty() ? null : pendingEvents.get(pendingEvents.size()-1);

        if (RECEIVE_FOOD==eventType &&
            previousEvent!=null && RECEIVE_FOOD==previousEvent.eventType) {
            previousEvent.v2 += v2;
            if (!GameState.getInstance().getDB().updateEvent(previousEvent)) {
                previousEvent.v2 -= v2; //If storing failed, revert to be in sync
            }
        } else {
            Event newEvent = new Event(eventType, v1, v2);
            if (GameState.getInstance().getDB().addEvent(newEvent)) {
                pendingEvents.add(newEvent);
            }
        }
    }

}
