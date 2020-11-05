package catocatocato.mweps.executors;

import catocatocato.mweps.MwepsMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Set;
import static catocatocato.mweps.MwepsMain.mwepconfig;
import static catocatocato.mweps.MwepsMain.mwepkeys;

public class WeaponUseExecutor {

    //constructor for main
    public MwepsMain plugin;
    public HashMap<String,String> playerUse = new HashMap<>();
    public WeaponUseExecutor(MwepsMain plugin){
        this.plugin = plugin;
    }

    //sync up the thread with main
    public void scheduleUse(Player player, String s, String usecase) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if(usecase.equals("LeftClick")||usecase.equals("RightClick")) {
                    weaponExecute(player, s, usecase);
                }else{
                    System.out.println("Error when determining the usecase!");
                }
            }
        }.runTask(plugin);
    }

    //executes each weapon
    public void weaponExecute(Player sender, String s, String usecase){

        //gathers the weapon list
        FileConfiguration itemlist = mwepconfig;
        Set<String> weapon = mwepkeys;

        //initialize variables
        int usetime;
        boolean consumable;
        boolean displayCooldown;
        Material ammoType = null;
        String ammoName = null;
        int ammoUse = 0;

        //parse cooldown counter
        if(itemlist.contains(s + ".DisplayCooldown")) {
            try {
                displayCooldown = itemlist.getBoolean(s + ".DisplayCooldown");
            } catch (NullPointerException e) {
                displayCooldown = false;
                sender.sendMessage(ChatColor.RED+"Invalid DisplayCooldown!");
            }
        }else {
            displayCooldown = false;
        }

        //parse consumable data
        if(itemlist.contains(s + ".Consumable")) {
            try {
                consumable = itemlist.getBoolean(s + ".Consumable");
            } catch (NullPointerException e) {
                consumable = false;
                sender.sendMessage(ChatColor.RED+"Invalid Consumable!");
            }
        }else {
            consumable = false;
        }

        //parse usetime data
        if(itemlist.contains(s + "." + usecase + ".UseTime")) {
            try {
                usetime = itemlist.getInt(s + "." + usecase + ".UseTime");
            } catch (NullPointerException e) {
                sender.sendMessage(ChatColor.RED+"Invalid UseTime!");
                usetime = 0;
            }
        }else {
            usetime = 0;
        }

        //parse ammo use
        if(itemlist.contains(s+"."+usecase+".Ammo")) {
            try {
                ammoType = Material.valueOf(itemlist.getString(s+"."+usecase+".Ammo.Type"));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid Ammo.Type!");
                ammoType = null;
            }

            try {
                ammoName = itemlist.getString(s+"."+usecase+".Ammo.Name");
            } catch (Exception e) {
                if(ammoType != null) {
                    sender.sendMessage(ChatColor.RED+"Invalid Ammo.Name!");
                    ammoName = ammoType.toString();
                }
            }

            try {
                ammoUse = itemlist.getInt(s+"."+usecase+".Ammo.Use");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED+"Invalid Ammo.Use!");
                ammoUse = 0;
            }
        }

        //give each player a delay
        if(!playerUse.containsKey(s+usecase+sender.getUniqueId().toString())){
            playerUse.put(s+usecase+sender.getUniqueId().toString(),Long.toString(System.currentTimeMillis()-usetime*50));
        }

        long LastUsed = (System.currentTimeMillis() - Long.parseLong(playerUse.get(s+usecase+sender.getUniqueId().toString())));

        //delay the action
        if(LastUsed>usetime*50) {
            boolean enable = true;

            //checks player's ammo if ammo is enabled in configs
            if(ammoUse>0&&ammoType != null){
                enable = checkAmmo(ammoType, ammoUse, ammoName, sender);
            }

            //if enabled perform valid value checks and if those pass, send them to their respect methods
            if(enable) {

                if(weapon.contains(s+"."+usecase+".Spawn")){
                    new SpawnExecutor(plugin, sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Sound")){
                    new SoundExecutor(sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Launch")){
                    new LaunchExecutor(sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Effect.Self")){
                    new EffectSelfExecutor(sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Effect.Other.Sphere")){
                    new EffectOthersSphereExecutor(sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Effect.Other.Aim")){
                    new EffectOthersAimExecutor(sender, s, usecase, itemlist);
                }

                if(weapon.contains(s+"."+usecase+".Track")){
                    new TrackerExecutor(sender, s, usecase, itemlist);
                }

                playerUse.put(s+usecase+sender.getUniqueId().toString(),Long.toString(System.currentTimeMillis()));

                //consumes the MWEP
                if(consumable&&weapon.contains(s+"."+usecase)){
                    doConsume(sender);
                }
            }
        }else if(LastUsed<usetime*50&&displayCooldown){

            String message = ((Long.parseLong(""+usetime*50)-LastUsed)/1000)+"";

            sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder()
                    .append(s+" "+usecase+" Cooldown: ").color(net.md_5.bungee.api.ChatColor.RED)
                    .append(message).color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).underlined(true)
                    .append(" second(s) left.").reset().color(net.md_5.bungee.api.ChatColor.GOLD)
                    .create());
        }
    }

    //checks the player's ammo if enabled
    public boolean checkAmmo(Material ammotype, int ammouse, String ammoname, Player user){
        boolean enable = true;
        //checks for creative mode and allows creatie players to bypass
        if(user.getGameMode() != GameMode.CREATIVE){

            //initialize the ammo name
            if(ammoname==null){
                ammoname = ammotype.toString();
            }

            //this is required since getDisplayName returns a blank string if there's none.
            String name = ammoname;

            if (ammouse > 64) {
                ammouse = 64;
            }

            //looks at what item is in the player's offhand
            try{
                ItemStack offhand = user.getInventory().getItemInOffHand();
                ItemMeta meta = offhand.getItemMeta();

                if(name.equalsIgnoreCase(offhand.getType().toString())){
                    name = meta.getDisplayName();
                }

                //checks the ammo count and removes ammo
                if (offhand.getType() == ammotype && offhand.getAmount() >= ammouse && meta.getDisplayName().equalsIgnoreCase(name)) {
                    offhand.setAmount(offhand.getAmount() - ammouse);
                } else {
                    //if there's not enough ammo then return a false
                    enable = false;
                }
            }catch (Exception e){
                //if a player is holding nothing return false
                enable = false;
            }

            //if false then play a sound and send a message
            if(!enable){
                user.getWorld().playSound(user.getLocation(),Sound.BLOCK_DISPENSER_DISPENSE,1,1);
                user.sendMessage(ChatColor.RED + "Weapon requires: " + ChatColor.GOLD + ammouse + ChatColor.GREEN +" " + ammoname);
            }
        }
        return enable;
    }

    //consumes mwep
    public void doConsume(Player user){
        //removes item if consumable
        if(user.getGameMode() != GameMode.CREATIVE){
            ItemStack weapon = user.getInventory().getItemInMainHand();
            weapon.setAmount(weapon.getAmount()-1);
        }
    }
}