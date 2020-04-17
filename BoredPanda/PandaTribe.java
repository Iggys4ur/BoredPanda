package BoredPanda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PandaTribe {

    public short maxSize = 3;
    public short Level = 1;
    public long Experience = 0;
    public String NAME;
    public BoredPanda Chieftan;
    public List<BoredPanda> tribe = new ArrayList<>(maxSize);
    public List<String> names = new enumeratedPandas().randomNames();

    public PandaTribe(BoredPanda chief, String tribeName)
    {
        Chieftan = chief;
        NAME = tribeName;
        for (int i = 0; i < maxSize; i++) {
            if(i == 0) tribe.add(Chieftan);
            else tribe.add(new BoredPanda(names.get(new Random().nextInt(names.size())), this));
        }
    }

    public void addExperience(Integer exp){
        for (int i = 0; i < exp; i++) {
            Experience += 1;
            if(0.04 * Math.sqrt(Experience) % 1 == 0) LevelUp();
        }
    }

    public void LevelUp(){
        Level = (short) (0.04 * Math.sqrt(Experience));
        if (Level % 5 == 0){
            List<BoredPanda> foo = new ArrayList<>(++maxSize);
            foo.addAll(tribe);
            tribe = foo;
            recruitNewPanda();
        }
    }

    public void challengeChieftan(BoredPanda challenger)
    {
        if(challenger.getJournal().SIZE > Chieftan.getJournal().SIZE){
            newChieftan(challenger);
        }
        else banishPanda(challenger);
    }

    public void newChieftan(BoredPanda challenger){
        Chieftan = challenger;
    }

    public void recruitNewPanda(){
        if (tribe.size() < maxSize) tribe.add(new BoredPanda(names.get(new Random().nextInt(names.size())), this));
    }

    public void banishPanda(BoredPanda toBanish) {
        tribe.remove(toBanish);
    }

}
