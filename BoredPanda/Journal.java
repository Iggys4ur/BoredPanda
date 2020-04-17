package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Journal {

    private final BoredPanda PANDA;
    protected final String NAME;
    protected final enumeratedPandas.Sex SEX;

    //Immutable Attributes
    protected final byte BREED = (byte) (new Random().nextInt(4)+1), // 5
                    QUALITY = (byte) (new Random().nextInt(6 - (5/(BREED))) + BREED), // 10
                    SIZE = (byte) ((new Random().nextInt((6 - (5/(BREED)) + BREED))/2) +1), // 10
                    LUCK = (byte) (new Random().nextInt( 11 - (10/QUALITY))), // 10
                    FERTILITY = (byte) (new Random().nextInt(11 - (QUALITY/2)) + (LUCK/10)); // 10
    //stats
    protected byte Physique = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK * 2) + (SIZE/2)),
                Agility = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK * 2) - (SIZE/2)),
                Intellect = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK * 2) - (BREED/2)),
                Charisma = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1 + (LUCK * 2) - (BREED/2)),
                Magic = (byte) (new Random().nextInt(QUALITY + LUCK));

    //xp counters
    protected long expPhysique = 0,
                    expAgility = 0,
                    expIntellect = 0,
                    expCharisma = 0,
                    expMagic = 0;
    //actions
    protected Action[] journal = new Action[11];
    protected List<Action> activityHistory = new ArrayList<Action>();

    //EXP + Level
    protected short totalLevel = 0;

    Journal(BoredPanda panda){
        PANDA = panda;
        NAME = panda.NAME;
        if(Math.round(Math.random()) > 0) SEX = enumeratedPandas.Sex.MALE;
        else SEX = enumeratedPandas.Sex.FEMALE;
        initLogs();
    }

    protected void initLogs()
    {
        PANDA.previousAction = new Action(PANDA).setFields("INITIALIZATION ACTIVITY", 0);
        activityHistory.add(PANDA.previousAction);
        for (int i = 0; i < journal.length; i++) {
            journal[i] = new Action(PANDA, i).setDuration(0);
        }
        PANDA.currentAction = PANDA.previousAction.choose(null);
    }

    protected void compute()
    {
        for (Action a: activityHistory)
        {
            journal[a.choice].duration += a.duration;

            switch (a.activity) {
                case SLEEP:
                    break;

                case EAT_BAMBOO:
                    break;

                case CLIMB_TREES:
                    break;

                case SWIM:
                    break;

                case PLAY_WITH_ROCKS:
                    break;

                case FIGHT_BEES_FOR_HONEY:
                    break;

                case DROOL_ON_THINGS:
                    break;

                case TERRORIZE_VILLAGERS:
                    break;

                case GROWL_AT_BIRDS:
                    break;

                case SLASH_AT_TREES:
                    break;

                case ABDUCT_AND_EAT_A_VILLAGER:
                    break;
            }
        }
    }

    public void print() {
        //System.out.println("------------ [ JOURNAL ] ------------");
        printStats();
        PANDA.Clock.printTotals();
    }
    public void printStats(){
        System.out.println(NAME + " (" + SEX.toString().charAt(0) + ") | Level " + totalLevel + " " + " | Age: " + PANDA.Clock.age() + "\n" + PANDA.Tribe.NAME + " <" + PANDA.Tribe.Level + ">");
        System.out.println("------------------------------------");
        System.out.println("[Breed] " + (int) BREED + " [Quality] " + (int) QUALITY + " [Size] " + (int) SIZE + " [Fertility] " + FERTILITY + " [Luck] " + (int) LUCK);
        System.out.println("[Physique] " + (int) Physique + " [Agility] " + (int) Agility + " [Charisma] " + (int) Charisma + " [Magic] " + (int) Magic + " [Intellect] " + (int) Intellect);
        System.out.println("____________________________________\n");
    }

    public byte[] getStats(){
        return new byte[]{BREED, QUALITY, SIZE, FERTILITY, LUCK, Physique, Agility, Intellect, Charisma, Magic};
    }

    public void setPhysique(byte physique) {
        Physique = physique;
    }

    public void setAgility(byte agility) {
        Agility = agility;
    }

    public void setIntellect(byte intellect) {
        Intellect = intellect;
    }

    public void setCharisma(byte charisma) {
        Charisma = charisma;
    }

    public void setMagic(byte magic) {
        Magic = magic;
    }

    public Action[] getJournal() {
        return journal;
    }

    protected List<Action> getActivityHistory() {
        return activityHistory;
    }

}
