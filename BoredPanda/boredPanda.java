package BoredPanda;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class boredPanda {

    private final pandaJournal journal;
    private final String pandaName;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private Action currentAction;

    public boredPanda(String name) {
        pandaName = name;
        journal = new pandaJournal(this, scheduler);
        currentAction = new Action().setFields("INITIALIZATION ACTIVITY", null, 1000);
    }

    protected void nextAction(){
        currentAction = currentAction.choose(null);
    }

    public void doPandaStuffForAPeriod(){
        doPandaStuffForDays(journal.SHIFTS_PER_PERIOD);
    }

    private void doPandaStuffForDays(int days) {

        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
            journal.submitClock();
        }
        journal.compute();
        journal.print();
    }

    protected void doPandaStuffForADay() {

        if (!journal.dayEnded()) {

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