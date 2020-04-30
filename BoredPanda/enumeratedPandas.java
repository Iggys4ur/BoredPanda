package BoredPanda;


import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class enumeratedPandas {

    public enum Activity{
        INITIALIZATION_ACTIVITY,
        SLEEP,
        EAT_BAMBOO,
        CLIMB_TREES,
        SWIM,
        PLAY_WITH_ROCKS,
        FIGHT_BEES_FOR_HONEY,
        DROOL_ON_THINGS,
        TERRORIZE_VILLAGERS,
        GROWL_AT_BIRDS,
        SLASH_AT_TREES,
        ABDUCT_AND_EAT_A_VILLAGER
    }

    public enum Sex{
        MALE,
        FEMALE
    }

    public enum Stat{
        PHYSIQUE,
        AGILITY,
        CONSTITUTION,
        INTELLECT,
        MAGIC
    }

    public enum Quality{


        MEDIOCRE,
        COMMON,
        UNCOMMON,
        RARE,
        EXCEPTIONAL,
        MYTHIC,
    }

    public enum Size{

    }
    public enum Breed{

    }
    public enum Luck{

    }

    public enum Fertility{

    }

    public final List<String> randomNames()
    {

        URL url = getClass().getResource("names.txt");
        List<String> statement = new ArrayList<String>();

        try(BufferedReader br = new BufferedReader(new FileReader(url.getPath()))){
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null){
                statement.add(sCurrentLine);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return statement;
    }
}
