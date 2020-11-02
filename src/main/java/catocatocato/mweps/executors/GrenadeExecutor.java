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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class GrenadeExecutor extends ExecutorFormat{

    private final MwepsMain plugin;
    private final Entity sender;
    private final Player psender;
    private final String mwep;
    private final String usecase;
    private final FileConfiguration mweplist;

    GrenadeExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist, Player player){
        this.plugin = plugin;
        this.sender = sender;
        this.mwep = mwep;
        this.usecase = usecase;
        this.mweplist = mweplist;
        this.psender = player;
        if(sender.isValid()) {
            this.parseData();
        }
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Spawn.Grenade.Name");
        id.add("Spawn.Grenade.Velocity");
        id.add("Spawn.Grenade.Spread");
        id.add("Spawn.Grenade.Count");
        id.add("Spawn.Grenade.Data");
        id.add("Spawn.Grenade.Explosion.Power");
        id.add("Spawn.Grenade.Explosion.Fire");
        id.add("Spawn.Grenade.Explosion.Break");

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
        boolean explosion;

        //parse spawn entity
        cases = id.get(0);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        EntityType.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else {
            enable = false;
        }

        //parse entity velocity
        cases = id.get(1);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
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
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
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
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else{
            enable = false;
        }

        //parse entity nbt data
        cases = id.get(4);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        toBeParsed.get(cases));
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else{
            data.put(cases, "{}");
        }

        //parse explosion power
        cases = id.get(5);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (long) Double.parseDouble(toBeParsed.get(cases)));
                explosion = true;
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                explosion = false;
            }
        }else {
            explosion = false;
        }

        //parse explosion fire
        cases = id.get(6);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Boolean.parseBoolean(toBeParsed.get(cases)));
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                explosion = false;
            }
        }else {
            data.put(cases, false);
        }

        //parse explosion break blocks
        cases = id.get(7);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Boolean.parseBoolean(toBeParsed.get(cases)));
            } catch (Exception e) {
                psender.sendMessage(ChatColor.RED + "Invalid " + cases);
                explosion = false;
            }
        }else {
            data.put(cases, false);
        }

        //sends the parsed data and executes it
        if(enable) {
            executeMwep(data);
        }

        //creates an explosion
        if(explosion) {
            doExplosion(data);
        }

        //clears the data getting hashmap
        toBeParsed.clear();
        data.clear();
    }

    @Override
    public void executeMwep(HashMap<String, Object> data) {
        //gathers initializes variables
        int count = (int) data.get("Spawn.Grenade.Count");
        double speed = (double) data.get("Spawn.Grenade.Velocity");
        double spread = (double) data.get("Spawn.Grenade.Spread");
        String nbtdata = (String) data.get("Spawn.Grenade.Data");
        EntityType name = (EntityType) data.get("Spawn.Grenade.Name");

        //iterates entity spawning based on count
        for (int i = 0; i < count; i++) {

            //determines player position and look vector
            Location ppos = sender.getLocation();
            Entity projectile = ppos.getWorld().spawnEntity(ppos, name);

            //add owner to projectile if enabled in config
            if(projectile instanceof Projectile && plugin.getConfig().getBoolean("Add-Owner-to-Projectiles")){
                ((Projectile) projectile).setShooter(psender);
            }

            //spawns the entity
            projectile.getPersistentDataContainer().set(new NamespacedKey(plugin,"mweps"), PersistentDataType.STRING, "mweps."+sender.getUniqueId()+"."+usecase+"."+mwep);
            try {
                NBTEntity nbt = new NBTEntity(projectile);
                nbt.mergeCompound(new NBTContainer(nbtdata));
            }catch(Exception e){
                psender.sendMessage("Invalid entity NBT data!");
            }

            Vector random = new Vector(Math.random()-.5,Math.random()-.5,Math.random()-.5).multiply(spread);
            projectile.setVelocity(random.multiply(speed));
        }

        sender.remove();
    }

    public void doExplosion(HashMap<String, Object> data){
        //creates an explosion
        long explosionPower = (long) data.get("Spawn.Grenade.Explosion.Power");
        boolean setFire = (boolean) data.get("Spawn.Grenade.Explosion.Fire");
        boolean breakBlocks = (boolean) data.get("Spawn.Grenade.Explosion.Break");
        sender.getWorld().createExplosion(sender.getLocation(),explosionPower,setFire,breakBlocks);
    }
}
