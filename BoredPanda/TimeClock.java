package BoredPanda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeClock {

    private static BoredPanda PANDA;
    private static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private SimpleDateFormat formatter = new SimpleDateFormat();

    //all the clocks
    private final long startTime = System.currentTimeMillis();
    protected final TimeUnit timeUnit = TimeUnit.SECONDS; //set TimeUnit for panda
    protected final long SHIFT_LENGTH = 480; //set length of a shift / day
    protected final int SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/week
    protected boolean endOfDay = false,
                        endOfPeriod = false;

    protected int days = 0;
    protected int weeks = 0;
    protected long dayClock = 0,
                    weekClock = 0,
                    weekOvertime = 0,
                    totalClock = 0,
                    totalOvertime = 0;

    protected List<long[]> history = new ArrayList<long[]>(1);

    public TimeClock(BoredPanda panda){
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
            if (endOfPeriod){endPeriod();}
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
        endOfPeriod = days == SHIFTS_PER_PERIOD;
    }

    protected void endPeriod(){
        weekOvertime += (weekClock - (SHIFT_LENGTH * SHIFTS_PER_PERIOD));
        updateHistory(new long[]{weekClock, weekOvertime});
        totalClock += weekClock;
        totalOvertime += weekOvertime;
        clearClocks();
    }

    protected void addHours(){
        dayClock += PANDA.currentAction.duration;
        endOfDay = dayClock >= SHIFT_LENGTH;
    }

    public void print() {
        printHistory(weeks);
        for (Action a : PANDA.getJournal().getJournal()){
            System.out.println(a);
        }
        System.out.println("------------------------------------\n");
    }

    protected void printHistory(int week) {
        System.out.println("------------------------------------");
        System.out.println("WEEK " + (week - 1) + " => " + week + " : [HOURS] " + (history.get(week-1)[0]) + " [OT] " + (history.get(week-1)[1]) + " ]" );
        System.out.println("------------------------------------");
    }

    public void printTotals(){
        System.out.println("[TOTAL WEEKS] " + weeks + " : [ " + (totalClock) + " HOURS" + " / " + (totalOvertime) + " OT ]");
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
