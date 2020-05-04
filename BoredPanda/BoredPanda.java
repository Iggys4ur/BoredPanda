package BoredPanda;

import BoredPanda.enums.Activity;
import BoredPanda.enums.Role;
import BoredPanda.enums.Stat;

import java.util.concurrent.ConcurrentHashMap;

public class BoredPanda {

    private final Journal JOURNAL;
    protected final TimeClock CLOCK;
    protected Action currentAction, previousAction;
    public PandaTribe Tribe;
    public final BoredPanda MOTHER, FATHER;
    private final ConcurrentHashMap<Long, BoredPanda> OFFSPRING;

    protected final String NAME;
    protected Role TribeRole;
    protected boolean isChief,
                      isElder;

    protected BoredPanda(PandaTribe tribe) {this(tribe,null,null, null);} // panda without parents, if they're null we know they're the ultimate antecedents.
    protected BoredPanda(PandaTribe tribe, BoredPanda mother, BoredPanda father) {this(tribe, mother, father, null);} // panda with parents

    private BoredPanda(PandaTribe tribe, BoredPanda mother, BoredPanda father, Role role) {
        NAME = tribe.randomName();
        MOTHER = mother;
        FATHER = father;
        Tribe = tribe;
        setRole(Role.MEMBER);
        JOURNAL = new Journal(this);
        CLOCK = new TimeClock(this);
        OFFSPRING = new ConcurrentHashMap<Long, BoredPanda>();
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

    private void doPandaStuffForAPeriod() {
        doPandaStuffForShifts(CLOCK.SHIFTS_PER_PERIOD);
    }

    private void doPandaStuffForShifts(int shifts)
    {
        for (int i = 0; i < shifts; i++)
        {
            doPandaStuffForAShift();
        }
    }

    void doPandaStuffForAShift() {
        punchClock();
        if (shiftNotEnded()) doPandaStuffForAShift();
    }

    void addExperience(long exp, Stat stat)
    {
        JOURNAL.addExperience(exp, stat);
    }

    public Journal getJournal() {
        return JOURNAL;
    }

    public void setTribe(PandaTribe tribe) {
        Tribe = tribe;
        setRole(Role.MEMBER);
    }

    public boolean hasParents() {return MOTHER != null && FATHER != null;}

    public boolean hasOffspring() {return !OFFSPRING.isEmpty();}

    void addOffspring(BoredPanda offspring)
    {
        OFFSPRING.put((long) OFFSPRING.size(), offspring);
    }

    void setRole(Role role)
    {
        TribeRole = role == null ? Role.MEMBER : role;
        switch (TribeRole)
        {
            case CHIEFTAIN:
            {
                isChief = true;
                isElder = true;
                break;
            }
            case ELDER:
            {
                isChief = false;
                isElder = true;
                break;
            }
            case MEMBER:
            {
                isChief = false;
                isElder = false;
                break;
            }
            default: break;
        }
    }

    private void punchClock()
    {
        CLOCK.punch();
    }

    private boolean shiftNotEnded()
    {
        return !CLOCK.isEndOfShift();
    }

    public void print()
    {
        System.out.println("------------------------------------");
        System.out.println(this);
        JOURNAL.printAttributes();
        JOURNAL.printStats();
        CLOCK.printTotals();
    }
    
    @Override
    public String toString(){
        return TribeRole.name() + " | " + NAME + " (" + JOURNAL.SEX.toString().charAt(0) + ") | Lv. " + JOURNAL.Tier;
    }

}