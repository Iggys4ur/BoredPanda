package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PandaTribe {

    public short maxSize = 3,
            Level = 1,
            daysLeft = 0,
            periodsLeft = 0;
    public long Experience = 0,
                Coins = 100;
    public String NAME;
    public BoredPanda Chieftain;
    public final List<BoredPanda> TRIBE;
    public List<String> names = new enumeratedPandas().randomNames();

    public PandaTribe(BoredPanda chief, String TRIBEName)
    {
        newChieftain(chief);
        NAME = TRIBEName;
        TRIBE = new ArrayList<>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            if(i == 0) TRIBE.add(Chieftain);
            else TRIBE.add(new BoredPanda(names.get(new Random().nextInt(names.size())), this));
        }
    }

    //TODO +100 TRIBE XP every time a panda levels up

    public void executeOrdersForPeriods(byte periods){
        for (BoredPanda panda : TRIBE)
        {
            panda.doPandaStuffForPeriods(periods);
        }
    }
    public void executeOrdersForAPeriod(){
        for (BoredPanda panda : TRIBE)
        {
            panda.doPandaStuffForAPeriod();
        }
    }
    public void executeOrdersForADay(){
        for (BoredPanda panda : TRIBE) {panda.doPandaStuffForADay();}
    }

    public void addExperience(long exp){
        for (int i = 0; i < exp; i++) {
            Experience += 1;
            if(0.04 * Math.sqrt(Experience) % 1 == 0) LevelUp();
        }
    }

    public void LevelUp(){
        Level = (short) (0.04 * Math.sqrt(Experience));
        if (Level % 5 == 0){
            maxSize++;
            recruitNewPanda();
        }
    }

    public void challengeChieftain(BoredPanda challenger)
    {
        if(challenger.getJournal().SIZE > Chieftain.getJournal().SIZE){
            newChieftain(challenger);
        }
        else banishPanda(challenger);
    }

    public void newChieftain(BoredPanda challenger){
        if(Chieftain == null)
        {
            Chieftain = challenger;
        }
        else
        {
            TRIBE.remove(Chieftain); // remove Chief from tribe
            TRIBE.set(0, challenger); // add challenger back at chieftain (index = 0)
            Chieftain = challenger; // make challenger Chieftain
        }
        challenger.isChief = true; // set Chieftain flag

    }

    public BoredPanda recruitNewPanda(){
        if (TRIBE.size() < maxSize)
        {
            BoredPanda panda = new BoredPanda(names.get(new Random().nextInt(names.size())), this);
            TRIBE.add(panda);
            return panda;
        }
        else return null;
    }

    public void banishPanda(BoredPanda toBanish) {
        TRIBE.remove(toBanish);
    }

    public void print()
    {
        Chieftain.getJournal().print();
        for (BoredPanda panda : TRIBE)
        {
            if (!panda.equals(Chieftain)) panda.getJournal().print();
        }
    }
    public void printStats()
    {
        Chieftain.getJournal().printStats();
        for (BoredPanda panda : TRIBE)
        {
            if (!panda.equals(Chieftain)) panda.getJournal().printStats();
        }
    }

}
