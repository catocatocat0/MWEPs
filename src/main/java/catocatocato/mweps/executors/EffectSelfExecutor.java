package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class EffectSelfExecutor extends ExecutorFormat{

    EffectSelfExecutor(MwepsMain plugin, Entity sender, String mwep, String usecase, FileConfiguration mweplist){
        super(plugin, sender, mwep, usecase, mweplist);
    }

    @Override
    public void parseData() {
        //sets up relevant data to look for
        ArrayList<String> id = new ArrayList<>();
        id.add("Effect.Self.Type");
        id.add("Effect.Self.Amplifier");
        id.add("Effect.Self.Duration");
        id.add("Effect.Self.Particles");
        id.add("Effect.Self.Icon");
        id.add("Effect.Self.Radius");

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
        }else{
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
        }else{
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
        }else {
            sender.sendMessage(ChatColor.RED + "Missing " + cases);
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
        //gather potion data
        @SuppressWarnings("unchecked")
        ArrayList<PotionEffectType> type = (ArrayList<PotionEffectType>) data.get("Effect.Self.Type");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> amplifier = (ArrayList<Integer>) data.get("Effect.Self.Amplifier");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> duration = (ArrayList<Integer>) data.get("Effect.Self.Duration");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> particles = (ArrayList<Boolean>) data.get("Effect.Self.Particles");
        @SuppressWarnings("unchecked")
        ArrayList<Boolean> icon = (ArrayList<Boolean>) data.get("Effect.Self.Icon");

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

                effect = new PotionEffect(type1, duration1, amplifier1, false, particles1, icon1);
            }catch (Exception e){
                sender.sendMessage(ChatColor.RED+"There was an error applying potion effects.");
            }

            //set the potion effect
            sender.addPotionEffect(effect);
        }
        data.clear();
    }
}
