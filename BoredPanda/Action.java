package BoredPanda;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Action implements Runnable{

    private int choice;
    private long duration; // minutes
    private String name;
    private List<String> taskList = new ArrayList<String>();
    private Random R = new Random();

    public Action(){}

    @Override
    public void run(){
        try{
            announce();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void announce(){
        System.out.println("Panda is going to " + name.toLowerCase() + " for the next " + duration + " minutes");
    }

    public Action choose (Integer c) {
        applyChoice(c);
        return this;
    }

    public void applyChoice(Integer c){ //todo turn activity type into enum for scaling activity list

        choice = (isNull(c) || c < 0 || 10 < c) ? R.nextInt(10) : c;
        List<String> tasks = new ArrayList<String>();

        switch (choice) {

            case 0:
                /*tasks.add("The bored panda looks around his surroundings for a place to lay his head.");
                tasks.add("Panda sees many logs, but none look good enough for him and so he begins to wander around his bamboo forest.");
                tasks.add("He sees a suitably comfortable log lying near the base of a boulder by the side of the stream.");
                tasks.add("Curious about the best way to lay his head down, the panda inspects his chosen log more closely, and begins to pile leaves around it.");
                tasks.add("He preens himself, content that his bed will be more comfortable and with a better view than any other spot on the forest floor");
                tasks.add("And in the very moment that the very bored Panda laid his very weary head down, he fell asleep.");*/
                tasks.add("|0|");
                setFields("Sleep", tasks, R.nextInt(8) + 8);
                break;

            case 1:
                tasks.add("|1|");
                setFields("Eat Bamboo", tasks, R.nextInt(5) + 5);
                break;

            case 2:
                tasks.add("|2|");
                setFields("Climb Trees", tasks, R.nextInt(7) + 3);
                break;

            case 3:
                tasks.add("|3|");
                setFields("Swim", tasks, R.nextInt(7) + 3);
                break;

            case 4:
                tasks.add("|4|");
                setFields("Play with Rocks", tasks, R.nextInt(9) + 1);
                break;

            case 5:
                tasks.add("|5|");
                setFields("Fight bees for honey", tasks, R.nextInt(6) + 4);
                break;

            case 6:
                tasks.add("|6|");
                setFields("Drool on Things", tasks, R.nextInt(9) + 1);
                break;

            case 7:
                tasks.add("|7|");
                setFields("Terrorize Villagers", tasks, R.nextInt(6) + 4);
                break;

            case 8:
                tasks.add("|8|");
                setFields("Growl at Birds", tasks, R.nextInt(9) + 1);
                break;

            case 9:
                tasks.add("|9|");
                setFields("Slash at Trees", tasks, R.nextInt(7) + 3);
                break;
            case 10:
                tasks.add("|10|");
                setFields("Abduct and Eat a Villager", tasks, R.nextInt(6) + 4);
                break;
        }
    }

    public Action setFields(String n, List<String> l, int b){
        setName(n);
        setTasks(l);
        setDuration(b);
        return this;
    }

    public void addDuration(long i){
        this.duration += i;
    }

    public Action setDuration(Integer bound) {

        //can't do anything with a null duration, so replace it
        if (bound == null){ this.duration = ((R.nextInt(47) + 13) * (R.nextInt(R.nextInt(10)) + 2));}

        //bound of -1 gives duration 0 for initializer purposes
        else if (bound <= 0 || bound > 16) { this.duration = bound < 0 ? 0 : bound; }

        else{ this.duration = ((R.nextInt(47) + 13) * (R.nextInt(bound) + 2));}

        return this;
    }

    public Action setName(String n){
        this.name = isNull(n) ? "Action Name": n;
        return this;
    }

    public Action setTasks(List<String> l){
        List<String> defaults = Arrays.asList("BLANK", "BLANK", "BLANK");
        this.taskList = isNull(l) ? new ArrayList<>(defaults) : l;
        return this;
    }

    public int getChoice(){
        return choice;
    }

    public long getDuration(){
        return duration;
    }

    public boolean isNull(Object o){
        return Objects.isNull(o);
    }

    @Override public String toString(){
        return "[" + name + "]" + " {" + duration + " minutes}" ;
    }

}