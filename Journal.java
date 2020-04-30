package BoredPanda;

import BoredPanda.enums.Activity;
import BoredPanda.enums.Sex;
import BoredPanda.enums.Stat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Journal {

    private final BoredPanda PANDA;
    protected final String NAME;
    protected final Sex SEX;

    //Immutable Attributes (EV STATS)
    protected final byte BREED = (byte) (new Random().nextInt(4)+1), // 5
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
    protected long expPhysique = 0L,
                    expAgility = 0L,
                    expIntellect = 0L,
                    expConstitution = 0L,
                    expMagic = 0L,
                    sleepBonus = 0L;
    //lvl counters
    protected short lvlPhysique = 0,
                    lvlAgility = 0,
                    lvlIntellect = 0,
                    lvlConstitution = 0,
                    lvlMagic = 0,
                    lvlTotals = 0;
    //actions
    protected final Action[] JOURNAL;
    protected final ConcurrentHashMap<Activity, Action> ACTIVITY_HISTORY;

    //Total Level
    protected short totalLevel = 0;
    //Total Exp
    protected long totalExp = 0;

    Journal(BoredPanda panda){
        PANDA = panda;
        NAME = panda.NAME;
        JOURNAL = new Action[11];
        ACTIVITY_HISTORY = new ConcurrentHashMap<Activity, Action>();

        if(Math.round(Math.random()) > 0) SEX = Sex.MALE;
        else SEX = Sex.FEMALE;
        initLogs();
    }

    protected void initLogs()
    {
        PANDA.previousAction = new Action(PANDA).setFields(Activity.INITIALIZATION_ACTIVITY, 0);
        for (int i = 0; i < JOURNAL.length; i++) {
            JOURNAL[i] = new Action(PANDA, i).setDuration(0);
        }
        PANDA.currentAction = PANDA.previousAction.choose(null);
    }

    //TODO add coins per activity & relevant levels
    protected void compute()
    {
        sleepBonus = 0;
        ArrayList<Action> history = new ArrayList<Action>(ACTIVITY_HISTORY.values());
        for (Action a : history)
        {
            double exp = a.duration + sleepBonus;
            this.JOURNAL[a.choice].duration = (long) (exp < 0 ? 0 : exp);

            switch (a.activity) {
                case SLEEP:
                    //add sleep time to bonus xp of next activity; stack iff panda's next activity is sleep
                    sleepBonus += exp;
                    break;

                case EAT_BAMBOO:
                    //constitution
                    addExpAndLevel((long) exp, Stat.CONSTITUTION);
                    break;

                case CLIMB_TREES:
                    //1/2 agil + 1/2 physique
                    addExpAndLevel((long) exp/2, Stat.AGILITY);
                    addExpAndLevel((long)  exp/2, Stat.PHYSIQUE);
                    break;

                case SWIM:
                    // 1/2 constitution | 1/4 physique | 1/4 agility
                    addExpAndLevel((long) exp/2, Stat.CONSTITUTION);
                    addExpAndLevel((long) exp/4, Stat.AGILITY);
                    addExpAndLevel( (long) exp/4, Stat.PHYSIQUE);
                    break;

                case PLAY_WITH_ROCKS:
                    // 1/2 physique | 1/2 constitution
                    addExpAndLevel((long) exp/2, Stat.PHYSIQUE);
                    addExpAndLevel((long) exp/2, Stat.CONSTITUTION);
                    break;

                case FIGHT_BEES_FOR_HONEY:
                    // 1/2 Intellect | 1/2 Agility
                    addExpAndLevel((long) exp/2, Stat.AGILITY);
                    addExpAndLevel( (long) exp/2, Stat.PHYSIQUE);
                    break;

                case DROOL_ON_THINGS:
                    //lose bonus xp for each minute drooling, against future xp if negative
                    sleepBonus -= exp;
                    break;

                case TERRORIZE_VILLAGERS:
                    // 3/4 intelligence | 1/4 magic
                    addExpAndLevel((long)(3*exp/4), Stat.INTELLECT);
                    addExpAndLevel((long) exp, Stat.MAGIC);
                    break;

                case GROWL_AT_BIRDS:
                    //intellect
                    addExpAndLevel((long) exp, Stat.INTELLECT);
                    break;

                case SLASH_AT_TREES:
                    // physique
                    addExpAndLevel((long) exp, Stat.PHYSIQUE);
                    break;

                case ABDUCT_AND_EAT_A_VILLAGER:
                    // 1/2 magic | 1/2 intellect
                    addExpAndLevel((long) exp/2, Stat.INTELLECT);
                    addExpAndLevel((long) exp/2, Stat.MAGIC);
                    break;
            }
        }
    }

    private void addExpAndLevel(long exp, Stat stat){

        switch (stat){
            case PHYSIQUE:
                for (int i = 0; i < exp; i++) {
                    expPhysique += 1;
                    if(atLevelUp(expPhysique)){
                        lvlPhysique+= 1 + PHYSIQUE;
                        addTotals();
                    }
                }                break;

            case AGILITY:
                for (int i = 0; i < exp; i++) {
                    expAgility += 1;
                    if(atLevelUp(expAgility)){
                        lvlAgility+= 1 + AGILITY;
                        addTotals();
                    }
                }                break;

            case CONSTITUTION:
                for (int i = 0; i < exp; i++) {
                    expConstitution += 1;
                    if(atLevelUp(expConstitution)){
                        lvlConstitution+= 1 + CONSTITUTION;
                        addTotals();
                    }
                }                break;

            case INTELLECT:
                for (int i = 0; i < exp; i++) {
                    expIntellect += 1;
                    if(atLevelUp(expIntellect)){
                        lvlIntellect+= 1 + INTELLECT;
                        addTotals();
                    }
                }                break;

            case MAGIC:
                for (int i = 0; i < exp; i++) {
                    expMagic += 1;
                    if(atLevelUp(expMagic)){
                        lvlMagic+= 1 + MAGIC;
                        addTotals();
                    }
                }                break;
        }
        //reset sleep bonus so it only applies to activity directly after sleep
        //add tribe XP every level up
        totalExp += exp;
        sleepBonus = 0;
    }
    public void addTotals()
    {
        lvlTotals++;
        if(lvlTotals % 3 == 0)
        {
            totalLevel++;
            PANDA.Tribe.addExperience(1500);
        }
    }

    public boolean atLevelUp(long exp){
        return (0.04 * Math.sqrt(exp) % 1 == 0);
    }

    public void print() {
        //System.out.println("------------ [ JOURNAL ] ------------");
        printStats();
        PANDA.Clock.printTotals();
    }

    //primarily debug
    public void printStats(){
        String role = "Member of the ";
        String parents = PANDA.hasParents() ? PANDA.MOTHER.NAME + " and " + PANDA.FATHER.NAME : "";
        String gender = SEX.toString();
        if(PANDA.hasParents()) gender = PANDA.getJournal().SEX == Sex.MALE ? "Son of " + parents : "Daughter of " + parents;
        if(PANDA.isElder) role = "Elder of the ";
        if(PANDA.isChief) role = "Chieftain of the ";
        System.out.println(NAME + " (" + gender + ") | Level " + totalLevel + " (" + lvlTotals + ") | Age: " + PANDA.Clock.age()
                + "\n" + role + PANDA.Tribe.NAME + " Lv(" + PANDA.Tribe.Level + "):[" + PANDA.Tribe.size() + "/" + PANDA.Tribe.maxSize + "] <" + PANDA.Tribe.Experience + "/" + PANDA.Tribe.nextLevelAt() +">");
        System.out.println("------------------------------------");
        System.out.println("[Breed] " + (int) BREED + " [Quality] " + (int) QUALITY + " [Size] " + (int) SIZE + " [Fertility] " + FERTILITY + " [Luck] " + (int) LUCK);
        System.out.println("EV STATS: [PHYSIQUE] " + (int) PHYSIQUE + " [AGILITY] " + (int) AGILITY + " [CONSTITUTION] " + (int) CONSTITUTION + " [MAGIC] " + (int) MAGIC + " [INTELLECT] " + (int) INTELLECT);
        System.out.println("LEVELS: [PHYSIQUE] " + (int) lvlPhysique + " [AGILITY] " + (int) lvlAgility + " [CONSTITUTION] " + (int) lvlConstitution + " [MAGIC] " + (int) lvlMagic + " [INTELLECT] " + (int) lvlIntellect);
        System.out.println("------------------------------------");
    }

    public byte[] getEVStats(){
        return new byte[]{BREED, QUALITY, SIZE, FERTILITY, LUCK, PHYSIQUE, AGILITY, INTELLECT, CONSTITUTION, MAGIC};
    }

}
