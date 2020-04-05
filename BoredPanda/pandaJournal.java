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
    private final float BREED = new Random().nextInt(5);             // five breeds, unaffected by quality
    private final short QUALITY = (short) (new Random().nextInt(9) + 1 + BREED/2);         // ten qualities, affected by breed
    private final float FERTILITY = new Random().nextFloat()*(QUALITY/5);
    private final float LUCK = (float) (new Random().nextFloat()*((double)(QUALITY)/5));
    private short Size = (short) ((new Random().nextFloat()*3*100)/100);
    private float Physique = new Random().nextInt(9) + 1 + (QUALITY / 4);
    private float Agility = new Random().nextInt(9) + 1 + (QUALITY / 4);
    private float Charisma = new Random().nextInt(9) + 1 + (QUALITY / 4);
    private float Magic = new Random().nextInt(9) + 1 + (QUALITY / 4);
    private float Intellect = new Random().nextInt(9) + 1 + (QUALITY / 4);

    private Action[] journal = new Action[10];
    private List<Action> activityHistory = new ArrayList<>();

    //all the clocks
    protected final TimeUnit timeUnit = TimeUnit.MILLISECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 480; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
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
        Action a = new Action();
        for (int i = 0; i < journal.length; i++) {
            journal[i] = new Action().choose(i).setDuration(0);
        }
        System.out.println(BREED + " // " + QUALITY + " // " + FERTILITY + " // " + LUCK + " // " + Size + " // " + Physique + " // " + Agility + " // " + Charisma + " // " + Magic + " // " + Intellect);
        print();
    }

    public void punchClock(boolean endOfDay)
    {
        activityHistory.add(PANDA.getCurrentAction());
        SCHEDULER.schedule(PANDA.getCurrentAction(), lastActionDuration(), timeUnit);

        if (endOfDay) {
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
                if(a.getChoice()-1 == i)
                {
                    journal[i].addDuration(a.getDuration());
                }
            }
        }
    }

    public void print(){

        System.out.println("_____________________________________\n");
        System.out.println("------------ [ JOURNAL ] ------------");
        System.out.println("NAME: " + PANDA.getPandaName());
        System.out.println("AGE : " + age ); //TODO convert age to year/month/day
        System.out.println("( WEEK " + week + " | HOURS: " + weekTimeClocked + " | O-T: " + clockedOvertime + " )" );
        System.out.println("____________________________________\n");
        for (Action a : journal){
            System.out.println("|"+ a.getChoice() + "| " + a.toString());
        }
        System.out.println("_____________________________________\n");
        clearClocks();
    }

    public long lastActionDuration()
    {
        return lastAction().getDuration();
    }

    public Action lastAction(){
        return activityHistory.get(activityHistory.size()-1);
    }

    public boolean dayEnded(){
        return !(dayTimeClocked < SHIFT_LENGTH);
    }

    public TimeUnit timeUnit(){
        return timeUnit;
    }


}
