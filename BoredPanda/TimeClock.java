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
    final long SHIFT_LENGTH = 8 * 60; //set length of a shift / day
    final byte SHIFTS_PER_PERIOD = 5; //set number of shifts/days per period/period
    private final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private SimpleDateFormat formatter = new SimpleDateFormat();

    private final ConcurrentHashMap<Activity, Action> SHIFT;
    private final ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>> PERIOD;
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>>> HISTORY;
    private final ConcurrentHashMap<Activity, Long> TOTAL;

    byte days = 0;
    private long dayClock = 0,
            totalClock = 0,
            totalOT = 0;
    private boolean endOfShift = false,
            endOfPeriod = false;


    public TimeClock(BoredPanda panda) {
        PANDA = panda;
        SHIFT = new ConcurrentHashMap<Activity, Action>(Activity.values().length); // 11k activity types / 11 v actions
        PERIOD = new ConcurrentHashMap<Byte, ConcurrentHashMap<Activity, Action>>(SHIFTS_PER_PERIOD); // X k shifts per period / X v SHIFT maps
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

    private void endActivity()
    {
        Action currentAction = PANDA.currentAction,
                totalAction = SHIFT.get(currentAction.activity);
        this.endOfShift = (dayClock += currentAction.duration) >= SHIFT_LENGTH;
        addToActivityTotal(currentAction);
        totalAction.duration += currentAction.getDuration();
        SHIFT.put(totalAction.activity, totalAction);
        updateTotalClock();
    }

    private void endShift() {
        this.endOfPeriod = ++days == SHIFTS_PER_PERIOD;
        PERIOD.put(days, SHIFT);
        clearSHIFT();
        if(PANDA.isChief) PANDA.Tribe.decrementShiftsLeft();
    }

    private void endPeriod()
    {
        HISTORY.put(periods(), PERIOD);
        compute();
        clearPERIOD();
        if(PANDA.isChief) PANDA.Tribe.decrementPeriodsLeft();
    }

    private void clearPERIOD(){
        this.days = 0;
        PERIOD.clear();
        clearSHIFT();
        PERIOD.put((byte) 0, SHIFT);
    }

    private void clearSHIFT() {
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

    private ConcurrentHashMap<Activity, Action> actionsFromShift(Long period, Byte shift)
    {
        if(period.equals(periods()))return SHIFT;
        else return HISTORY.get(period).get(shift);
    }

    private ConcurrentHashMap<Activity, Action> actionsFromPeriod(Long period) {

        byte shiftsInPeriod = period.equals(periods()) ? (byte) PERIOD.size() : (byte) HISTORY.get(period).size();

        ConcurrentHashMap<Activity, Action> periodTotals = new ConcurrentHashMap<Activity, Action>();
            for (Activity activity : Activity.values()){periodTotals.put(activity, new Action(PANDA, activity,0L));}

        List<Action> actions = new ArrayList<Action>();
            for (byte shift = 0; shift < shiftsInPeriod; shift++){actions.addAll(actionsFromShift(period, shift).values());}

        for (Action action : actions)
        {
            Activity activity = action.activity;
            long duration = action.duration + periodTotals.get(activity).duration;
            Action total = new Action(PANDA, activity, duration);
            periodTotals.put(activity, total);
        }
        return periodTotals;
    }

    private long[] totalClockFromShift(Long period, Byte shift)
    {
        long shiftClock = 0,
                shiftOT = 0;

        ArrayList<Action> readShift = period.equals(periods()) ? new ArrayList<>(SHIFT.values()) :  new ArrayList<>(HISTORY.get(period).get(shift).values()); // if asking for clock from current period, iterate for shifts in current period
        for (Action action : readShift)
        {
            shiftClock += action.duration;
            shiftOT += (shiftClock - SHIFT_LENGTH) < 0 ? 0 : (shiftClock - SHIFT_LENGTH);
        }
        return new long[]{shiftClock, shiftOT};
    }

    private long[] totalClockFromPeriod(Long period)
    {
        long periodClock = 0,
                periodOT = 0;
        byte shiftsInPeriod = period.equals(periods()) ? shifts() : (byte) HISTORY.get(period).size(); // if asking for clock from current period, iterate for number of shifts in current period
        for(byte shift = 0; shift < shiftsInPeriod; shift++)
        {
            periodClock += totalClockFromShift(period, shift)[0];
            periodOT += totalClockFromShift(period, shift)[1];
        }
        return new long[]{periodClock, periodOT};
    }

    void printShiftFromHistory(Long period, Byte shift)
    {
        ArrayList<Action> actions = new ArrayList<Action>(actionsFromShift(period, shift).values());
        System.out.println("SHIFT: " + shift);
        System.out.println("------------------------------------");
        for (Action action : actions){System.out.println(action);}
        System.out.println("------------------------------------");
    }

    void printPeriodFromHistory(Long period) {
        ArrayList<Action> actions = new ArrayList<Action>(actionsFromPeriod(period).values());
        System.out.println("\n\n------------------------------------");
        System.out.println(PANDA);
        System.out.println("------------------------------------");
        System.out.println("PERIOD " + (period - 1) + " -> " + (period) + " : [HOURS] " + periodClock(period) + " [OT] " + periodOT(period) + " ]" );
        System.out.println("------------------------------------");
        for (Action action : actions){System.out.println(action);}
        System.out.println("------------------------------------");

    }

    public void printTotals(){

        System.out.println("[TOTAL PERIODS] " + periods() + " : [ " + totalClock/60 + " HOURS" + " / " + totalOT/60 + " OT ]");
        System.out.println("------------------------------------");
        for (Activity activity : Activity.values()){System.out.println(activity.name() + " " + TOTAL.get(activity)/60 + " hours");}
        System.out.println("------------------------------------\n");
    }

    boolean isEndOfShift()
    {
        return endOfShift;
    }

    boolean isEndOfPeriod()
    {
        return endOfPeriod;
    }

    Byte shifts(){return (byte) PERIOD.size();}

    Long periods(){return (long) HISTORY.size();}

    long periodClock(long period){return totalClockFromPeriod(period)[0];}

    long periodOT(long period){return totalClockFromPeriod(period)[1];}

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
                    PANDA.addExperience(exp, Stat.CONSTITUTION);
                    break;
                }
                case CLIMB_TREES: {
                    //1/2 agil + 1/2 physique
                    PANDA.addExperience(exp / 2, Stat.AGILITY);
                    PANDA.addExperience(exp / 2, Stat.PHYSIQUE);
                    break;
                }

                case SWIM: {
                    // 1/2 constitution | 1/4 physique | 1/4 agility
                    PANDA.addExperience(exp / 2, Stat.CONSTITUTION);
                    PANDA.addExperience(exp / 4, Stat.AGILITY);
                    PANDA.addExperience(exp / 4, Stat.PHYSIQUE);
                    break;
                }

                case PLAY_WITH_ROCKS: {
                    // 1/2 physique | 1/2 constitution
                    PANDA.addExperience(exp / 2, Stat.PHYSIQUE);
                    PANDA.addExperience(exp / 2, Stat.CONSTITUTION);
                    break;
                }

                case FIGHT_BEES_FOR_HONEY: {
                    // 1/2 Intellect | 1/2 Agility
                    PANDA.addExperience(exp / 2, Stat.AGILITY);
                    PANDA.addExperience(exp / 2, Stat.PHYSIQUE);
                    break;
                }

                case DROOL_ON_THINGS: {
                    //lose bonus xp for each minute drooling, against future xp if negative
                    sleepBonus -= exp;
                    break;
                }

                case TERRORIZE_VILLAGERS: {
                    // 3/4 intelligence | 1/4 magic
                    PANDA.addExperience((3 * exp / 4), Stat.INTELLECT);
                    PANDA.addExperience(exp, Stat.MAGIC);
                    break;
                }

                case GROWL_AT_BIRDS: {
                    //intellect
                    PANDA.addExperience(exp, Stat.INTELLECT);
                    break;
                }
                case SLASH_AT_TREES: {
                    // physique
                    PANDA.addExperience(exp, Stat.PHYSIQUE);
                    break;
                }
                case ABDUCT_AND_EAT_A_VILLAGER: {
                    // 1/2 magic | 1/2 intellect
                    PANDA.addExperience(exp / 2, Stat.INTELLECT);
                    PANDA.addExperience(exp / 2, Stat.MAGIC);
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
