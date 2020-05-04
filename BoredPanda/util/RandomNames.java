package BoredPanda.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RandomNames {

    public final List<String> randomNames = randomNames();

    private final List<String> randomNames()
    {
        if(randomNames != null) return randomNames;

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
