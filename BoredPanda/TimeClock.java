package BoredPanda;


import BoredPanda.enums.Activity;
import BoredPanda.enums.Stat;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeClock {

    //attributes
    private final BoredPanda PANDA;
    private final long startTime = System.currentTimeMillis();
    private final TimeUnit timeUnit = TimeUnit.SECONDS; //set TimeUnit for panda
    private final long SHIFT_LENGTH = 8 * 60; //set length of a shift / day
    final byte SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/period
    private final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private SimpleDateFormat formatter = new SimpleDateFormat();

    private final ConcurrentHashMap<Activity, Action> SHIFT;
    private final ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>> PERIOD;
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>>> HISTORY;
    private final ConcurrentHashMap<Activity, Long> TOTAL;

    byte days = 0;
    long dayClock = 0,
            totalClock = 0,
            totalOT = 0;
    boolean endOfShift = false,
            endOfPeriod = false;


    public TimeClock(BoredPanda panda) {
        PANDA = panda;
        SHIFT = new ConcurrentHashMap<Activity, Action>(Activity.values().length); // 11k activity types / 11 v actions
        PERIOD = new ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>>(SHIFTS_PER_PERIOD); // k shifts per period / 7 v SHIFT maps
        HISTORY = new ConcurrentHashMap<Long, ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>>>(); // X k long / X v PERIOD maps
        TOTAL = new ConcurrentHashMap<Activity, Long>(Activity.values().length); // 11k activity types / 11 v long
        initLogs();
    }

    private void initLogs() {
        clearPERIOD(); //initialize period
        for (Activity activity : Activity.values())
        {
            TOTAL.put(activity, 0L);
        }
    }

    void punch() {
        //SCHEDULER.execute(PANDA.currentAction);
        endActivity();
        SCHEDULER.schedule(PANDA.currentAction, PANDA.previousAction.duration, timeUnit);
        if (endOfShift) {
            endShift();
            if (endOfPeriod) {
                endPeriod();
            }
        }
        PANDA.nextAction();
    }

    void endActivity()
    {
        Action currentAction = PANDA.currentAction,
                totalAction = SHIFT.get(currentAction.activity);
        this.endOfShift = (dayClock += currentAction.duration) >= SHIFT_LENGTH;
        addToActivityTotal(currentAction);
        totalAction.duration += currentAction.getDuration();
        SHIFT.put(totalAction.activity, totalAction);
        updateTotalClock();
    }

    void endShift() {
        this.endOfPeriod = ++days == SHIFTS_PER_PERIOD;
        PERIOD.put(days, SHIFT);
        clearSHIFT();
        PANDA.Tribe.shiftsLeft -= 1;
    }

    void endPeriod()
    {
        HISTORY.put(periods(), PERIOD);
        compute();
        clearPERIOD();
        PANDA.Tribe.periodsLeft -= 1;
    }

    void clearPERIOD(){
        this.days = 0;
        PERIOD.clear();
        for (byte shift = 0; shift < SHIFTS_PER_PERIOD; shift++)
        {
            clearSHIFT();
            PERIOD.put(shift, SHIFT);
        }
    }

    void clearSHIFT() {
        this.dayClock = 0;
        SHIFT.clear();
        for (Activity activity : Activity.values())
        {
            SHIFT.put(activity, new Action(PANDA, activity,0L));
        }
    }

    void updateTotalClock()
    {
        totalClock += PANDA.currentAction.duration;
        if(dayClock > SHIFT_LENGTH) totalOT += (dayClock - SHIFT_LENGTH);
    }

    void addToActivityTotal(Action toUpdate)
    {
        long currentTotal = TOTAL.get(toUpdate.activity);
        currentTotal += toUpdate.duration;
        TOTAL.put(toUpdate.activity, currentTotal);
    }

    private long[] clockFromShift(long period, byte shift)
    {
        long shiftClock = 0,
                shiftOT = 0;

        ArrayList<Action> readShift = new ArrayList<Action>(HISTORY.get(period).get(shift).values());
        for (Action action : readShift)
        {
            shiftClock += action.duration;
            long ot = shiftClock - SHIFT_LENGTH;
            shiftOT += ot < 0 ? 0 : ot;
        }
        return new long[]{shiftClock, shiftOT};
    }

    private long[] clockFromPeriod(long period)
    {
        long periodClock = 0,
                periodOT = 0;

        for(byte shift = 0; shift < HISTORY.get(period).size(); shift++)
        {
            periodClock += clockFromShift(period, shift)[0];
            periodOT += clockFromShift(period, shift)[1];
        }
        return new long[]{periodClock, periodOT};
    }

    public void print() {printHistory(periods());}

    void printHistory(long period) {
        System.out.println("------------------------------------");
        System.out.println("PERIOD " + (period - 1) + " => " + period + " : [HOURS] " + periodClock(period) + " [OT] " + periodOT(period) + " ]" );
        System.out.println("------------------------------------");
    }

    public void printTotals(){

        System.out.println("[TOTAL PERIODS] " + periods() + " : [ " + totalClock/60 + " HOURS" + " / " + totalOT/60 + " OT ]");
        System.out.println("------------------------------------");
        for (Activity activity : Activity.values())
        {
            System.out.println(activity.name() + " " + TOTAL.get(activity)/60 + " hours");
        }
        System.out.println("------------------------------------\n");
    }

    long periods(){return HISTORY.size();}

    long periodClock(long period){return clockFromPeriod(period)[0];}

    long periodOT(long period){return clockFromPeriod(period)[1];}

    long age(){return System.currentTimeMillis() - startTime;}

    void compute() {
        long sleepBonus = 0;

        for (Activity activity : Activity.values()) {

            double xp = TOTAL.get(activity) + sleepBonus;
            long exp = (long) xp;

            switch (activity) {
                case SLEEP: {
                    //add sleep time to bonus xp of next activity; stack iff panda's next activity is sleep
                    sleepBonus += exp;
                    break;
                }
                case EAT_BAMBOO: {
                    //constitution
                    PANDA.getJournal().addExpAndLevel(exp, Stat.CONSTITUTION);
                    break;
                }
                case CLIMB_TREES: {
                    //1/2 agil + 1/2 physique
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.AGILITY);
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.PHYSIQUE);
                    break;
                }

                case SWIM: {
                    // 1/2 constitution | 1/4 physique | 1/4 agility
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.CONSTITUTION);
                    PANDA.getJournal().addExpAndLevel(exp / 4, Stat.AGILITY);
                    PANDA.getJournal().addExpAndLevel(exp / 4, Stat.PHYSIQUE);
                    break;
                }

                case PLAY_WITH_ROCKS: {
                    // 1/2 physique | 1/2 constitution
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.PHYSIQUE);
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.CONSTITUTION);
                    break;
                }

                case FIGHT_BEES_FOR_HONEY: {
                    // 1/2 Intellect | 1/2 Agility
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.AGILITY);
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.PHYSIQUE);
                    break;
                }

                case DROOL_ON_THINGS: {
                    //lose bonus xp for each minute drooling, against future xp if negative
                    sleepBonus -= exp;
                    break;
                }

                case TERRORIZE_VILLAGERS: {
                    // 3/4 intelligence | 1/4 magic
                    PANDA.getJournal().addExpAndLevel((long) (3 * exp / 4), Stat.INTELLECT);
                    PANDA.getJournal().addExpAndLevel(exp, Stat.MAGIC);
                    break;
                }

                case GROWL_AT_BIRDS: {
                    //intellect
                    PANDA.getJournal().addExpAndLevel(exp, Stat.INTELLECT);
                    break;
                }
                case SLASH_AT_TREES: {
                    // physique
                    PANDA.getJournal().addExpAndLevel(exp, Stat.PHYSIQUE);
                    break;
                }
                case ABDUCT_AND_EAT_A_VILLAGER: {
                    // 1/2 magic | 1/2 intellect
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.INTELLECT);
                    PANDA.getJournal().addExpAndLevel(exp / 2, Stat.MAGIC);
                    break;
                }
            }
        }
    }


    String formatHours (long input){return formatWithPattern(input, "hh:mm"); }

    String formatDays (long input){
        return formatWithPattern(input, "dd:hh:mm");
    }

    String formatPeriods (long input){
        return formatWithPattern(input, "ww:dd:hh:mm");
    }

    String formatMonth (long input){
        return formatWithPattern(input, "MM:ww:dd:hh:mm");
    }

    String formatYear (long input){
        return formatWithPattern(input, "yy:MM:ww:dd:hh:mm");
    }

    String formatWithPattern (long input, String pattern){
        formatter.applyPattern(pattern);
        return formatter.format(new Date(input));
    }



}
