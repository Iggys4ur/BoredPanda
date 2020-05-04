package BoredPanda;

import BoredPanda.enums.Sex;
import BoredPanda.enums.Stat;

import java.util.*;

class Journal {

    final BoredPanda PANDA;
    public final String NAME;
    final Sex SEX = Math.round(Math.random()) > 0 ? Sex.MALE : Sex.FEMALE;

    //Immutable Attributes (EV STATS)
    final byte BREED = (byte) (new Random().nextInt(4)+1), // 5
                        QUALITY = (byte) (new Random().nextInt(6 - (5/(BREED))) + BREED), // 10
                        SIZE = (byte) ((new Random().nextInt((6 - (5/(BREED)) + BREED))/2) +1), // 10
                        LUCK = (byte) (new Random().nextInt( 11 - (10/QUALITY))), // 10
                        FERTILITY = (byte) (new Random().nextInt(11 - (QUALITY/2)) + (LUCK/10)), // 10
                        PHYSIQUE = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK) + (SIZE)),
                        AGILITY = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK) - (SIZE)),
                        INTELLECT = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK) - (BREED/2)),
                        CONSTITUTION = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK) - (BREED/2)),
                        MAGIC = (byte) (new Random().nextInt(QUALITY + LUCK));
    //xp counters
    private long expPhysique = 0L,
                    expAgility = 0L,
                    expIntellect = 0L,
                    expConstitution = 0L,
                    expMagic = 0L;
    //lvl counters
    private short lvlPhysique = 0,
                    lvlAgility = 0,
                    lvlIntellect = 0,
                    lvlConstitution = 0,
                    lvlMagic = 0,
                    lvlTotals = 0;
    //Total Level
    short totalLevel = 0;
    //Total Exp
    long totalExp = 0;

    Journal(BoredPanda panda){
        PANDA = panda;
        NAME = panda.NAME;
    }

    //TODO add coins per activity & relevant levels
    void addExpAndLevel(long exp, Stat stat){

        switch (stat){
            case PHYSIQUE:
                for (int i = 0; i < exp; i++) {
                    expPhysique += 1;
                    if(atLevelUp(expPhysique)){
                        lvlPhysique+= 1 + PHYSIQUE;
                        addTotalsAndLevelTribe();
                    }
                }                break;

            case AGILITY:
                for (int i = 0; i < exp; i++) {
                    expAgility += 1;
                    if(atLevelUp(expAgility)){
                        lvlAgility+= 1 + AGILITY;
                        addTotalsAndLevelTribe();
                    }
                }                break;

            case CONSTITUTION:
                for (int i = 0; i < exp; i++) {
                    expConstitution += 1;
                    if(atLevelUp(expConstitution)){
                        lvlConstitution+= 1 + CONSTITUTION;
                        addTotalsAndLevelTribe();
                    }
                }                break;

            case INTELLECT:
                for (int i = 0; i < exp; i++) {
                    expIntellect += 1;
                    if(atLevelUp(expIntellect)){
                        lvlIntellect+= 1 + INTELLECT;
                        addTotalsAndLevelTribe();
                    }
                }                break;

            case MAGIC:
                for (int i = 0; i < exp; i++) {
                    expMagic += 1;
                    if(atLevelUp(expMagic)){
                        lvlMagic+= 1 + MAGIC;
                        addTotalsAndLevelTribe();
                    }
                }                break;
        }
        totalExp += exp;
    }
    private void addTotalsAndLevelTribe()
    {
        if(++lvlTotals % 3 == 0)
        {
            totalLevel++;
            PANDA.Tribe.addExperience(1500);
        }
    }

    private boolean atLevelUp(long exp){
        return (0.04 * Math.sqrt(exp) % 1 == 0);
    }

    void print() {
        System.out.println("------------ [ JOURNAL ] ------------");
        printStats();
        PANDA.CLOCK.printTotals();
    }

    //primarily debug
    void printStats(){
        String role = "Member of the ";
        String parents = PANDA.hasParents() ? PANDA.MOTHER.NAME + " and " + PANDA.FATHER.NAME : "";
        String gender = SEX.toString();
        if(PANDA.hasParents()) gender = PANDA.getJournal().SEX == Sex.MALE ? "Son of " + parents : "Daughter of " + parents;
        if(PANDA.isElder) role = "Elder of the ";
        if(PANDA.isChief) role = "Chieftain of the ";
        System.out.println(NAME + " (" + gender + ") | Level " + totalLevel + " (" + lvlTotals + ") | Age: " + PANDA.CLOCK.age()
                + "\n" + role + PANDA.Tribe.NAME + " Lv(" + PANDA.Tribe.Level + "):[" + PANDA.Tribe.size() + "/" + PANDA.Tribe.maxSize + "] <" + PANDA.Tribe.Experience + "/" + PANDA.Tribe.nextLevelAt() +">");
        System.out.println("------------------------------------");
        System.out.println("[Breed] " + (int) BREED + " [Quality] " + (int) QUALITY + " [Size] " + (int) SIZE + " [Fertility] " + FERTILITY + " [Luck] " + (int) LUCK);
        System.out.println("EV STATS: [PHYSIQUE] " + (int) PHYSIQUE + " [AGILITY] " + (int) AGILITY + " [CONSTITUTION] " + (int) CONSTITUTION + " [MAGIC] " + (int) MAGIC + " [INTELLECT] " + (int) INTELLECT);
        System.out.println("LEVELS: [PHYSIQUE] " + (int) lvlPhysique + " [AGILITY] " + (int) lvlAgility + " [CONSTITUTION] " + (int) lvlConstitution + " [MAGIC] " + (int) lvlMagic + " [INTELLECT] " + (int) lvlIntellect);
        System.out.println("------------------------------------");
    }

    byte[] getEVStats(){
        return new byte[]{BREED, QUALITY, SIZE, FERTILITY, LUCK, PHYSIQUE, AGILITY, INTELLECT, CONSTITUTION, MAGIC};
    }

}
