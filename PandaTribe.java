package BoredPanda;

import BoredPanda.enums.Sex;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PandaTribe {

    public short Level = 1,
                maxSize = 5,
                daysLeft = 0,
                periodsLeft = 0;
    public long Experience = 0,
                Coins = 100;
    public String NAME;
    public BoredPanda Chieftain;
    public final List<String> names = new RandomNames().randomNames;
    public final ConcurrentHashMap<Integer, BoredPanda> TRIBE;

    public PandaTribe(String TribeName)
    {
        NAME = TribeName;
        newChieftain(new BoredPanda(this));
        TRIBE = new ConcurrentHashMap<Integer, BoredPanda>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            if(i == 0) TRIBE.put(i, Chieftain);
            else
            {
                BoredPanda panda = new BoredPanda(this);
                panda.isElder = true;
                TRIBE.put(i, panda);
            }
        }
    }

    public void executeOrdersForPeriods(short periods){
        periodsLeft = periods;
        daysLeft = (short) (periodsLeft * Chieftain.Clock.SHIFTS_PER_PERIOD);
        ArrayList<BoredPanda> t = new ArrayList<BoredPanda>(TRIBE.values());
        byte count = 0;
        for (BoredPanda panda : t)
        {
            if(count < 5) panda.doPandaStuffForPeriods(periods);
            else panda.doPandaStuffForPeriods(periodsLeft);
            count++;
        }
    }
    public void executeOrdersForADay(){
        for (int i = 0; i < maxSize; i++) {
            BoredPanda panda = TRIBE.get(i);
            panda.doPandaStuffForADay();
        }
    }

    public void addExperience(long exp){
        for (int i = 0; i < exp; i++) {
            Experience += 1;
            if(0.04 * Math.sqrt(Experience) % 1 == 0) LevelUp();
        }
    }

    public void LevelUp(){
        Level = (short) (0.04 * Math.sqrt(Experience));
        if (Level < 10)
        {
            maxSize++;
            breedPandas();
            TRIBE.get(size() - 1).doPandaStuffForDays(daysLeft);
        }
        else if(Level > 10 && Level < 25)
        {
            if(Level % 3 == 0)
            {
                maxSize++;
                breedPandas();
                TRIBE.get(size() - 1).doPandaStuffForDays(daysLeft);
            }
        }
        else
        {
            if(Level % 5 == 0)
            {
                maxSize++;
                breedPandas();
                TRIBE.get(size() - 1).doPandaStuffForDays(daysLeft);
            }
        }
    }

    public void challengeChieftain(BoredPanda challenger)
    {
        if(challenger.getJournal().SIZE > Chieftain.getJournal().SIZE){
            newChieftain(challenger);
        }
        else banishPanda(challenger);
    }

    public void newChieftain(BoredPanda challenger)
    {
        if(Chieftain == null) Chieftain = challenger;
        else
        {
            TRIBE.replace(0, challenger);
            TRIBE.put(TRIBE.size(), Chieftain);
            Chieftain = challenger;
        }
        challenger.isElder = true; // set elder flag
        challenger.isChief = true; // set Chieftain flag
    }

    public void banishPanda(BoredPanda toBanish) {
        TRIBE.entrySet().stream().filter(p -> p.getValue().equals(toBanish)).iterator().remove();
    }

    public String randomName(){

        return names.get(new Random().nextInt(names.size()));
    }

    public int size(){
        return TRIBE.size();
    }

    public void print()
    {
        System.out.println();
        for (int i = 0; i < TRIBE.size(); i++)
        {
            TRIBE.get(i).getJournal().print();
        }
    }

    public void printStats()
    {
        System.out.println();
        for (int i = 0; i < TRIBE.size(); i++)
        {
            TRIBE.get(i).getJournal().printStats();
        }
    }

    public long nextLevelAt(){
        return (long) Math.pow(((Level+1)/0.04), 2);
    }

    public void breedPandas() {
        Map<Integer, BoredPanda> m = TRIBE.entrySet()
                .stream()
                .filter(p -> p.getValue().getJournal().SEX == Sex.MALE)
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Integer, BoredPanda> f = TRIBE.entrySet()
                .stream()
                .filter(p -> p.getValue().getJournal().SEX == Sex.FEMALE)
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

        ArrayList<BoredPanda> males = new ArrayList<BoredPanda>(m.values());
        ArrayList<BoredPanda> females = new ArrayList<BoredPanda>(f.values());
        byte attempts = 0;
        while (!males.isEmpty() && !females.isEmpty() && TRIBE.size() < maxSize && attempts <= 3 * TRIBE.size()) {
            BoredPanda female = females.get(new Random().nextInt(females.size()));
            BoredPanda male = males.get(new Random().nextInt(males.size()));
            byte fertility = (byte)(male.getJournal().FERTILITY + female.getJournal().FERTILITY);
            byte birthChance = fertility == 0 ? 0 : (byte) new Random().nextInt(fertility);
            if (birthChance >= 7)
            {
                TRIBE.put(size(), new BoredPanda(this, female, male));
                males.remove(male);
                females.remove(female);
            } else attempts++;
        }
    }

    @Override
    public String toString(){
        String statement = "TRIBE: " + NAME + " LED BY: ";
        for (int i = 0; i < TRIBE.size(); i++) {
            BoredPanda panda = TRIBE.get(i);
            statement += panda.NAME + " " + panda.getJournal().SEX + " " + panda.getJournal().totalLevel + " / ";
        }
        return statement;
    }

}
