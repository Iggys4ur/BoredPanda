package BoredPanda;

import BoredPanda.enums.Activity;

import java.util.*;

class Action implements Runnable{

    private BoredPanda PANDA;
    protected int choice;
    protected long duration; // minutes
    protected Activity activity;
    private Random R = new Random();

    public Action(BoredPanda panda)
    {
        this(panda, null);
    }

    public Action(BoredPanda panda, Integer i)
    {
        PANDA = panda;
        choose(i);
    }

    @Override
    public void run(){
        try{
            announce();
            //PANDA.Clock.SCHEDULER.awaitTermination(duration, PANDA.Clock.timeUnit);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void announce(){
        System.out.println(PANDA.NAME + " is going to " + activity.name() + " for the next " + duration + " minutes. SYSTEM TIME: " + System.currentTimeMillis());
    }

    public Action choose (Integer c) {
        applyChoice(c);
        return this;
    }

    public void applyChoice(Integer c){

        choice = (c == null || c < 0 || 10 < c) ? R.nextInt(11) : c;

        switch (choice) {

            case 0:
                setFields(Activity.SLEEP, R.nextInt(8) + 8);
                break;

            case 1:
                setFields(Activity.EAT_BAMBOO, R.nextInt(5) + 5);
                break;

            case 2:
                setFields(Activity.CLIMB_TREES, R.nextInt(7) + 3);
                break;

            case 3:
                setFields(Activity.SWIM, R.nextInt(7) + 3);
                break;

            case 4:
                setFields(Activity.PLAY_WITH_ROCKS, R.nextInt(9) + 1);
                break;

            case 5:
                setFields(Activity.FIGHT_BEES_FOR_HONEY, R.nextInt(6) + 4);
                break;

            case 6:
                setFields(Activity.DROOL_ON_THINGS, R.nextInt(9) + 1);
                break;

            case 7:
                setFields(Activity.TERRORIZE_VILLAGERS,R.nextInt(6) + 4);
                break;

            case 8:
                setFields(Activity.GROWL_AT_BIRDS, R.nextInt(9) + 1);
                break;

            case 9:
                setFields(Activity.SLASH_AT_TREES, R.nextInt(7) + 3);
                break;

            case 10:
                setFields(Activity.ABDUCT_AND_EAT_A_VILLAGER, R.nextInt(6) + 4);
                break;
        }
    }

    public Action setFields(Activity a, int b){
        activity = a;
        setDuration(b);
        return this;
    }

    public Action setDuration(Integer bound) {
        //can't do anything with a null duration, so replace it
        if (bound == null){ this.duration = ((R.nextInt(47) + 13) * (R.nextInt(R.nextInt(10)) + 2));}
        //out of bounds gives duration 0 for (bound = -1) initializer purposes
        else if (bound <= 0 || bound > 16) { this.duration = bound < 0 ? 0 : bound; }
        //else in all valid cases (1 - 16) put it through
        else{ this.duration = ((R.nextInt(47) + 13) * (R.nextInt(bound) + 2));}
        return this;
    }

    @Override
    public String toString(){
        return activity.name() + " [" + duration + "]";
    }
}