package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AnimalGroup {

    private final int level;
    private final int id;
    private final List<Integer> animalList = new ArrayList<>();

    public AnimalGroup(final int level,
                       final int id) {
        this.level = level;
        this.id = id;
    }

    public String getName(final Context ctx) {
        return ctx.getString(id);
    }

    public void addAnimal(final Integer animal) {
        animalList.add(animal);
    }
}
