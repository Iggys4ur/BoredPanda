package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class timeClock {

    private static boredPanda PANDA;
    private static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    //all the clocks
    private final long startTime = System.currentTimeMillis();
    protected final TimeUnit timeUnit = TimeUnit.MILLISECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 480; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
    protected boolean endOfDay = false,
                        endOfWeek = false;

    protected int days = 0;
    protected int weeks = 0;
    protected long dayClock,
                    weekClock,
                    weekOvertime,
                    totalClock,
                    totalOvertime;

    protected List<long[]> history = new ArrayList<long[]>(1);

    public timeClock(boredPanda panda){
        clearClocks();
        PANDA = panda;
    }
    
    protected void punch()
    {
        SCHEDULER.schedule(PANDA.currentAction, PANDA.previousAction.duration, timeUnit);
        addHours();

        if (endOfDay)
        {
            endDay();
            if (endOfWeek){submitClock();}
        }

        PANDA.nextAction();
    }

    protected void updateHistory(long[] newWeek)
    {
        List<long[]> list = new ArrayList<long[]>(weeks++);
        if(history != null) list.addAll(history);
        list.add(newWeek);
        history = list;
    }

    protected void endDay(){
        weekClock += dayClock;
        dayClock = 0;
        days++;
        endOfWeek = days == SHIFTS_PER_PERIOD;
    }

    protected void submitClock(){
        weekOvertime += (weekClock - (SHIFT_LENGTH * SHIFTS_PER_PERIOD));
        updateHistory(new long[]{weekClock, weekOvertime});
        totalClock += weekClock;
        totalOvertime += weekOvertime;
        clearClocks();

        //TODO what else happens when the time clock is submitted for the end of the period?
    }

    protected void addHours(){
        dayClock += PANDA.currentAction.duration;
        endOfDay = dayClock >= SHIFT_LENGTH;
    }

    protected void print(){
        printHistory(weeks);
    }

    protected void printHistory(int week) {

        System.out.println("------------------------------------");
        System.out.println("WEEK " + week + " => " + (week + 1) + " : [ HOURS: " + history.get(week)[0] + " | OT: " + history.get(week)[1] + " ]" );
        System.out.println("------------------------------------");
        for (Action a : PANDA.getJournal().getJournal()){
            System.out.println(a);
        }
        System.out.println("------------------------------------\n");
    }

    protected void clearClocks()
    {
        this.days = 0;
        this.dayClock = 0;
        this.weekClock = 0;
        this.weekOvertime = 0;
    }

    protected int age(){ return (int) (System.currentTimeMillis() - startTime ); }
}
