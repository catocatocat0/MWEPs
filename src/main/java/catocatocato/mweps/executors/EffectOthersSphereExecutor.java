package catocatocato.mweps.executors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class EffectOthersSphereExecutor extends ExecutorFormat {
    private final Player sender;
    private final String mwep;
    private final String usecase;
    private final FileConfiguration mweplist;

    EffectOthersSphereExecutor(Entity sender, String mwep, String usecase, FileConfiguration mweplist) {
        this.sender = (Player) sender;
        this.mwep = mwep;
        this.usecase = usecase;
        this.mweplist = mweplist;
        this.parseData();
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Effect.Other.Sphere.Type");
        id.add("Effect.Other.Sphere.Amplifier");
        id.add("Effect.Other.Sphere.Duration");
        id.add("Effect.Other.Sphere.Particles");
        id.add("Effect.Other.Sphere.Icon");
        id.add("Effect.Other.Sphere.Radius");
        id.add("Effect.Other.Sphere.Ray.Particle");
        id.add("Effect.Other.Sphere.Ray.Density");
        id.add("Effect.Other.Sphere.Hit.Particle");
        id.add("Effect.Other.Sphere.Hit.Density");
        id.add("Effect.Other.Sphere.Filter.Tag.Exclude");
        id.add("Effect.Other.Sphere.Filter.Tag.Include");

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

        //parse potion type
        cases = id.get(0);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion effects and allows you to spawn multiple
                ArrayList<PotionEffectType> potioneffects = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    potioneffects.add(PotionEffectType.getByName(effect));
                }

                data.put(cases, potioneffects);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse amplifier
        cases = id.get(1);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion amplifiers and allows you to use multiple
                ArrayList<Integer> amplifier = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    amplifier.add((int) Double.parseDouble(effect));
                }

                data.put(cases, amplifier);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, 0);
        }

        //parse duration
        cases = id.get(2);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion duration and allows you to use multiple
                ArrayList<Integer> duration = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    duration.add((int) Double.parseDouble(effect));
                }

                data.put(cases, duration);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse particles
        cases = id.get(3);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion duration and allows you to use multiple
                ArrayList<Boolean> particles = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    particles.add(Boolean.parseBoolean(effect));
                }

                data.put(cases, particles);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse icon
        cases = id.get(4);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion duration and allows you to use multiple
                ArrayList<Boolean> icon = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    icon.add(Boolean.parseBoolean(effect));
                }

                data.put(cases, icon);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse sphere radius
        cases = id.get(5);
        if (toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, 10D);
        }

        //parse ray particles
        cases = id.get(6);
        if (toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Particle.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, null);
        }

        //parse ray density
        cases = id.get(7);
        if (toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, 1);
        }

        //parse hit particles
        cases = id.get(8);
        if (toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Particle.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, null);

        }

        //parse hit density
        cases = id.get(9);
        if (toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, 1);

        }

        //parse exclusion tags
        cases = id.get(10);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion effects and allows you to spawn multiple
                String[] effects = toBeParsed.get(cases).split(",");

                ArrayList<String> strings = new ArrayList<>(Arrays.asList(effects));

                data.put(cases, strings);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, null);
        }

        //parse inclusion tags
        cases = id.get(11);
        if (toBeParsed.containsKey(cases)) {
            try {
                //splits up potion effects and allows you to spawn multiple
                String[] effects = toBeParsed.get(cases).split(",");

                ArrayList<String> strings = new ArrayList<>(Arrays.asList(effects));

                data.put(cases, strings);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        } else {
            data.put(cases, null);
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
        //gather potion data
        @SuppressWarnings("unchecked")
        ArrayList<PotionEffectType> type = (ArrayList<PotionEffectType>) data.get("Effect.Other.Sphere.Type");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> amplifier = (ArrayList<Integer>) data.get("Effect.Other.Sphere.Amplifier");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> duration = (ArrayList<Integer>) data.get("Effect.Other.Sphere.Duration");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> particles = (ArrayList<Boolean>) data.get("Effect.Other.Sphere.Particles");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> icon = (ArrayList<Boolean>) data.get("Effect.Other.Sphere.Icon");
        double radius = (double) data.get("Effect.Other.Sphere.Radius");
        int density = (int) data.get("Effect.Other.Sphere.Ray.Density");
        int hdensity = (int) data.get("Effect.Other.Sphere.Hit.Density");

        //apply effects to others
        Collection<Entity> collection = sender.getWorld().getNearbyEntities(sender.getLocation(), radius, radius, radius);
        collection.removeIf((e) -> !(e instanceof LivingEntity));
        collection.removeIf((e) -> e.getUniqueId().equals(sender.getUniqueId()));
        Object[] entities = collection.toArray();

        //filters the collection
        if (data.get("Effect.Other.Sphere.Filter.Tag.Exclude") != null || data.get("Effect.Other.Sphere.Filter.Tag.Include") != null) {
            filterCollection(data, collection);
        }

        for (Object o : entities) {
            Entity entity = (Entity) o;

            if (entity instanceof LivingEntity) {

                //creates a ray from player to entity
                if (data.get("Effect.Other.Sphere.Ray.Particle") != null && density > 0) {
                    Particle ray = (Particle) data.get("Effect.Other.Sphere.Ray.Particle");
                    Location eloc = entity.getLocation().add(0, ((LivingEntity) entity).getEyeHeight() / 2, 0);
                    Location ploc = sender.getLocation().add(0, 1.0, 0);
                    int count = (int) (ploc.distance(eloc));
                    double dx = (eloc.getX() - ploc.getX()) / (count * density);
                    double dy = (eloc.getY() - ploc.getY()) / (count * density);
                    double dz = (eloc.getZ() - ploc.getZ()) / (count * density);

                    for (int v = 0; v < count * density; v++) {
                        ploc = ploc.add(dx, dy, dz);
                        sender.getWorld().spawnParticle(ray, ploc, 1);
                    }
                }

                //creates hit effect
                if (data.get("Effect.Other.Sphere.Hit.Particle") != null && hdensity > 0) {
                    Particle hray = (Particle) data.get("Effect.Other.Sphere.Hit.Particle");
                    Location eloc = entity.getLocation().add(0, ((LivingEntity) entity).getEyeHeight() / 2, 0);
                    sender.getWorld().spawnParticle(hray, eloc, hdensity, hdensity / 10, hdensity / 10, hdensity / 10, 1, null, true);
                }

                //applies the potion effects
                for (int i = 0; i < type.size(); i++) {
                    //create the potion effect
                    PotionEffect effect = new PotionEffect(PotionEffectType.HEAL, 0, 0);
                    try {
                        PotionEffectType type1 = type.get(i);

                        int duration1;
                        try {
                            duration1 = duration.get(i);
                        } catch (Exception e) {
                            duration1 = duration.get(0);
                        }

                        int amplifier1;
                        try {
                            amplifier1 = amplifier.get(i);
                        } catch (Exception e) {
                            amplifier1 = amplifier.get(0);
                        }

                        boolean particles1;
                        try {
                            particles1 = particles.get(i);
                        } catch (Exception e) {
                            particles1 = particles.get(0);
                        }

                        boolean icon1;
                        try {
                            icon1 = icon.get(i);
                        } catch (Exception e) {
                            icon1 = icon.get(0);
                        }

                        effect = new PotionEffect(type1, duration1, amplifier1, false, particles1, icon1);

                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "There was an error applying potion effects.");
                    }

                    //set the potion effect
                    ((LivingEntity) entity).addPotionEffect(effect);
                }
            }
        }
        data.clear();
    }

    private void filterCollection(HashMap<String, Object> data, Collection<Entity> collection) {

        if (data.get("Effect.Other.Sphere.Filter.Tag.Exclude") != null) {
            @SuppressWarnings("unchecked")
            ArrayList<String> tags = (ArrayList<String>) data.get("Effect.Other.Sphere.Filter.Tag.Exclude");

            for (String tag : tags) {
                collection.removeIf((e) -> e.getScoreboardTags().contains(tag));
            }
        }

        if (data.get("Effect.Other.Sphere.Filter.Tag.Include") != null) {
            @SuppressWarnings("unchecked")
            ArrayList<String> tags = (ArrayList<String>) data.get("Effect.Other.Sphere.Filter.Tag.Include");

            for (String tag : tags) {
                collection.removeIf((e) -> !e.getScoreboardTags().contains(tag));
            }
        }
    }
}