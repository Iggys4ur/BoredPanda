package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class pandaJournal {

    private final long startTime = System.currentTimeMillis();
    private final boredPanda PANDA;
    private final ScheduledExecutorService SCHEDULER;

    //All the stats
    private final float BREED = new Random().nextInt(4) + 1;             // five breeds, unaffected by quality
    private final short QUALITY = (short) (new Random().nextInt(9) + 1 + BREED/2);         // ten qualities, affected by breed
    private final float FERTILITY = new Random().nextFloat()*(QUALITY/5);
    private final float LUCK = (float) (new Random().nextFloat()*((double)(QUALITY)/5));
    private short Size = (short) ((new Random().nextFloat()*3*100)/100);
    private float Physique = new Random().nextInt(5 + (QUALITY/2)) + 1;
    private float Agility = new Random().nextInt(5 + (QUALITY/2)) + 1;
    private float Charisma = new Random().nextInt(5 + (QUALITY/2)) + 1;
    private float Magic = new Random().nextInt(5 + (QUALITY/2)) + 1;
    private float Intellect = new Random().nextInt(5 + (QUALITY/2)) + 1 ;

    private Action[] journal = new Action[10];
    private List<Action> activityHistory = new ArrayList<>();

    //all the clocks
    protected final TimeUnit timeUnit = TimeUnit.MILLISECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 480; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
    protected int day = 0;
    protected int week = 0;
    protected long age = 0;
    protected long dayTimeClocked = 0;
    protected long weekTimeClocked = 0;
    protected long clockedOvertime = 0;  //only awarded overtime for working a full week

    protected pandaJournal(boredPanda panda, ScheduledExecutorService s){
        this.PANDA = panda;
        this.SCHEDULER = s;
        initLogs();
    }

    public void initLogs()
    {
        activityHistory.add(PANDA.previousAction);
        Action a = new Action();
        for (int i = 0; i < journal.length; i++) {
            journal[i] = new Action().choose(i).setDuration(0);
        }
        print(); //DEBUG
        System.out.println(" LOGS INITIALIZED ");
    }

    public void punchClock(boolean endOfDay)
    {
        //System.out.println("PREVIOUS = " + PANDA.previousAction); //DEBUG
        //System.out.println("CURRENT = " + PANDA.currentAction); //DEBUG

        activityHistory.add(PANDA.currentAction);
        SCHEDULER.schedule(PANDA.currentAction, PANDA.previousAction.getDuration(), timeUnit);

        if (endOfDay) {
            day++;
            dayTimeClocked += PANDA.getCurrentAction().getDuration();
            weekTimeClocked += dayTimeClocked; //only add full shifts to the week + age
            dayTimeClocked = 0;
        }
        else {
            dayTimeClocked += PANDA.getCurrentAction().getDuration();
        }

        PANDA.nextAction();
    }

    public void submitClock(){
        clockOvertime();
        week++;
        //TODO what else happens when the time clock is submitted?
    }

    public void clockOvertime()
    {
        this.clockedOvertime += weekTimeClocked - (SHIFT_LENGTH * SHIFTS_PER_PERIOD);
    }

    public void clearClocks()
    {
        this.dayTimeClocked = 0;
        this.weekTimeClocked = 0;
        this.clockedOvertime = 0;
    }

    public void compute()
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

    protected void print() {

        /*if(debug){
            System.out.println("*************************************");
            System.out.println("----------------DEBUG----------------");
            System.out.println("*************************************\n");
            for (Action a: activityHistory) {
                System.out.println(a.toString());
            }
            System.out.println("\n*************************************");
            System.out.println("*************************************\n");
            return;
        }*/

        System.out.println("------------ [ JOURNAL ] ------------");
        System.out.println("NAME: " + PANDA.getPandaName() +" | AGE : " + age);
        System.out.println("P: " + (int) Physique + " | A: " + (int) Agility + " | C: " + (int) Charisma + " | M: " + (int) Magic + " | I: " + (int) Intellect);
        System.out.println("B:" + (int) BREED + " | Q: " + (int) QUALITY + " | S: " + (int) Size + " | F: " + FERTILITY + " | L: " + (int) LUCK);
        System.out.println("------------------------------------");
        System.out.println("WEEK " + week + " => [ HOURS: " + weekTimeClocked + " | OT: " + clockedOvertime + " ]" );
        System.out.println("------------------------------------");
        for (Action a : journal){
            System.out.println("|"+ a.getChoice() + "| " + a.toString());
        }
        System.out.println("------------------------------------\n");
        clearClocks();
    }

    public boolean dayStarted() {return dayTimeClocked > 0;}

    public boolean endOfDay(){
        return !(dayTimeClocked < SHIFT_LENGTH);
    }

    public boolean endOfPeriod() {return (day == SHIFTS_PER_PERIOD);} //TODO

    public TimeUnit timeUnit(){
        return timeUnit;
    }


}
