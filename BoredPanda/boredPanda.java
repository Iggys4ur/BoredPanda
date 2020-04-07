package BoredPanda;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class boredPanda {

    private final pandaJournal journal;
    private final String pandaName;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected Action currentAction, previousAction;

    public boredPanda(String name) {
        pandaName = name;
        previousAction = new Action().setFields("INITIALIZATION ACTIVITY", null, 1000);
        currentAction = new Action();
        journal = new pandaJournal(this, scheduler);
    }

    protected void nextAction() {
        previousAction = currentAction;
        currentAction = new Action();
    }

    public void doPandaStuffForAPeriod(){
        doPandaStuffForDays(journal.SHIFTS_PER_PERIOD);
    }

    private void doPandaStuffForDays(int days) {

        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
            if(journal.endOfPeriod()) journal.submitClock();
        }
        journal.compute();
        journal.print();
    }

    protected void doPandaStuffForADay() {

        if (!journal.endOfDay()) {

            journal.punchClock(false);
            doPandaStuffForADay();
        }
        else {
            journal.punchClock(true);
        }
    }

    public String getPandaName() {
        return pandaName;
    }

    public pandaJournal getJournal() {
        return journal;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

}