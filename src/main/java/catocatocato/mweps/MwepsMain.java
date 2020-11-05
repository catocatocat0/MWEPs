package catocatocato.mweps;

import catocatocato.mweps.commands.CommandManager;
import catocatocato.mweps.executors.WeaponUseExecutor;
import catocatocato.mweps.datastorage.DefaultItems;
import catocatocato.mweps.datastorage.WeaponStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
            /*
            To do list:
            - Add in a save function to save memory config to config file
            - Add in config to enable/disable arrow bounceback
            - Modify NBT merging to allow nested NBT tags
            - use a better method to copy MWEPs
            - split up the editor into its own multiple commands
            */
public class MwepsMain extends JavaPlugin implements Listener {
    //define objects
    WeaponUseExecutor useWeapon;
    ConvertLegacy convert;
    public static Set<String> mwepkeys;
    public static FileConfiguration mwepconfig;

    //version number
    public static final String catocatocatoMwepsCurrentVersion = "1.5";

    //runs when the plugin gets enabled
    @Override
    public void onEnable(){
        //creates file config stuff
        new BukkitRunnable(){
            @Override
            public void run(){
                mwepconfig = WeaponStorage.getWeapons();
                mwepkeys = WeaponStorage.getWeaponKeys();
            }
        }.runTaskLater(this, 1L);
        getServer().getPluginManager().registerEvents(this,this);

        //setup items.yml and add defaults to it
        try {
            DefaultItems.writeDefaults();
        }catch(NullPointerException e){
            System.out.println("MWeps failed to load weapons.yml!");
            e.printStackTrace();
        }

        //call constructors
        this.getCommand("mweps").setExecutor(new CommandManager(this));
        useWeapon = new WeaponUseExecutor(this);
        convert = new ConvertLegacy();

        //save config file
        this.saveDefaultConfig();

        //convert legacy formats to current
        if(mwepconfig.contains("Version-DO-NOT-TOUCH")) {
            if (!mwepconfig.getString("Version-DO-NOT-TOUCH").equalsIgnoreCase(catocatocatoMwepsCurrentVersion)) {
                convert.convertLegacy();
            }
        }else{
            convert.convertLegacy();
        }
    }

    @Override
    public void onDisable(){

        //save any player added files
        WeaponStorage.saveWeapons();
    }

    //executes mweps
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event){
        if(event.getAction() == Action.LEFT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.LEFT_CLICK_BLOCK){
            try {
                if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this, "mweps"), PersistentDataType.STRING)) {
                    ItemStack weapon = event.getPlayer().getInventory().getItemInMainHand();
                    PersistentDataContainer data = weapon.getItemMeta().getPersistentDataContainer();
                    String mwep = data.get(new NamespacedKey(this, "mweps"), PersistentDataType.STRING);
                    Set<String> weaponlist = WeaponStorage.getWeaponKeys();

                    if (this.getConfig().getBoolean("Require-Perms-to-Use-MWEP") &&
                            !event.getPlayer().hasPermission("cato.mweps.weapon." + mwep)) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use weapon: " + mwep);

                    } else if (weaponlist.contains(mwep)) {
                        String usecase = "";

                        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            usecase = "LeftClick";
                        }

                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            usecase = "RightClick";
                        }

                        event.setCancelled(true);
                        useWeapon.scheduleUse(event.getPlayer(), mwep, usecase);

                    }
                }
            }catch(NullPointerException e){

            }
        }
    }

    //no iframes
    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event){
        try {
            if (this.getConfig().getBoolean("Disable-IFrames") &&
                    event.getDamager().getPersistentDataContainer().has(new NamespacedKey(this, "mweps"), PersistentDataType.STRING)&&
                    event.getDamager() instanceof Projectile) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                    if (event.getEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event.getEntity();
                        entity.setNoDamageTicks(0);
                        entity.setMaximumNoDamageTicks(0);
                    }
                }, 2L);
            }
        }catch (Exception e){
            
        }
    }

    //prevent dropping
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        try {
            if (event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this, "mweps"), PersistentDataType.STRING)&&
                    event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                ItemStack weapon = event.getItemDrop().getItemStack();
                PersistentDataContainer data = weapon.getItemMeta().getPersistentDataContainer();
                String mwep = data.get(new NamespacedKey(this, "mweps"), PersistentDataType.STRING);
                if(mwepconfig.contains(mwep+".Drop")){
                    if((!Boolean.parseBoolean(mwepconfig.getString(mwep+".Drop")))){
                        event.getPlayer().sendMessage(ChatColor.RED+"You cannot drop: "+mwep);
                        event.setCancelled(true);
                    }
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //clears the timers when players leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        //clear use times
        try {
            Object[] keys = useWeapon.playerUse.keySet().toArray();

            for(int i = 0; i < useWeapon.playerUse.size(); i++){
                String key = keys[i].toString();
                if(key.contains(event.getPlayer().getUniqueId().toString())){
                    useWeapon.playerUse.remove(key);
                }
            }
        }catch (Exception e){

        }
    }

    //prevents itemframes/paintings from breaking
    @EventHandler
    public void itemFrameBreak(HangingBreakByEntityEvent event){
        if(this.getConfig().getBoolean("Disable-Projectiles-Breaking-Hanging-Entities")&&
                event.getRemover().getPersistentDataContainer().has(new NamespacedKey(this, "mweps"), PersistentDataType.STRING)) {
            if(event.getRemover() instanceof Projectile){
                event.setCancelled(true);
            }
        }
    }
}