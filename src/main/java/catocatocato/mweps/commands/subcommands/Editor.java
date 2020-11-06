package catocatocato.mweps.commands.subcommands;

import catocatocato.mweps.commands.CommandFormat;
import catocatocato.mweps.datastorage.WeaponStorage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import java.util.*;

public class Editor extends CommandFormat {
    FileConfiguration itemlist = WeaponStorage.getWeapons();
    @Override
    public String getName() {
        return "editor";
    }

    @Override
    public String getDescription() {
        return "use this command to create or modify an mwep!";
    }

    @Override
    public String getSyntax() {
        return " <help/new/modify/delete/list/inject/view/copy> <data>";
    }

    @Override
    public int getArgumentMinCount() {
        return 1;
    }

    @Override
    public void executeCommand(CommandSender e, String[] s) {

        switch(s[1]) {
            case "new":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    if(s.length>2) {
                        newMwep(e, s[2]);
                    }else{
                        e.sendMessage(ChatColor.RED+"Missing MWEP name!");
                    }
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "modify":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    modMwep(e, s, false);
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "delete":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    if(s.length>2) {
                        delMwep(e, s[2]);
                    }else{
                        e.sendMessage(ChatColor.RED+"Missing MWEP name!");
                    }
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "list":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    listMweps(e);
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "inject":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    modMwep(e, s, true);
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "view":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    if(s.length>2) {
                        viewMwep(e, s[2]);
                    }else{
                        e.sendMessage(ChatColor.RED+"Missing MWEP name!");
                    }
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            case "copy":
                if(e.hasPermission("cato.mweps.editor."+s[1])) {
                    copyMwep(e, s);
                }else{
                    e.sendMessage(ChatColor.RED+"You do not have permission to use editor "+s[1]+"!");
                }
                break;
            default:
                sendHelp(e);
                break;
        }
    }
    //the copy command
    public void copyMwep(CommandSender sender, String[] args){
        try{
            if (itemlist.getKeys(true).contains(args[2])) {
                if (args.length > 3) {
                    ConfigurationSection data = itemlist.getConfigurationSection(args[2]);
                    itemlist.createSection(args[3],data.getValues(true));
                    sender.sendMessage(ChatColor.GREEN + "Sucessfully copied: " + args[2] + " to: " + args[3]);
                    WeaponStorage.saveWeapons();
                    WeaponStorage.reloadWeapons();
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid path/data!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + args[2] + " Doesn't exist!");
            }
        }catch (Exception e){
            sender.sendMessage(ChatColor.RED+"Missing arguments!");
        }
    }

    //the view command
    public void viewMwep(CommandSender sender, String name){
        if(itemlist.getKeys(true).contains(name)){
            Object[] array = {""};
            try {
                array = itemlist.getConfigurationSection(name).getKeys(true).toArray();
            }catch(Exception e){
                sender.sendMessage("There was an error loading the MWEP tree.");
            }
            for (Object o : array) {
                try {
                    if (!itemlist.getString(name + "." + o).contains("MemorySection")) {
                        String message = ChatColor.GOLD + name + "." + o.toString() + " : " + ChatColor.GREEN + itemlist.getString(name + "." + o.toString());
                        String copy = name + "." + o.toString() + " " + itemlist.getString(name + "." + o.toString());

                        sender.spigot().sendMessage(new ComponentBuilder(message)
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/mweps editor modify "+copy))
                                .create());
                    }
                }catch (Exception e){
                    sender.sendMessage("There was an error loading the MWEP tree.");
                }
            }
            sender.sendMessage(ChatColor.BOLD+""+ChatColor.UNDERLINE+""+ChatColor.AQUA+"You can click on the text!");
        }else{
            sender.sendMessage(ChatColor.RED + name+" doesn't exists");
        }
    }

    //the list command
    public void listMweps(CommandSender sender){

        Set<String> list = itemlist.getKeys(false);
        Object[] array = list.toArray();
        Arrays.sort(array);
        String msg = ChatColor.GOLD+ "MWEPs List:";

        for (Object o : array) {
            msg = msg + ChatColor.RED + ", " + ChatColor.RESET + o;
        }

        sender.sendMessage(msg);
    }

    //the delete command
    public void delMwep(CommandSender sender, String name){
        if(itemlist.getKeys(true).contains(name)){
            itemlist.set(name,null);
            sender.sendMessage(ChatColor.GREEN + name+" was successfully deleted!");
            WeaponStorage.saveWeapons();
        }else{
            sender.sendMessage(ChatColor.RED + name+" doesn't exists");
        }
    }

    //the modify and inject commands
    public void modMwep(CommandSender sender, String[] args, Boolean inj){
        try{
            if (itemlist.getKeys(true).contains(args[2])||inj) {

                //combines the user input into a string
                if (args.length > 3) {
                    String data="";
                    for(int i = 3; i<args.length;i++){
                        data += args[i] ;
                        if(i!=args.length-1){
                            data += " ";
                        }
                    }

                    //assign the correct data type
                    try{
                        itemlist.set(args[2], Double.parseDouble(data));
                    }catch (NumberFormatException e){
                        if(data.equals("true")||data.equals("false")) {
                            itemlist.set(args[2], Boolean.parseBoolean(data));
                        }else{
                            itemlist.set(args[2], data);
                        }
                    }

                    sender.sendMessage(ChatColor.GREEN + "Sucessfully set path: " + args[2] + " to value: " + itemlist.getString(args[2]));
                    WeaponStorage.saveWeapons();
                } else {
                    sender.sendMessage(ChatColor.RED + "Missing data!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + args[2] + " Doesn't exist!");
            }
        }catch (Exception e){
            sender.sendMessage(ChatColor.RED+"Missing arguments!");
        }
    }

    //the new command
    public void newMwep(CommandSender sender, String name){
        if(!itemlist.getKeys(true).contains(name)) {
            String wnum = "whole number";
            String num = "number";
            String tf = "true/false";
            String dat = "nbt data here";

            itemlist.set(name + ".Data", "{"+dat+"}");
            itemlist.set(name + ".Weapon", "spigot material");
            itemlist.set(name + ".Consumable", tf);
            itemlist.set(name + ".Drop", tf);
            itemlist.set(name + ".DisplayCooldown", tf);

            itemlist.set(name + ".RightClick.UseTime", wnum);
            itemlist.set(name + ".RightClick.AmmoUse", wnum);
            itemlist.set(name + ".RightClick.Ammo", "spigot material");
            itemlist.set(name + ".RightClick.AmmoName", "ammo name");
            itemlist.set(name + ".RightClick.Sound.Name", "spigot sound type");
            itemlist.set(name + ".RightClick.Sound.Pitch", num);
            itemlist.set(name + ".RightClick.Sound.Volume", num);
            itemlist.set(name + ".RightClick.Spawn.Name", "spigot entity type");
            itemlist.set(name + ".RightClick.Spawn.Count", wnum);
            itemlist.set(name + ".RightClick.Spawn.Spread", num);
            itemlist.set(name + ".RightClick.Spawn.Velocity", num);
            itemlist.set(name + ".RightClick.Spawn.Data", "{"+dat+"}");
            itemlist.set(name + ".RightClick.Grenade.Spawn.Timer", num);
            itemlist.set(name + ".RightClick.Grenade.Spawn.Name", "spigot entity type");
            itemlist.set(name + ".RightClick.Grenade.Spawn.Count", wnum);
            itemlist.set(name + ".RightClick.Grenade.Spawn.Spread", num);
            itemlist.set(name + ".RightClick.Grenade.Spawn.Velocity", num);
            itemlist.set(name + ".RightClick.Grenade.Spawn.Data", "{"+dat+"}");
            itemlist.set(name + ".RightClick.Grenade.Explosion.Power", num);
            itemlist.set(name + ".RightClick.Grenade.Explosion.Fire", tf);
            itemlist.set(name + ".RightClick.Grenade.Explosion.Break", tf);
            itemlist.set(name + ".RightClick.Launch.Velocity", num);
            itemlist.set(name + ".RightClick.Effect.Self.Type", "spigot potion effect type");
            itemlist.set(name + ".RightClick.Effect.Self.Amplifier", num);
            itemlist.set(name + ".RightClick.Effect.Self.Duration", num);
            itemlist.set(name + ".RightClick.Effect.Self.Particles", tf);
            itemlist.set(name + ".RightClick.Effect.Self.Icon", tf);
            itemlist.set(name + ".RightClick.Effect.Self.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".RightClick.Effect.Self.Ray.Density", num);
            itemlist.set(name + ".RightClick.Effect.Aim.Type", "spigot potion effect type");
            itemlist.set(name + ".RightClick.Effect.Aim.Amplifier", num);
            itemlist.set(name + ".RightClick.Effect.Aim.Duration", num);
            itemlist.set(name + ".RightClick.Effect.Aim.Range", num);
            itemlist.set(name + ".RightClick.Effect.Aim.Particles", tf);
            itemlist.set(name + ".RightClick.Effect.Aim.Icon", tf);
            itemlist.set(name + ".RightClick.Effect.Aim.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".RightClick.Effect.Aim.Ray.Density", num);
            itemlist.set(name + ".RightClick.Effect.Aim.Hit.Particle", "spigot particle type");
            itemlist.set(name + ".RightClick.Effect.Aim.Hit.Density", num);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Type", "spigot potion effect type");
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Amplifier", num);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Duration", num);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Particles", tf);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Icon", tf);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Ray.Density", num);
            itemlist.set(name + ".RightClick.Effect.Other.Sphere.Ray.Radius", num);
            itemlist.set(name + ".RightClick.Track.Type", "spigot entity type");
            itemlist.set(name + ".RightClick.Track.Range", num);
            itemlist.set(name + ".RightClick.Track.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".RightClick.Track.Ray.Range", num);
            itemlist.set(name + ".RightClick.Track.Ray.Density", wnum);

            itemlist.set(name + ".LeftClick.UseTime", wnum);
            itemlist.set(name + ".LeftClick.AmmoUse", wnum);
            itemlist.set(name + ".LeftClick.Ammo", "spigot material");
            itemlist.set(name + ".LeftClick.AmmoName", "ammo name");
            itemlist.set(name + ".LeftClick.Sound.Name", "spigot sound type");
            itemlist.set(name + ".LeftClick.Sound.Pitch", num);
            itemlist.set(name + ".LeftClick.Sound.Volume", num);
            itemlist.set(name + ".LeftClick.Spawn.Name", "spigot entity type");
            itemlist.set(name + ".LeftClick.Spawn.Count", wnum);
            itemlist.set(name + ".LeftClick.Spawn.Spread", num);
            itemlist.set(name + ".LeftClick.Spawn.Velocity", num);
            itemlist.set(name + ".LeftClick.Spawn.Data", "{"+dat+"}");
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Timer", num);
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Name", "spigot entity type");
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Count", wnum);
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Spread", num);
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Velocity", num);
            itemlist.set(name + ".LeftClick.Grenade.Spawn.Data", "{"+dat+"}");
            itemlist.set(name + ".LeftClick.Grenade.Explosion.Power", num);
            itemlist.set(name + ".LeftClick.Grenade.Explosion.Fire", tf);
            itemlist.set(name + ".LeftClick.Grenade.Explosion.Break", tf);
            itemlist.set(name + ".LeftClick.Launch.Velocity", num);
            itemlist.set(name + ".LeftClick.Effect.Self.Type", "spigot potion effect type");
            itemlist.set(name + ".LeftClick.Effect.Self.Amplifier", num);
            itemlist.set(name + ".LeftClick.Effect.Self.Duration", num);
            itemlist.set(name + ".LeftClick.Effect.Self.Particles", tf);
            itemlist.set(name + ".LeftClick.Effect.Self.Icon", tf);
            itemlist.set(name + ".LeftClick.Effect.Self.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".LeftClick.Effect.Self.Ray.Density", num);
            itemlist.set(name + ".LeftClick.Effect.Aim.Type", "spigot potion effect type");
            itemlist.set(name + ".LeftClick.Effect.Aim.Amplifier", num);
            itemlist.set(name + ".LeftClick.Effect.Aim.Duration", num);
            itemlist.set(name + ".LeftClick.Effect.Aim.Particles", tf);
            itemlist.set(name + ".LeftClick.Effect.Aim.Icon", tf);
            itemlist.set(name + ".LeftClick.Effect.Aim.Range", num);
            itemlist.set(name + ".LeftClick.Effect.Aim.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".LeftClick.Effect.Aim.Ray.Density", num);
            itemlist.set(name + ".LeftClick.Effect.Aim.Hit.Particle", "spigot particle type");
            itemlist.set(name + ".LeftClick.Effect.Aim.Hit.Density", num);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Type", "spigot potion effect type");
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Amplifier", num);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Duration", num);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Particles", tf);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Icon", tf);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Ray.Density", num);
            itemlist.set(name + ".LeftClick.Effect.Other.Sphere.Ray.Radius", num);
            itemlist.set(name + ".LeftClick.Track.Type", "spigot entity type");
            itemlist.set(name + ".LeftClick.Track.Range", num);
            itemlist.set(name + ".LeftClick.Track.Ray.Particle", "spigot particle type");
            itemlist.set(name + ".LeftClick.Track.Ray.Range", num);
            itemlist.set(name + ".LeftClick.Track.Ray.Density", wnum);

            sender.sendMessage(ChatColor.GREEN + "Successfully Created: " + name);
            WeaponStorage.saveWeapons();
        }else{
            sender.sendMessage(ChatColor.RED + name+" already exists!");
        }
    }

    //gives the player a help booklet
    public void sendHelp(CommandSender sender){
        Player player = (Player) sender;
        if(player.getInventory().firstEmpty() == -1||sender instanceof ConsoleCommandSender){
            player.sendMessage(ChatColor.RED + "Not enough inventory space to send instruction booklet!");
        }else{
            player.sendMessage(ChatColor.GOLD + "An instruction booklet has been given to you.");
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setAuthor("catocatocato");
            meta.setTitle("How to use the MWEPs editor");
            //write the instructional booklet
            List<String> pages = new ArrayList<>();
            ChatColor bold = ChatColor.BOLD;
            ChatColor uline = ChatColor.UNDERLINE;
            ChatColor reset = ChatColor.RESET;
            ChatColor green = ChatColor.DARK_GREEN;
            ChatColor red = ChatColor.DARK_RED;
            ChatColor blue = ChatColor.DARK_BLUE;
            ChatColor gold = ChatColor.GOLD;
            pages.add(bold+"How to use the MWEPs editor\n\n"+reset+green+"By catocatocato\n\n"+"Table of Contents\n\n" +reset+blue+
                    "1. MWEP Structure (3)\n" +
                    "2. Weapon names (6)\n" +
                    "3. Weapon props. (8)\n" +
                    "4. Action Arguments (13)\n" +
                    "5. Sound & Launch (14)\n" +
                    "6. Spawn (16)");
            pages.add(blue+"7. Ammo Mode (21)\n" +
                    "8. Tutorial (22)\n" +
                    "9. Commands (30)\n\n" +green+
                    "You can skip to page 22 and just go through the tutorial if you can't be bothered to read.");
            pages.add("SECTION 1: MWEP Structure\n\nCreating an MWEP requires knowledge of how the weapons.yml file is structured.\n\n" +
                    "The weapons.yml file is organized a lot like how folders are, with a path"  +
                    "and with data stored at the end.");
            pages.add("MWEP paths are structured:\n\n"+blue+"[Name].[Property].[Action].[Arguments]\n\n" +reset+
                    "and just like a folder, the paths can branch out into multiple other paths. " +
                    "However, unlike a folder, each path will always end with data.");
            pages.add("The MWEPs plugin uses these paths to determine what the MWEP should do " +
                    "and the data at the end of each path tells the MWEPs plugin how to do it. ");
            pages.add("SECTION 2: Weapon Name\n\n" +
                    "The [Name] of your MWEP is its unique identifier which distinguishes it from other items. When generating an MWEP, " +
                    "the item you get will have the same name as the [Name]. You can use _ and - as spaces.");
            pages.add("however you can change its name using   an anvil without changing its properties." +
                    "The [Name] will be stored as a tag on the weapon itself, making it impossible to create without commands so " +
                    "no need to worry about players creating an MWEP by renaming an item!");
            pages.add("SECTION 3: Weapon Properties\n\n" +
                    "Currently, there are 6 possible properties an MWEP can have:\n\n" +blue+
                    "[Data],[Weapon],[Consumable],[UseTime],[RightClick],[LeftClick]\n\n" +reset+
                    "These properties come right after [Name], for example [Name].[Data] ");
            pages.add("You use the command"+red+" /mwep editor modify [Name].[Property] <data> " +reset+
                    "in order to edit these values. The [Data] value determines what NBT data to generate " +
                    "your weapon with such as {Enchantments:[{id:\"loyalty\",lvl:3}} which would give your generated ");
            pages.add("MWEP the loyalty 3 enchantment. It's important to note that if [Data] is not wrapped with brackets, MWEPs will throw errors! "+
                    "The [Weapon] path determines what item to actually spawn. It uses the Spigot Materials library which can be found at: ");
            pages.add(uline+"https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html \n" +reset+
                    "The [Consumable] tag accepts a true/false value and determines if you use up the MWEP when it is activated. ");
            pages.add("The [UseTime] tag accepts a whole number and determines the cool-down of the MWEP in ticks." +
                    "The [RightClick] and [LeftClick] paths are action paths which tells the MWEPs plugin what to do on left and right click.");
            pages.add("SECTION 4: Action Arguments\n\n" +
                    "The [RightClick] and [LeftClick] are action paths which tells the MWEPs plugin what exactly to do. " +
                    "Currently, there are 3 possible actions under each path."+red+" [Sound],[Launch],[Spawn] ");
            pages.add("SECTION 5: [Sound]&[Launch]\n\n" +
                    "The [Sound] path tells MWEPs to play a sound when an MWEP is used. Ithas 3 other paths, "+red+"[Name],[Pitch],[Volume] " +reset+
                    "The [Name] accepts the Spigot sound library which can be found at:\n"+uline+"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
            pages.add("The [Pitch] path accepts a number and determines the pitch, how fast, to play the song. The [Volume] path accepts a number" +
                    "which determines the volume of the sound. Currently 1 is the max volume and increasing it only increases how far the sound can be heard from. ");
            pages.add("The [Launch] path only has a single path under it, [Velocity]. The [Velocity] path determines how fast to throw the player forward. ");
            pages.add("SECTION 6: [Spawn]\n\n" +
                    "The [Spawn] path spawns entities. It has 5 sub-paths: "+red+"[Count],[Name],[Velocity],[Data],[Spread] " +reset+
                    "The [Count] path accepts a whole number and determines how many entities to spawn. ");
            pages.add("The [Name] path determines what entity to spawn and utilizes the spigot EntityType library which can be found at: " +uline+
                    "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html");
            pages.add("The [Velocity] path accepts a number and determines what velocity to shoot the entity out at in blocks/tick " +
                    "The [Data] path adds NBT data to the entity you're shooting out and requires brackets or else it'll throw errors. ");
            pages.add("The [Spread] path acceps a number and determines how spread the entities will be shot at.");
            pages.add("SECTION 7: Ammo Mode\n\n" +
                    "In weapons.yml there is a configuration setting called Ammo**Mode which accepts a true/false value and determines if MWEPs will use arrows as ammo. " +
                    "The amount of ammo used is equal to the the value set for [Count].");
            pages.add("SECTION 8: Tutorial\n\n" +
                    "Let's put all of what we learned to use and make a flamethrower MWEP! " +
                    "We first begin by creating the mwep with "+gold+"/mwep editor new Flamethrower."+reset+" This will create a new MWEP which we can use"+red+" /mwep generate to obtain, ");
            pages.add("however, in its current state it does nothing. In order to make it do stuff we need to modify a few path values.");
            pages.add("To start, let's make the MWEP a blaze rod by using"+gold+" /mwep editor modify Flamethrower.Weapon BLAZE_ROD.");
            pages.add("Let's also make it play a sound on right click so we use "+gold+"/mwep editor modify Flamethrower.RightClick.Sound.Name ENTITY_BLAZE_SHOOT");
            pages.add("Now, let's make it shoot fireballs by using "+gold+"/mwep editor modify Flamethrower.RightClick.Spawn.Name FIREBALL_SMALL");
            pages.add("It currently now shoots fireballs and plays a sound but that isn't too flamethrower like. Let's modify the spread, velocity and count too! " +
                    "make it more flamethrower like!");
            pages.add(gold+"/mweps editor modify Flamethrower.RightClick.Spawn.Count 50\n\n /mweps editor modify Flamethrower.RightClick.Spawn.Velocity 2\n\n");
            pages.add(gold+"/mweps editor modify Flamethrower.RightClick.Spawn.Spread 3. \n\n" +reset+
                    "Now, let's tidy up the MWEP tree by using "+gold+"/mwep editor delete Flamethrower.LeftClick"+reset+" and "+gold+"/mwep editor delete Flamethrower.RightClick.Launch\n\n"+reset+
                    "With any luck, you should now have a flamethrower that works!");
            pages.add(red+"SECTION 9: Commands\n\n" +
                    "help - gives this book\n" +gold+
                    "new <name> - creates a new MWEP\n" +red+
                    "modify <path> <data> - modifies an MWEP\n" +gold+
                    "delete <name> - deletes an MWEP or path\n" +red+
                    "list - shows all MWEPs\n" +gold+
                    "inject <path> <data> - injects raw data into weapons.yml\n");
            pages.add(red+"view <name> - views MWEP tree.\n" +
                    "copy <existing> <new> - copies the MWEP");
            meta.setPages(pages);
            //give the player the info book
            book.setItemMeta(meta);
            player.getInventory().addItem(book);
        }
    }

}
