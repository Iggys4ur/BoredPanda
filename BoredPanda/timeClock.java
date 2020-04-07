package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class timeClock {

    private final long startTime = System.currentTimeMillis();
    private static boredPanda PANDA;
    private static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    //all the clocks
    protected final TimeUnit timeUnit = TimeUnit.MILLISECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 480; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
    protected int day = 0;
    protected int week = 0;
    protected long dayTimeClocked;
    protected long weekTimeClocked;
    protected long clockedOvertime;  //only awarded overtime for working a full week

    protected List<long[]> history = new ArrayList<>();

    public timeClock(boredPanda panda){
        clearClocks();
        PANDA = panda;
    }
    
    protected void punchClock(boolean endOfDay)
    {
        //System.out.println("PREVIOUS = " + PANDA.previousAction); //DEBUG
        //System.out.println("CURRENT = " + PANDA.currentAction); //DEBUG
        PANDA.getJournal().addToHistory(PANDA.currentAction);
        SCHEDULER.schedule(PANDA.currentAction, PANDA.previousAction.getDuration(), timeUnit);

        if (endOfDay) {
            day++;
            dayTimeClocked += PANDA.getCurrentAction().getDuration();
            weekTimeClocked += dayTimeClocked;
            dayTimeClocked = 0;
            if (endOfPeriod())
            {
                submitClock();
                clearClocks();
            }
        }
        else {
            dayTimeClocked += PANDA.getCurrentAction().getDuration();
        }

        PANDA.nextAction();
    }

    protected void submitClock(){
        clockOvertime();
        history.add(new long[]{week++, weekTimeClocked, clockedOvertime});

        //TODO what else happens when the time clock is submitted for the end of the period?
    }

    protected void clockOvertime() { this.clockedOvertime += weekTimeClocked - (SHIFT_LENGTH * SHIFTS_PER_PERIOD); }

    protected void clearClocks()
    {
        this.dayTimeClocked = 0;
        this.weekTimeClocked = 0;
        this.clockedOvertime = 0;
    }

    protected int age(){ return (int) (System.currentTimeMillis() - startTime ); }

    protected boolean endOfDay(){ return !(dayTimeClocked < SHIFT_LENGTH); }

    protected boolean endOfPeriod() { return (day == SHIFTS_PER_PERIOD); }

}
