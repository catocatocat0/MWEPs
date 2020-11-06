package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import java.util.ArrayList;
import java.util.HashMap;

public class SoundExecutor extends ExecutorFormat{

    SoundExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist){
        super(plugin, sender, mwep, usecase, mweplist);
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Sound.Name");
        id.add("Sound.Volume");
        id.add("Sound.Pitch");

        //gathers all the relevant data to the weapon
        HashMap<String, String> toBeParsed = new HashMap<>();

        for (String aCase : id) {
            if (mweplist.contains(mwep + "." + usecase + "." + aCase)) {
                toBeParsed.put(aCase, mweplist.getString(mwep + "." + usecase + "." + aCase));
            }
        }

        //creates a data hashmap that's to be sent to the execution method
        HashMap<String, Object> data = new HashMap<>();

        //creates a path id for parsing the data and an enable/disable switch
        String cases;
        boolean enable = true;

        //parse spawn entity
        cases = id.get(0);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Sound.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse sound volume
        cases = id.get(1);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (float) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse sound pitch
        cases = id.get(2);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (float) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else{
            data.put(cases, 1F);

        }

        //sends the parsed data and executes it
        if(enable) {
            executeMwep(data);
        }else{
            data.clear();
        }

        //clears the data getting hashmap
        toBeParsed.clear();
    }

    @Override
    public void executeMwep(HashMap<String, Object> data) {
        //initializes variables
        Sound name = (Sound) data.get("Sound.Name");
        float volume = (float) data.get("Sound.Volume");
        float pitch = (float) data.get("Sound.Pitch");

        //play sound
        sender.getWorld().playSound(sender.getLocation(), name, volume, pitch);
        data.clear();
    }
}
