package BoredPanda;

public class BoredPanda {

    private final Journal JOURNAL;
    protected TimeClock Clock = new TimeClock(this);
    protected Action currentAction, previousAction;
    public PandaTribe Tribe;

    protected final String NAME;
    protected boolean isChief;

    public BoredPanda(String name, PandaTribe tribe) {
        NAME = name;
        JOURNAL = new Journal(this);
        Tribe = tribe;
    }

    public BoredPanda(String name, String tribeName)
    {
        NAME = name;
        JOURNAL = new Journal(this);
        Tribe = new PandaTribe(this, tribeName);
    }

    protected void nextAction() {
        previousAction = currentAction;
        JOURNAL.ACTIVITY_HISTORY.add(previousAction);
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

    public void doPandaStuffForDays(int days) {
        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
        }
        JOURNAL.compute();
    }

    protected void doPandaStuffForADay() {
        Clock.punch();
        if(!Clock.endOfDay) doPandaStuffForADay();
    }

    public Journal getJournal() {
        return JOURNAL;
    }

}