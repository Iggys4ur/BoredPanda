package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private Action[] journal = new Action[11];
    private List<Action> activityHistory = new ArrayList<>();

    protected pandaJournal(boredPanda panda, String name){
        this.PANDA = panda;
        this.NAME = name;
        initLogs();
        initStats();
    }

    private void initLogs()
    {
        activityHistory.add(PANDA.previousAction);
        Action a = new Action();
        for (int i = 0; i < journal.length; i++) {
            journal[i] = new Action().choose(i).setDuration(0);
        }
        //print(); //DEBUG
        //System.out.println(" LOGS INITIALIZED \n\n\n");
    }

    private void initStats()
    {

    }


    protected void compute()
    {
        for (int i = 0; i < journal.length; i++) {
            for (Action a : activityHistory){
                if(a.getChoice() == i)
                {
                    journal[i].addDuration(a.getDuration());
                }
            }
        }
    }

    protected void print(){
        print(PANDA.clock.week);
    }

    protected void print(int week) {

        long[] clock = PANDA.clock.history.get(week-1);

        System.out.println("------------ [ JOURNAL ] ------------");
        System.out.println("NAME: " + NAME +" | AGE : " + PANDA.clock.age());
        System.out.println("P: " + (int) Physique + " | A: " + (int) Agility + " | C: " + (int) Charisma + " | M: " + (int) Magic + " | I: " + (int) Intellect);
        System.out.println("B:" + (int) BREED + " | Q: " + (int) QUALITY + " | S: " + (int) SIZE + " | F: " + FERTILITY + " | L: " + (int) LUCK);
        System.out.println("------------------------------------");
        System.out.println("WEEK " + clock[0] + " => " + (clock[0] + 1) + " : [ HOURS: " + clock[1] + " | OT: " + clock[2] + " ]" );
        System.out.println("------------------------------------");
        for (Action a : journal){
            System.out.println("|"+ a.getChoice() + "| " + a.toString());
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

    public void updateJournal(Action[] arr) {
        for (int i = 0; i < arr.length; i++) {
            journal[i] = arr[i];
        }
    }

    protected List<Action> getActivityHistory() {
        return activityHistory;
    }

    protected void addToHistory(Action a){
        activityHistory.add(a);
    }

}
