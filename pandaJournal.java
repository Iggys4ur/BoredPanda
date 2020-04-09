package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class pandaJournal {

    private final boredPanda PANDA;
    protected final String NAME;

    //Immutable Attributes
    private final byte BREED = (byte) (new Random().nextInt(4)+1),
                    QUALITY = (byte) (new Random().nextInt(6 - (5/(BREED))) + BREED),
                    SIZE = (byte) ((new Random().nextInt((6 - (5/(BREED)) + BREED))/2) +1),
                    FERTILITY = (byte) (new Random().nextInt(11 - (10/(QUALITY))) + BREED),
                    LUCK = (byte) (new Random().nextInt( 11 - (10/QUALITY)));

    //stats
    private byte Physique = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1),
                Agility = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1),
                Intellect = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1),
                Charisma = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1),
                Magic = (byte) (new Random().nextInt(5 + (QUALITY/2)) + 1);

    //actions
    protected Action[] journal = new Action[11];
    protected List<Action> activityHistory = new ArrayList<Action>();

    protected pandaJournal(boredPanda panda, String name){
        this.PANDA = panda;
        this.NAME = name;
        initLogs();
    }

    protected void initLogs()
    {
        PANDA.previousAction = new Action(PANDA).setFields("INITIALIZATION ACTIVITY", null, 1000);
        activityHistory.add(PANDA.previousAction);
        for (int i = 0; i < journal.length; i++) {
            journal[i] = new Action(PANDA, i).setDuration(0);
        }
        PANDA.currentAction = PANDA.previousAction.choose(null);
        //print(); //DEBUG
        //System.out.println(" LOGS INITIALIZED \n\n\n");
    }

    protected void compute()
    {
        for (Action a: activityHistory)
        {
            journal[a.choice].duration += a.duration;
        }
    }

    protected void print() {

        timeClock clock = PANDA.clock;

        System.out.println("------------ [ JOURNAL ] ------------");
        System.out.println("NAME: " + NAME +" | AGE : " + PANDA.clock.age());
        System.out.println("P: " + (int) Physique + " | A: " + (int) Agility + " | C: " + (int) Charisma + " | M: " + (int) Magic + " | I: " + (int) Intellect);
        System.out.println("B:" + (int) BREED + " | Q: " + (int) QUALITY + " | S: " + (int) SIZE + " | F: " + FERTILITY + " | L: " + (int) LUCK);
        System.out.println("------------------------------------");
        System.out.println("TOTAL WEEKS " + clock.weeks + " [ TOTAL HOURS: " + clock.totalClock + " | TOTAL OT: " + clock.totalOvertime + " ]" );
        System.out.println("------------------------------------");
        for (Action a : journal){
            System.out.println(a);
        }
        System.out.println("------------------------------------\n");
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
