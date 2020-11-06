package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class LaunchExecutor extends ExecutorFormat{

    LaunchExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist){
        super(plugin, sender, mwep, usecase, mweplist);
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Launch.Velocity");

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

        //parse launch velocity
        cases = id.get(0);
        try {
            data.put(cases,
                    Double.parseDouble(toBeParsed.get(cases)));
        }catch (Exception e){
            sender.sendMessage(ChatColor.RED+"Invalid "+cases);
            enable = false;
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
        double velocity = (double) data.get("Launch.Velocity");

        LivingEntity ply = sender;
        Vector direction = (ply.getEyeLocation().getDirection()).normalize();
        ply.setVelocity(ply.getVelocity().add(direction.multiply(velocity)));

        data.clear();
    }
}
