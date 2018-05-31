package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AnimalGroup {

    private final int level;
    private final int stringResourceId;
    private final List<AnimalDefinition> animalDefinitionList = new ArrayList<>();

    public AnimalGroup(final int level,
                       final int stringResourceId) {
        this.level = level;
        this.stringResourceId = stringResourceId;
    }

    public int getLevel() {
        return level;
    }
    
    public String getName(final Context ctx) {
        return ctx.getString(stringResourceId);
    }

    public void addAnimalDefinition(final AnimalDefinition animalDefinition) {
        animalDefinitionList.add(animalDefinition);
    }

    public List<AnimalDefinition> getAnimalDefinitionList() {
        return animalDefinitionList;
    }

}
