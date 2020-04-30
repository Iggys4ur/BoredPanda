package BoredPanda;

public class BoredPanda {

    private final Journal JOURNAL;
    protected TimeClock Clock;
    protected Action currentAction, previousAction;
    public PandaTribe Tribe;
    public final BoredPanda MOTHER, FATHER;

    protected final String NAME;
    protected boolean isChief,
                      isElder;

    protected BoredPanda(PandaTribe tribe) {this(tribe,null,null);}

    protected BoredPanda(PandaTribe tribe, BoredPanda mother, BoredPanda father) {
        NAME = tribe.randomName();
        MOTHER = mother;
        FATHER = father;
        Tribe = tribe;
        JOURNAL = new Journal(this);
        Clock = new TimeClock(this);

    }

    protected void nextAction() {
        previousAction = currentAction;
        JOURNAL.ACTIVITY_HISTORY.put(previousAction.activity, previousAction);
        currentAction = new Action(this);
    }

    protected void doPandaStuffForPeriods(int periods) {
        for (int i = 0; i < periods; i++) {
            doPandaStuffForAPeriod();
        }
    }

    protected void doPandaStuffForAPeriod() {
        doPandaStuffForDays(Clock.SHIFTS_PER_PERIOD);
    }

    protected void doPandaStuffForDays(int days) {
        for (int i = 0; i < days; i++) {

            doPandaStuffForADay();
        }
        JOURNAL.compute();
    }

    protected void doPandaStuffForADay() {
        Clock.punch();
        if (!Clock.endOfDay) doPandaStuffForADay();
    }

    public Journal getJournal() {
        return JOURNAL;
    }

    public void setTribe(PandaTribe tribe) {
        Tribe = tribe;
    }

    public boolean hasParents() {return MOTHER != null && FATHER != null;}

}