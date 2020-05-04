package BoredPanda;

import BoredPanda.enums.Activity;

import java.util.*;

class Action implements Runnable{

    private BoredPanda PANDA;
    long duration; // minutes
    Activity activity;
    private final Random R = new Random();


    public Action(BoredPanda panda, Activity activity)
    {
        PANDA = panda;
        choose(activity);
    }

    public Action(BoredPanda panda, Activity activity, Long duration)
    {
        this(panda, activity);
        setDuration(duration);
    }

    public Action(BoredPanda panda)
    {
        this(panda, null, null);
    }

    public Action(BoredPanda panda, Long duration)
    {
        this(panda, null, duration);
    }

    @Override
    public void run(){
        try{
            //announce();
            //PANDA.Clock.SCHEDULER.awaitTermination(duration, PANDA.Clock.timeUnit);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void announce(){
        System.out.println(PANDA + " is going to " + activity + " for the next " + duration + " minutes. SYSTEM TIME: " + System.currentTimeMillis());
    }

    public Action choose(Activity a){
        setActivity(a);
        setDuration(null);
        return this;
    }

    void setActivity(Activity a) {
        this.activity = a == null ? randomActivity() : a;
    }

    private Activity randomActivity()
    {
        Activity statement = Activity.SLEEP;
        byte b = (byte) (R.nextInt(Activity.values().length) + 1),
                i = 0;
        for (Activity activity : Activity.values())
        {
            if(++i == b) statement = activity;
        }
        return statement;
    }

    private long generateDuration()
    {
        long bound;
        switch (activity)
        {
            case SLEEP: bound =  R.nextInt(11) + 8;break;
            case EAT_BAMBOO: bound =  R.nextInt(5) + 5;break;
            case CLIMB_TREES: bound =  R.nextInt(7) + 3;break;
            case SWIM: bound =  R.nextInt(7) + 3;break;
            case PLAY_WITH_ROCKS: bound =  R.nextInt(9) + 1;break;
            case FIGHT_BEES_FOR_HONEY: bound =  R.nextInt(6) + 4;break;
            case DROOL_ON_THINGS: bound =  R.nextInt(3) + 1;break;
            case TERRORIZE_VILLAGERS: bound =  R.nextInt(6) + 4;break;
            case GROWL_AT_BIRDS: bound =  R.nextInt(9) + 1;break;
            case SLASH_AT_TREES: bound =  R.nextInt(7) + 3;break;
            case ABDUCT_AND_EAT_A_VILLAGER: bound =  R.nextInt(6) + 4; break;
            default: bound = R.nextInt(10)+1;
        }
        return ((R.nextInt(47) + 13) * (R.nextInt((int) bound) + 2));
    }

    void setDuration(Long d) {
        this.duration = d == null ? generateDuration() : d;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString(){
        return activity.name() + " [" + duration + "]";
    }
}