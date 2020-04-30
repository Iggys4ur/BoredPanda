package BoredPanda;


import BoredPanda.enums.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeClock {

    private final BoredPanda PANDA;
    protected ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private SimpleDateFormat formatter = new SimpleDateFormat();

    //all the clocks
    private final long startTime = System.currentTimeMillis();
    protected final TimeUnit timeUnit = TimeUnit.SECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 8 * 60; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
    protected boolean endOfDay = false,
                        endOfPeriod = false;

    protected long days = 0;
    protected long weeks = 0;
    protected long dayClock = 0,
                    weekClock = 0,
                    weekOvertime = 0,
                    totalClock = 0,
                    totalOvertime = 0;

    protected final ConcurrentHashMap<Long, long[]> HISTORIES;

    public TimeClock(BoredPanda panda){
        PANDA = panda;
        HISTORIES  = new ConcurrentHashMap<>();
        HISTORIES.put(0L, new long[13]);
        clearClocks();
    }
    
    protected void punch()
    {
        //SCHEDULER.execute(PANDA.currentAction);
        SCHEDULER.schedule(PANDA.currentAction, PANDA.previousAction.duration, timeUnit);
        addHours();

        if (endOfDay)
        {
            endDay();
            if (endOfPeriod){endPeriod();}
        }

        PANDA.nextAction();
    }

    //TODO is the issue here?
    protected void updateHistory()
    {
        long[] history = new long[13];
        Action[] journal = PANDA.getJournal().JOURNAL;
        history[0] = totalClock;
        history[1] = totalOvertime;
        for (int i = 0; i < journal.length; i++) {
            //TODO OR HERE?
            history[i+2] += HISTORIES.get(weeks-1)[i+2] + journal[i].duration;
        }
        HISTORIES.put(weeks, history);
        clearClocks();
    }

    protected void endDay(){
        this.weekClock += this.dayClock;
        this.dayClock = 0;
        this.days++;
        this.endOfPeriod = this.days == this.SHIFTS_PER_PERIOD;
        if(PANDA.isChief) PANDA.Tribe.daysLeft -= 1;
    }

    protected void endPeriod(){
        this.weekOvertime += (weekClock - (SHIFT_LENGTH * SHIFTS_PER_PERIOD));
        this.totalClock += weekClock;
        this.totalOvertime += weekOvertime;
        this.weeks++;
        updateHistory(); // TODO or that we call it here?
        if(PANDA.isChief) PANDA.Tribe.periodsLeft -= 1;
    }

    protected void addHours(){
        this.dayClock += PANDA.currentAction.duration;
        this.endOfDay = dayClock >= SHIFT_LENGTH;
    }

    public void print() {
        printHistory(weeks);
        for (Action a : PANDA.getJournal().JOURNAL){
            System.out.println(a);
        }
        System.out.println("------------------------------------\n");
    }

    protected void printHistory(long week) {
        System.out.println("------------------------------------");
        System.out.println("WEEK " + (week - 1) + " => " + week + " : [HOURS] " + (HISTORIES.get(week)[0])/60 + " [OT] " + (HISTORIES.get(week)[1])/60 + " ]" );
        System.out.println("------------------------------------");
    }

    //TODO fix non-elder panda logs??!?!?!!?!? not totalling sometimes, others totalling more than theoretically possible ?!?!?!!?

    public void printTotals(){

        long[] history = weeks == 0 ? HISTORIES.get(0L) : HISTORIES.get(weeks-1);

        System.out.println("[TOTAL WEEKS] " + weeks + " : [ " + (history[0])/60 + " HOURS" + " / " + (history[1])/60 + " OT ]");
        System.out.println("------------------------------------");
        for (int i = 2; i < history.length; i++) {
            System.out.println(history[i]);
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

    protected int age(){
        return (int) (System.currentTimeMillis() - startTime);
    }

    protected String formatHours (long input){
        return formatWithPattern(input, "hh:mm");
    }

    protected String formatDays (long input){
        return formatWithPattern(input, "dd:hh:mm");
    }

    protected String formatWeeks (long input){
        return formatWithPattern(input, "ww:dd:hh:mm");
    }

    protected String formatMonth (long input){
        return formatWithPattern(input, "MM:ww:dd:hh:mm");
    }

    protected String formatYear (long input){
        return formatWithPattern(input, "yy:MM:ww:dd:hh:mm");
    }

    protected String formatWithPattern (long input, String pattern){
        formatter.applyPattern(pattern);
        return formatter.format(new Date(input));
    }



}
