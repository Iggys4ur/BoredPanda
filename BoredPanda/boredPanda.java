package BoredPanda;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class boredPanda {

    private final pandaJournal journal;
    protected final timeClock clock;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected Action currentAction, previousAction;

    public boredPanda(String name) {
        previousAction = new Action().setFields("INITIALIZATION ACTIVITY", null, 1000);
        currentAction = new Action();
        clock = new timeClock(this);
        journal = new pandaJournal(this, name);
    }

    protected void nextAction() {
        previousAction = currentAction;
        currentAction = new Action();
    }

    public void doPandaStuffForAPeriod(){
        doPandaStuffForDays(clock.SHIFTS_PER_PERIOD);
    }

    private void doPandaStuffForDays(int days) {

        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
        }
        journal.compute();
        journal.print();
    }

    protected void doPandaStuffForADay() {

        if (!clock.endOfDay()) {

            clock.punchClock(false);
            doPandaStuffForADay();
        }
        else {
            clock.punchClock(true);
        }
    }

    public String getPandaName() {
        return journal.NAME;
    }

    public pandaJournal getJournal() {
        return journal;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

}