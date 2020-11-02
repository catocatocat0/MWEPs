package catocatocato.mweps.commands.subcommands;

import catocatocato.mweps.MwepsMain;
import catocatocato.mweps.commands.CommandFormat;
import catocatocato.mweps.commands.CommandManager;
import catocatocato.mweps.datastorage.WeaponStorage;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class Bind extends CommandFormat {
    //constructor to get main instance
    private final MwepsMain gmain;
    public Bind(CommandManager plugin){
        this.gmain = plugin.mwepsMain;
    }

    @Override
    public String getName() {
        return "bind";
    }

    @Override
    public String getDescription() {
        return "binds an mwep to the item you're holding";
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public int getArgumentMinCount() {
        return 0;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] s) {
        //checks of the command sender is a player
        if(sender instanceof Player){

            //gets player and name and sends to binding
            Player player = (Player) sender;
            try {
                String name = s[1];
                bindWeapon(player, name);
            }catch (Exception e){
                player.sendMessage(ChatColor.RED+"Specify a weapon name!");
            }

        }else{
            sender.sendMessage(ChatColor.RED+"You must run this command as a player!");
        }
    }

    //binds weapon to player's onhand item
    public void bindWeapon(Player player, String weaponName){
        Set<String> itemKeys = WeaponStorage.getWeapons().getKeys(false);
        if(itemKeys.contains(weaponName)){
            //checks if player is holding something
            try {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getAmount()>0) {
                    //binds the MWEP
                    ItemMeta meta = item.getItemMeta();
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();
                    pdc.set(new NamespacedKey(gmain,"mweps"), PersistentDataType.STRING, weaponName);
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN+weaponName+" was successfully bound!");
                }else{
                    player.sendMessage(ChatColor.RED+"You must be holding something to use this command!");
                }
            }catch (Exception e){
                player.sendMessage(ChatColor.RED+"You must be holding something to use this command!");
            }
        }else{
            player.sendMessage(ChatColor.RED+weaponName+" does not exist!");
        }

    }
}
