package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EffectOthersAimExecutor extends ExecutorFormat{


    EffectOthersAimExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist){
        super(plugin, sender, mwep, usecase, mweplist);
    }


    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Effect.Other.Aim.Type");
        id.add("Effect.Other.Aim.Amplifier");
        id.add("Effect.Other.Aim.Duration");
        id.add("Effect.Other.Aim.Particles");
        id.add("Effect.Other.Aim.Icon");
        id.add("Effect.Other.Aim.Range");
        id.add("Effect.Other.Aim.Ray.Particle");
        id.add("Effect.Other.Aim.Ray.Density");
        id.add("Effect.Other.Aim.Hit.Particle");
        id.add("Effect.Other.Aim.Hit.Density");
        id.add("Effect.Other.Aim.Tag");
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
        if(toBeParsed.containsKey(cases)) {
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
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;
        }

        //parse amplifier
        cases = id.get(1);
        if(toBeParsed.containsKey(cases)) {
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
        }else{
            data.put(cases, 0);

        }

        //parse duration
        cases = id.get(2);
        if(toBeParsed.containsKey(cases)) {
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
        }else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse particles
        cases = id.get(3);
        if(toBeParsed.containsKey(cases)) {
            try {
                //splits up potion duration and allows you to use multiple
                ArrayList<Boolean> particles = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    particles.add(Boolean.parseBoolean(effect));
                }

                data.put(cases, particles);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Missing " + cases);
                enable = false;
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse icon
        cases = id.get(4);
        if(toBeParsed.containsKey(cases)) {
            try {
                //splits up potion duration and allows you to use multiple
                ArrayList<Boolean> icon = new ArrayList<>();
                String[] effects = toBeParsed.get(cases).split(",");

                for (String effect : effects) {
                    icon.add(Boolean.parseBoolean(effect));
                }

                data.put(cases, icon);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Missing " + cases);
                enable = false;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
            enable = false;

        }

        //parse sphere radius
        cases = id.get(5);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else {
            data.put(cases, 10D);

        }

        //parse ray particles
        cases = id.get(6);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Particle.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else{
            data.put(cases, null);
        }

        //parse ray density
        cases = id.get(7);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else{
            data.put(cases, 1);

        }

        //parse hit particles
        cases = id.get(8);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        Particle.valueOf(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else{
            data.put(cases, null);

        }

        //parse hit density
        cases = id.get(9);
        if(toBeParsed.containsKey(cases)) {
            try {
                data.put(cases,
                        (int) Double.parseDouble(toBeParsed.get(cases)));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid " + cases);
                enable = false;

            }
        }else{
            data.put(cases, 1);

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
        //gather potion data
        @SuppressWarnings("unchecked")
        ArrayList<PotionEffectType> type = (ArrayList<PotionEffectType>) data.get("Effect.Other.Aim.Type");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> amplifier = (ArrayList<Integer>) data.get("Effect.Other.Aim.Amplifier");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> duration = (ArrayList<Integer>) data.get("Effect.Other.Aim.Duration");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> particles = (ArrayList<Boolean>) data.get("Effect.Other.Aim.Particles");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> icon = (ArrayList<Boolean>) data.get("Effect.Other.Aim.Icon");
        double range = (double) data.get("Effect.Other.Aim.Range");
        int density = (int) data.get("Effect.Other.Aim.Ray.Density");
        int hdensity = (int) data.get("Effect.Other.Aim.Hit.Density");

        //get player's location and calculate weapon location
        Location ppos = sender.getEyeLocation();
        Vector plook = ppos.getDirection().normalize();
        ppos.add((plook.clone()).multiply(0.75));

        //ray trace for entities
        RayTraceResult e2 = sender.getWorld().rayTraceEntities(ppos,plook,range,
                this::entityFilter);
        LivingEntity entity;
        boolean enable = true;

        try {
            entity = ((LivingEntity) e2.getHitEntity());
        }catch (Exception e){
            entity = null;
        }

        //ray trace for blocks
        RayTraceResult b2 = sender.getWorld().rayTraceBlocks(ppos,plook,range, FluidCollisionMode.NEVER,true);
        Block block;
        try {
            block = (b2.getHitBlock());
        }catch (Exception e){
            block = null;
        }

        //check and see if there's a block in front of the entity
        if(block != null && entity != null) {
            if ((e2.getHitPosition().toLocation(sender.getWorld()).distance(ppos) - b2.getHitPosition().toLocation(sender.getWorld()).distance(ppos))>0) {
                enable = false;
            }
        }

        //creates hit effect
        if (data.get("Effect.Other.Aim.Hit.Particle") !=null && hdensity > 0) {
            Particle hray = (Particle) data.get("Effect.Other.Aim.Hit.Particle");
            Location eloc;
            if(entity != null &&enable) {
                eloc = e2.getHitPosition().toLocation(sender.getWorld());
            }else{
                if(block != null) {
                    eloc = b2.getHitPosition().toLocation(sender.getWorld());
                }else {
                    eloc = (ppos.clone()).add(ppos.getDirection().multiply(range));
                }
            }
            sender.getWorld().spawnParticle(hray, eloc, density, density/10, density/10, density/10 ,1, null, true);
        }

        //creates a ray
        if (data.get("Effect.Other.Aim.Ray.Particle") !=null && density > 0) {
            Particle ray = (Particle) data.get("Effect.Other.Aim.Ray.Particle");
            Location eloc;

            if(entity != null&&enable) {
                eloc = e2.getHitPosition().toLocation(sender.getWorld());
            }else{
                if(block != null) {
                    eloc = b2.getHitPosition().toLocation(sender.getWorld());
                }else {
                    eloc = (ppos.clone()).add(ppos.getDirection().multiply(range));
                }
            }

            int count = (int) (ppos.distance(eloc));

            double dx = (eloc.getX() - ppos.getX()) / (count * density);
            double dy = (eloc.getY() - ppos.getY()) / (count * density);
            double dz = (eloc.getZ() - ppos.getZ()) / (count * density);

            for (int v = 0; v < count * density; v++) {
                ppos = ppos.add(dx, dy, dz);
                sender.getWorld().spawnParticle(ray, ppos, 1, 0, 0, 0 ,1, null, true);
            }
        }

        //creates and applies potion effects
        if(entity != null &&enable){
            for (int i = 0; i < type.size(); i++) {
                //create the potion effect
                PotionEffect effect = new PotionEffect(PotionEffectType.HEAL,0,0);
                try {
                    PotionEffectType type1 = type.get(i);

                    int duration1;
                    try{
                        duration1 = duration.get(i);
                    }catch (Exception e){
                        duration1 = duration.get(0);
                    }

                    int amplifier1;
                    try {
                        amplifier1 = amplifier.get(i);
                    }catch (Exception e){
                        amplifier1 = amplifier.get(0);
                    }

                    boolean particles1;
                    try {
                        particles1 = particles.get(i);
                    }catch (Exception e){
                        particles1 = particles.get(0);
                    }

                    boolean icon1;
                    try {
                        icon1 = icon.get(i);
                    }catch (Exception e){
                        icon1 = icon.get(0);
                    }

                    //reverses undead potion effects
                    if(entity.getCategory().equals(EntityCategory.UNDEAD)){
                        if(type1.equals(PotionEffectType.HARM)){
                            type1 = PotionEffectType.HEAL;
                        }else if(type1.equals(PotionEffectType.HEAL)){
                            type1 = PotionEffectType.HARM;
                        }
                    }

                    effect = new PotionEffect(type1, duration1, amplifier1, false, particles1, icon1);


                }catch (Exception e){
                    sender.sendMessage(ChatColor.RED+"There was an error applying potion effects.");
                }

                //set the potion effect
                entity.addPotionEffect(effect);
            }
        }
    }

    private boolean entityFilter(Entity e){
        boolean status;

        if(e.getUniqueId()!=sender.getUniqueId()&&e instanceof LivingEntity){

            Set<String> tags = e.getScoreboardTags();

            status = !tags.contains("HVZHuman") &&
                    (!mwep.equalsIgnoreCase("Laser-Pistol") ||
                    !mwep.equalsIgnoreCase("Wonderwaffle"));
        }else{
            status = false;
        }

        return status;
    }

}
