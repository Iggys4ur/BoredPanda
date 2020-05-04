package BoredPanda;

import BoredPanda.enums.Attribute;
import BoredPanda.enums.Sex;
import BoredPanda.enums.Stat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Journal {

    final BoredPanda PANDA;
    final String NAME;
    final Sex SEX = Math.round(Math.random()) > 0 ? Sex.MALE : Sex.FEMALE;

    //Immutable Attributes (EV STATS)
    private final ConcurrentHashMap<Attribute, Byte> ATTRIBUTES;
    //Stats (Level up with Panda)
    private final ConcurrentHashMap<Stat, Long[]> STATS;
    //Total Level
    short Tier = 0,
        TotalLevels = 0;
    //Total Exp
    long TotalExperience = 0;

    Journal(BoredPanda panda){
        PANDA = panda;
        NAME = panda.NAME;
        ATTRIBUTES = new ConcurrentHashMap<>(Attribute.values().length); // never changed EV STATS
        STATS = new ConcurrentHashMap<>(Stat.values().length); // stats that update with levels
        initATTRIBUTES();
        initSTATS();
    }

    void initATTRIBUTES() {
        byte breed = (byte) (new Random().nextInt(4) + 1),// 5
                quality = (byte) (new Random().nextInt(6 - (5 / (breed))) + breed), // 10
                size = (byte) ((new Random().nextInt((6 - (5 / (breed)) + breed)) / 2) + 1), // 10
                luck = (byte) (new Random().nextInt(11 - (10 / quality))), // 10
                fertility = (byte) (new Random().nextInt(11 - (quality / 2)) + (luck / 10)), // 10
                physique = (byte) (new Random().nextInt(5 + (quality / 2)) + 1 + (luck) + (size)),
                agility = (byte) (new Random().nextInt(5 + (quality / 2)) + 1 + (luck) - (size)),
                intellect = (byte) (new Random().nextInt(5 + (quality / 2)) + 1 + (luck) - (breed / 2)),
                constitution = (byte) (new Random().nextInt(5 + (quality / 2)) + 1 + (luck) - (breed / 2)),
                magic = (byte) (new Random().nextInt(quality + luck));
        byte[] attributes = new byte[]{breed, quality, size, luck, fertility, physique, agility, intellect, constitution, magic};
        for(byte i = 0; i < attributes.length; i++)
        {
            ATTRIBUTES.put(Attribute.values()[i], attributes[i]);
        }
    }

    void initSTATS()
    {
        Long[] initialStats = new Long[]{0L,0L};
        for (Stat stat : Stat.values())
        {
            STATS.put(stat, initialStats);
        }
    }

    //TODO add coins per activity & relevant levels
    void addExperience(long exp, Stat stat){

        for (long i = 0; i < exp; i++) {
            Long[] totalStat = STATS.get(stat);
            totalStat[0] += 1;
            STATS.put(stat, totalStat);
            if(atLevelUp(stat)) {levelUp(stat);}
        }
        TotalExperience += exp;
    }

    private void levelUp(Stat stat)
    {
        Long[] totalStat = STATS.get(stat);
        totalStat[1] += 1;
        STATS.put(stat, totalStat);
        addTotalsAndLevelTribe();
    }

    private void addTotalsAndLevelTribe()
    {
        if(++TotalLevels % 3 == 0)
        {
            Tier++;
            PANDA.Tribe.addExperience(1500);
        }
    }

    private boolean atLevelUp(Stat stat){
        return (0.04 * Math.sqrt(STATS.get(stat)[0]) % 1 == 0);
    }

    void print() {
        System.out.println("------------ [ JOURNAL OF " + PANDA + " ] ------------");
        printAttributes();
        printStats();
        PANDA.CLOCK.printTotals();
    }

    //primarily debug
    void printHeredity(){
        String role = "Member of the ";
        String parents = PANDA.hasParents() ? PANDA.MOTHER.NAME + " and " + PANDA.FATHER.NAME : "";
        String gender = SEX.toString();
        if(PANDA.hasParents()) gender = PANDA.getJournal().SEX == Sex.MALE ? "Son of " + parents : "Daughter of " + parents;
        if(PANDA.isElder) role = "Elder of the ";
        if(PANDA.isChief) role = "Chieftain of the ";
        System.out.println(NAME + " (" + gender + ") | Tier " + Tier + " (Lv." + TotalLevels + " || " + TotalExperience + ") | Age: " + PANDA.CLOCK.age()
                + "\n" + role + PANDA.Tribe.NAME + " Lv(" + PANDA.Tribe.Level + "):[" + PANDA.Tribe.size() + "/" + PANDA.Tribe.maxSize + "] <" + PANDA.Tribe.Experience + "/" + PANDA.Tribe.nextLevelAt() +">");
    }

    void printAttributes()
    {
        StringBuilder sb = new StringBuilder("[ ATTRIBUTES ] : ");
        for (Attribute attribute : Attribute.values())
        {
            sb.append(" [");
            sb.append(attribute.name().charAt(0));
            sb.append("] ");
            sb.append(getAttribute(attribute));
            sb.append(" | ");
        }
        System.out.println("------------------------------------");
        System.out.println(sb);
    }

    void printStats()
    {
        StringBuilder sb = new StringBuilder("[ STATS ] : ");
        for (Stat stat : Stat.values())
        {
            sb.append(" [");
            sb.append(stat.name().charAt(0));
            sb.append("] ");
            sb.append(getLevel(stat));
            sb.append(" | ");
        }
        System.out.println("------------------------------------");
        System.out.println(sb);
        System.out.println("------------------------------------");
    }

    public long getExperience(Stat stat)
    {
        return STATS.get(stat)[0];
    }

    public long getLevel(Stat stat)
    {
        return STATS.get(stat)[1];
    }

    public ConcurrentHashMap<Stat, Long[]> getStats()
    {
        return STATS;
    }

    public byte getAttribute(Attribute attribute)
    {
        return ATTRIBUTES.get(attribute);
    }

    public ConcurrentHashMap<Attribute, Byte> getAttributes()
    {
        return ATTRIBUTES;
    }

}
