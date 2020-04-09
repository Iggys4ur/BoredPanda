package BoredPanda;


import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class enumeratedPandas {

    public enum Activity{
        SLEEP,
        EAT_BAMBOO,
        CLIMB_TREES,
        SWIM,
        PLAY_ROCKS,
        BEES_HONEY,
        DROOL,
        VILLAGER_TERRORIZE,
        GROWL_BIRDS,
        SLASH_TREES,
        VILLAGER_EAT
    }

    public enum Quality{

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
