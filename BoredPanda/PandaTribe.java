package BoredPanda;

import BoredPanda.enums.Attribute;
import BoredPanda.enums.Role;
import BoredPanda.enums.Sex;
import BoredPanda.enums.Stat;
import BoredPanda.util.RandomNames;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PandaTribe {

    public short Level = 1,
                maxSize = 5,
                shiftsLeft = 0,
                periodsLeft = 0;
    public long Experience = 0,
                Coins = 100; //TODO
    public String NAME;
    public BoredPanda Chieftain;
    public final List<String> names = new RandomNames().randomNames;
    public final ConcurrentHashMap<Short, BoredPanda> TRIBE;

    public PandaTribe(String TribeName)
    {
        NAME = TribeName;
        TRIBE = new ConcurrentHashMap<Short, BoredPanda>(maxSize);
        Chieftain = new BoredPanda(this);
        Chieftain.setRole(Role.CHIEFTAIN);
        TRIBE.put((short) 0, Chieftain);
        for (long tribeSize = 1; tribeSize < maxSize; tribeSize++)
        {
            {
                BoredPanda panda = new BoredPanda(this);
                panda.setRole(Role.ELDER);
                TRIBE.put((short) size(), panda);
            }
        }
    }

    public void executeOrdersForPeriods(short periods){
        periodsLeft = periods;
        ArrayList<BoredPanda> tribe = new ArrayList<>(TRIBE.values());
        for (BoredPanda panda : tribe)
        {
            panda.doPandaStuffForPeriods(periods);
        }
    }

    public void executeOrdersForAShift(){
        for (int i = 0; i < maxSize; i++) {
            BoredPanda panda = TRIBE.get(i);
            panda.doPandaStuffForAShift();
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
        if (Level < 10) // from level one to ten add a panda every level
        {
            maxSize++;
            for(BoredPanda panda : breedPandas()) panda.doPandaStuffForPeriods(periodsLeft);

        }
        else if(Level > 10 && Level < 25) //from level 11 - 24 add a panda every 3 levels
        {
            if(Level % 3 == 0)
            {
                maxSize++;
                for(BoredPanda panda : breedPandas()) panda.doPandaStuffForPeriods(periodsLeft);
            }
        }
        else // level 25+ add a panda every 5 levels
        {
            if(Level % 5 == 0)
            {
                maxSize++;
                for(BoredPanda panda : breedPandas()) panda.doPandaStuffForPeriods(periodsLeft);
            }
        }
    }


    //TODO implement multiple challenge stats
    public void challengeChieftain(BoredPanda challenger, Stat ... challenges) // challenger challenges Chieftain based on challenge level
    {
        byte successfulChallenges = 0;

        if (Chieftain != null)
        {
            for(byte i = 0; i < challenges.length; i++)
            {
                long challengerStat = challenger.getJournal().getLevel(challenges[i]),
                        chieftainStat = Chieftain.getJournal().getLevel(challenges[i]);
                if (challengerStat > chieftainStat) successfulChallenges++;
            }
            //put chieftain on the bottom of the totem pole + set role to member
            if (successfulChallenges > 3) {setNewChieftain(challenger);}
            else banishPanda(challenger); // banish unsuccessful challenger
        }
        else setNewChieftain(challenger);
    }

    public void setNewChieftain(BoredPanda challenger)
    {
        banishPanda(challenger); // remove challenger from tribe before setting as chieftain to prevent duplicates
        TRIBE.put((short) 0, challenger);
        if(Chieftain != null){
            TRIBE.put((short) TRIBE.size(), Chieftain);
            Chieftain.setRole(Role.MEMBER);
            Chieftain = challenger;
        }
        challenger.setRole(Role.CHIEFTAIN);
    }

    private void banishPanda(BoredPanda toBanish)
    {
        ArrayList<BoredPanda> tribe = new ArrayList<BoredPanda>(TRIBE.values());
        tribe.remove(toBanish);
        TRIBE.clear();
        for (BoredPanda panda : tribe)
        {
            TRIBE.put((short) size(), panda);
        }
    }

    public String randomName(){
        String name = names.get(new Random().nextInt(names.size()));
        names.remove(name);
        return name;
    }

    public int size(){
        return TRIBE.size();
    }

    public void print()
    {
        for (short panda = 0; panda < TRIBE.size(); panda++)
        {
            TRIBE.get(panda).print();
        }

    }

    public void printStats()
    {
        System.out.println();
        for (short panda = 0; panda < TRIBE.size(); panda++)
        {
            TRIBE.get(panda).getJournal().printStats();
        }
    }

    void decrementPeriodsLeft()
    {
        periodsLeft--;
    }

    void decrementShiftsLeft()
    {
        shiftsLeft--;
    }

    public long nextLevelAt(){
        return (long) Math.pow(((Level+1)/0.04), 2);
    }

    public List<BoredPanda> breedPandas() {
        List<BoredPanda> children = new ArrayList<BoredPanda>();
        Map<Short, BoredPanda> m = TRIBE.entrySet()
                .stream()
                .filter(p -> p.getValue().getJournal().SEX == Sex.MALE)
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Short, BoredPanda> f = TRIBE.entrySet()
                .stream()
                .filter(p -> p.getValue().getJournal().SEX == Sex.FEMALE)
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

        ArrayList<BoredPanda> males = new ArrayList<BoredPanda>(m.values());
        ArrayList<BoredPanda> females = new ArrayList<BoredPanda>(f.values());
        byte attempts = 0;
        while (!males.isEmpty() && !females.isEmpty() && TRIBE.size() < maxSize && attempts <= 3 * TRIBE.size()) {
            BoredPanda mother = females.get(new Random().nextInt(females.size()));
            BoredPanda father = males.get(new Random().nextInt(males.size()));
            byte fertility = (byte)(mother.getJournal().getAttribute(Attribute.FERTILITY) + father.getJournal().getAttribute(Attribute.FERTILITY));
            byte birthChance = fertility == 0 ? 0 : (byte) new Random().nextInt(fertility);
            if (birthChance >= 7)
            {
                BoredPanda child = new BoredPanda(this, mother, father);
                TRIBE.put((short) size(), child);
                father.addOffspring(child);
                mother.addOffspring(child);
                males.remove(father);
                females.remove(mother);
                children.add(child);
            } else attempts++;
        }
        return children;
    }

    @Override
    public String toString(){
        StringBuilder statement = new StringBuilder("[" + NAME + "] ");
        List<BoredPanda> tribe = new ArrayList<BoredPanda>(TRIBE.values());
        for (BoredPanda panda : tribe)
        {
            statement.append(panda + " // ");
        }
        return statement.toString();
    }

}
