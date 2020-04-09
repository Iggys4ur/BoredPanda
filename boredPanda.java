package BoredPanda;

public class boredPanda {

    private final pandaJournal journal;
    protected final timeClock clock;
    protected Action currentAction, previousAction;

    public boredPanda(String name) {
        journal = new pandaJournal(this, name);
        clock = new timeClock(this);
    }

    protected void nextAction() {
        previousAction = currentAction;
        journal.activityHistory.add(previousAction);
        currentAction = new Action(this);
    }

    public void doPandaStuffForPeriods(int days) {

        for (int i = 0; i < days; i++) {
            doPandaStuffForAPeriod();
        }
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

        if(!clock.endOfDay){
            clock.punch();
            doPandaStuffForADay();
        }
        else clock.punch();
    }

    public pandaJournal getJournal() {
        return journal;
    }


}