package BoredPanda;

public class BoredPanda {

    private final Journal Journal;
    protected TimeClock Clock = new TimeClock(this);
    protected Action currentAction, previousAction;
    public PandaTribe Tribe;
    protected final String NAME;

    public BoredPanda(String name, PandaTribe tribe) {
        NAME = name;
        Journal = new Journal(this);
        Tribe = tribe;
    }

    public BoredPanda(String name, String tribeName)
    {
        NAME = name;
        Journal = new Journal(this);
        Tribe = new PandaTribe(this, tribeName);
    }

    protected void nextAction() {
        previousAction = currentAction;
        Journal.activityHistory.add(previousAction);
        currentAction = new Action(this);
    }

    public void doPandaStuffForPeriods(int periods) {

        for (int i = 0; i < periods; i++) {
            doPandaStuffForAPeriod();
        }
    }

    public void doPandaStuffForAPeriod(){
        doPandaStuffForDays(Clock.SHIFTS_PER_PERIOD);
    }

    private void doPandaStuffForDays(int days) {

        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
        }
        Journal.compute();
    }

    protected void doPandaStuffForADay() {

        if(!Clock.endOfDay){
            Clock.punch();
            doPandaStuffForADay();
        }
        else Clock.punch();
    }

    public Journal getJournal() {
        return Journal;
    }

}