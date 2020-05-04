package BoredPanda;

import BoredPanda.enums.Activity;

public class BoredPanda {

    private final Journal JOURNAL;
    protected final TimeClock CLOCK;
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
        CLOCK = new TimeClock(this);
        currentAction = new Action(this, 0L);
        previousAction = new Action(this, Activity.SLEEP, 480L);
    }

    protected void nextAction() {
        previousAction = currentAction;
        currentAction = new Action(this);
    }

    public void doPandaStuffForPeriods(int periods) {
        for (int i = 0; i < periods; i++) {
            doPandaStuffForAPeriod();
        }
    }

    protected void doPandaStuffForAPeriod() {
        doPandaStuffForDays(CLOCK.SHIFTS_PER_PERIOD);
    }

    protected void doPandaStuffForDays(int days)
    {
        for (int i = 0; i < days; i++)
        {
            doPandaStuffForADay();
        }
    }

    protected void doPandaStuffForADay() {
        CLOCK.punch();
        if (!CLOCK.endOfShift) doPandaStuffForADay();
    }

    public Journal getJournal() {
        return JOURNAL;
    }

    public void setTribe(PandaTribe tribe) {
        Tribe = tribe;
    }

    public boolean hasParents() {return MOTHER != null && FATHER != null;}

    public void print()
    {
        JOURNAL.printStats();
        CLOCK.printTotals();
    }

    @Override
    public String toString(){
        return NAME;
    }

}