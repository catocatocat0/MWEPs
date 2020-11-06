package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.*;

public class TrackerExecutor extends ExecutorFormat {

    TrackerExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist) {
        super(plugin, sender, mwep, usecase, mweplist);
    }

    @Override
    public void parseData() {
        ArrayList<String> id = new ArrayList<>();
        id.add("Track.Type");
        id.add("Track.Range");
        id.add("Track.Ray.Particle");
        id.add("Track.Ray.Range");
        id.add("Track.Ray.Density");

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

        //parse track type
        cases = id.get(0);
        if(toBeParsed.containsKey(cases)) {
            try {

                switch (toBeParsed.get(cases)) {
                    case "ALL":
                        data.put(cases, "ALL");
                        break;

                    case "LIVING":
                        data.put(cases, "LIVING");
                        break;

                    case "NOTLIVING":
                        data.put(cases, "NOTLIVING");
                        break;

                    case "ANIMALS":
                        data.put(cases, "ANIMALS");
                        break;

                    case "MONSTERS":
                        data.put(cases, "MONSTERS");
                        break;

                    case "PROJECTILE":
                        data.put(cases, "PROJECTILE");
                        break;

                    case "HVZ":
                        data.put(cases, "HVZ");
                        break;

                    default:
                        data.put(cases,
                                EntityType.valueOf(toBeParsed.get(cases)));
                        break;
                }

            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse range
        cases = id.get(1);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse ray type
        cases = id.get(2);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Particle.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else{
            data.put(cases, null);
        }

        //parse ray range
        cases = id.get(3);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else{
            data.put(cases, 0D);
        }

        //parse ray density
        cases = id.get(4);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid "+cases);
                enable = false;
            }
        }else {
            data.put(cases, 1);
        }

        //sends the parsed data and executes it
        if (enable) {
            executeMwep(data);
        } else {
            data.clear();
        }

        //clears the data getting hashmap
        toBeParsed.clear();
    }

    @Override
    public void executeMwep(HashMap<String, Object> data) {
        //initialize variables
        double trackRange = (double) data.get("Track.Range");
        double rayRange = (double) data.get("Track.Ray.Range");
        int rayDensity = (int) data.get("Track.Ray.Density");

        //collects nearby entities
        Collection<Entity> collection = sender.getWorld().getNearbyEntities(sender.getLocation(), trackRange, trackRange, trackRange);
        if(collection.size()>0) {
            //filter out entities
            switch (data.get("Track.Type").toString()) {
                case "ALL":
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "LIVING":
                    collection.removeIf((e) -> !(e instanceof LivingEntity));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "NOTLIVING":
                    collection.removeIf((e) -> (e instanceof LivingEntity));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "ANIMALS":
                    collection.removeIf((e) -> !(e instanceof Animals));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "MONSTERS":
                    collection.removeIf((e) -> !(e instanceof Monster));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "PROJECTILE":
                    collection.removeIf((e) -> !(e instanceof Projectile));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                case "HVZ":
                    collection.removeIf((e) -> !(e instanceof Player));
                    collection.removeIf((e) -> !e.getScoreboardTags().contains("HVZHuman"));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;

                default:
                    collection.removeIf((e) -> !(e.getType().equals(data.get("Track.Type"))));
                    collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
                    break;
            }

            //sorts the collection
            ArrayList<Entity> entitys = new ArrayList<>(collection);
            Collections.sort(entitys, (e1, e2) -> (int) (e1.getLocation().distanceSquared(sender.getLocation()) - e2.getLocation().distanceSquared(sender.getLocation())));
            if(entitys.size()>0) {
                Entity entity = entitys.get(0);

                //traces a ray
                if (data.get("Track.Ray.Particle") != null && rayDensity > 0) {
                    double eyeHeight = 0;
                    if (entity instanceof LivingEntity) {
                        eyeHeight = ((LivingEntity) entity).getEyeHeight();
                    }
                    Particle ray = (Particle) data.get("Track.Ray.Particle");
                    Location eloc = entity.getLocation().add(0, eyeHeight / 2, 0);
                    Location ploc = sender.getLocation().add(0, 1.0, 0);

                    Vector dloc = ((eloc.subtract(ploc).toVector()).normalize()).multiply(rayRange);

                    double dx = (dloc.getX()) / (rayRange * rayDensity);
                    double dy = (dloc.getY()) / (rayRange * rayDensity);
                    double dz = (dloc.getZ()) / (rayRange * rayDensity);

                    for (int v = 0; v < rayRange * rayDensity; v++) {
                        ploc = ploc.add(dx, dy, dz);
                        sender.getWorld().spawnParticle(ray, ploc, 1);
                    }
                }

                //messages the player
                sender.sendMessage(ChatColor.GREEN + "Located: " + ChatColor.GOLD + entity.getName() + ChatColor.WHITE + " at " + ChatColor.GOLD +
                        (int) entity.getLocation().getX() + ", " + (int) entity.getLocation().getY() + ", " + (int) entity.getLocation().getZ() + ", ");
            }else{
                sender.sendMessage(ChatColor.RED+"Nothing was found.");
            }
        }else{
            sender.sendMessage(ChatColor.RED+"Nothing was found.");
        }
    }
}