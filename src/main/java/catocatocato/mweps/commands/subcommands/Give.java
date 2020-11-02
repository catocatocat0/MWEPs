package catocatocato.mweps.commands.subcommands;

import catocatocato.mweps.MwepsMain;
import catocatocato.mweps.commands.CommandFormat;
import catocatocato.mweps.commands.CommandManager;
import catocatocato.mweps.datastorage.WeaponStorage;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NbtApiException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.Set;

public class Give extends CommandFormat {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "gives an mwep to a player";
    }

    @Override
    public String getSyntax() {
        return "<player> <tool name> <amount>";
    }

    @Override
    public int getArgumentMinCount() {
        return 1;
    }

    //executes the /generate command
    @Override
    public void executeCommand(CommandSender sender, String[] s) {

        //gets player and name and sends weapon generator
        Player player = Bukkit.getServer().getPlayer(s[1]);
        if(player!=null&&s.length>2) {
            String name = s[2];
            int amount = 1;
            try {

                amount = Integer.parseInt(s[3]);

            } catch (Exception e) {

            }
            genWeapon(sender, player, name, amount);
        }else{
            sender.sendMessage(ChatColor.RED+"Player was not found or missing argument 3!");
        }
    }

    //constructor to get main instance
    private final MwepsMain gmain;
    public Give(CommandManager plugin){
        this.gmain = plugin.mwepsMain;
    }

    //generates weapon with unique identifier
    public void genWeapon(CommandSender sender, Player player, String weaponName, int amount){
        //sets up access to weapons.yml
        FileConfiguration weaponfile = WeaponStorage.getWeapons();
        Set<String> itemKeys = WeaponStorage.getWeaponKeys();

        //checks and sees if weapon player requested exists
        if(itemKeys.contains(weaponName)&&!weaponName.contains("**")){


            //checks player's inventory
            if(player.getInventory().firstEmpty() == -1){

                sender.sendMessage(ChatColor.RED+"Player's Inventory is full, make some space!");

            }else{

                //gets what kind of material the weapon is
                Material weaponmaterial = Material.ACACIA_BOAT;
                try {
                    weaponmaterial = Material.getMaterial(weaponfile.getString(weaponName + ".Weapon"));
                }catch (Exception e){
                    sender.sendMessage(ChatColor.RED+"Invalid MWEP Material! Using Default material.");
                }                ItemStack weapon = new ItemStack(weaponmaterial);
                NBTItem nbt;

                //creates the weapon and renames the item
                ItemMeta meta = weapon.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                meta.setDisplayName(weaponName.replace("-", " ").replace("_"," "));
                data.set(new NamespacedKey(gmain,"mweps"), PersistentDataType.STRING, weaponName);
                weapon.setItemMeta(meta);
                weapon.setAmount(amount);

                //check and see if nbt data is included and include nbt data
                if(weaponfile.getString(weaponName+".Data")!=null) {
                    String weapondata = weaponfile.getString(weaponName + ".Data");
                    try{

                        if(!weapondata.split("")[0].equals("{")) {
                            weapondata = "{" + weapondata + "}";
                        }

                        nbt = new NBTItem(weapon);
                        nbt.mergeCompound(new NBTContainer(weapondata));
                        weapon = nbt.getItem();

                    }catch(NbtApiException e){
                        sender.sendMessage(ChatColor.RED + "There was an error loading weapon NBT data");
                        System.out.println(ChatColor.RED + "[MWeps] There was an error loading weapon NBT data for player "+player.getName());
                    }
                }

                player.getInventory().addItem(weapon);
                player.sendMessage(ChatColor.GREEN + "You've recieved: "+weaponName+"!");
                sender.sendMessage(ChatColor.GREEN + "You've sent: "+weaponName+" to: "+player.getName()+"!");
            }

        }else{
            sender.sendMessage(ChatColor.RED+weaponName+" does not exist! MWeps names are case sensitive!");
        }
    }
}
