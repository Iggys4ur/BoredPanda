package BoredPanda;

import java.util.*;

class Action implements Runnable{

    private BoredPanda PANDA;
    private Journal JOURNAL;
    protected int choice;
    protected long duration; // minutes
    protected enumeratedPandas.Activity activity;
    protected String name;
    private Random R = new Random();

    public Action(BoredPanda panda)
    {
        this(panda, null);
    }

    public Action(BoredPanda panda, Integer i)
    {
        PANDA = panda;
        JOURNAL = panda.getJournal();
        choose(i);
    }

    @Override
    public void run(){
        try{
            announce();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void announce(){
        System.out.println(PANDA.NAME + " is going to " + name.toLowerCase() + " for the next " + duration + " minutes");
    }

    public Action choose (Integer c) {
        applyChoice(c);
        return this;
    }

    public void applyChoice(Integer c){

        choice = (c == null || c < 0 || 10 < c) ? R.nextInt(11) : c;

        switch (choice) {

            case 0:
                setFields("Sleep", R.nextInt(8) + 8);
                break;

            case 1:
                setFields("Eat Bamboo", R.nextInt(5) + 5);
                break;

            case 2:
                setFields("Climb Trees", R.nextInt(7) + 3);
                break;

            case 3:
                setFields("Swim", R.nextInt(7) + 3);
                break;

            case 4:
                setFields("Play with Rocks", R.nextInt(9) + 1);
                break;

            case 5:
                setFields("Fight bees for honey", R.nextInt(6) + 4);
                break;

            case 6:
                setFields("Drool on Things", R.nextInt(9) + 1);
                break;

            case 7:
                setFields("Terrorize Villagers",R.nextInt(6) + 4);
                break;

            case 8:
                setFields("Growl at Birds", R.nextInt(9) + 1);
                break;

            case 9:
                setFields("Slash at Trees", R.nextInt(7) + 3);
                break;
            case 10:
                setFields("Abduct and Eat a Villager", R.nextInt(6) + 4);
                break;
        }
    }

    public Action setFields(String n, int b){
        name = n;
        activity = enumeratedPandas.Activity.valueOf(n.replace(' ', '_').toUpperCase());
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

    @Override public String toString(){
        return "[" + name + "]" + " {" + (duration) + " minutes}" ;
    }

}