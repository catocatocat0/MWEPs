package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;

public class SpawnExecutor extends ExecutorFormat{

    SpawnExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist){
        super(plugin, sender, mwep, usecase, mweplist);
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Spawn.Name");
        id.add("Spawn.Velocity");
        id.add("Spawn.Spread");
        id.add("Spawn.Count");
        id.add("Spawn.Data");
        id.add("Spawn.Grenade.Timer");

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
                        EntityType.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse entity velocity
        cases = id.get(1);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else {
            data.put(cases, 0D);
        }

        //parse entity spread
        cases = id.get(2);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else {
            data.put(cases, 0D);
        }

        //parse entity count
        cases = id.get(3);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse entity nbt data
        cases = id.get(4);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        toBeParsed.get(cases));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else{
            data.put(cases, "{}");
        }

        //parse grenade timer
        cases = id.get(5);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (long) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else {
            data.put(cases, 0L);
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
        //gathers initializes variables
        int count = (int) data.get("Spawn.Count");
        double speed = (double) data.get("Spawn.Velocity");
        double spread = (double) data.get("Spawn.Spread");
        String nbtdata = (String) data.get("Spawn.Data");
        EntityType name = (EntityType) data.get("Spawn.Name");
        
        //iterates entity spawning based on count
        for (int i = 0; i < count; i++) {

            //determines player position and look vector
            Location ppos = sender.getLocation();
            LivingEntity player = sender;
            Vector direction = (player.getEyeLocation().getDirection());
            Entity projectile = ppos.getWorld().spawnEntity(ppos.add(player.getEyeLocation().getDirection()).add(0, 1.5, 0), name);

            //add owner to projectile if enabled in config
            if(projectile instanceof Projectile && plugin.getConfig().getBoolean("Add-Owner-to-Projectiles")){
                ((Projectile) projectile).setShooter(sender);
            }

            //spawns the entity
            projectile.getPersistentDataContainer().set(new NamespacedKey(plugin,"mweps"), PersistentDataType.STRING, "mweps."+sender.getUniqueId()+"."+usecase+"."+mwep);
            try {
                NBTEntity nbt = new NBTEntity(projectile);
                nbt.mergeCompound(new NBTContainer(nbtdata));
            }catch(Exception e){
                sender.sendMessage("Invalid entity NBT data!");
            }

            //gives the entities spawned a velocity
            if(sender.isSneaking()){
                spread = spread/2;
            }

            Vector random = new Vector(Math.random()-.5,Math.random()-.5,Math.random()-.5).multiply(spread);
            projectile.setVelocity(((direction.normalize()).multiply(speed)).add(random));

            //executes the grenade if values are present
            if(mweplist.getKeys(true).contains(mwep + "." + usecase + ".Spawn.Grenade.Name")){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new GrenadeExecutor(plugin, projectile, mwep, usecase, mweplist, sender);
                    }
                }.runTaskLater(plugin,(long) data.get("Spawn.Grenade.Timer"));
            }
        }
        data.clear();
    }
}
