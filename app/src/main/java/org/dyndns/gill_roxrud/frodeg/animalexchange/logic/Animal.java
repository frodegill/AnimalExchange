package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;


public class Animal {

    private AnimalDefinition animalDefinition;


    public Animal(final AnimalDefinition animalDef) {
        this.animalDefinition = animalDef;
    }

    public int getLevel() {
        return animalDefinition.getLevel();
    }

}
