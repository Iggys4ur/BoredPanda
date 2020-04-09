package BoredPanda;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class enumeratedPandas {

    public enumeratedPandas(){
        NAMES();
    }


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

    public enum Names{

    }

    public enum Quality{

    }

    public enum Luck {

    }

    public final List<String> NAMES()
    {
        List<String> statement = new ArrayList<String>();

        try(BufferedReader br = new BufferedReader(new FileReader("D:\\Users\\eswai\\Documents\\BoredPanda01a\\names.txt"))){
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
