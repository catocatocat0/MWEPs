package catocatocato.mweps.datastorage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import static catocatocato.mweps.MwepsMain.mwepconfig;
import static catocatocato.mweps.MwepsMain.mwepkeys;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class WeaponStorage {

    private static File file;

    private static FileConfiguration config;

    //creates and checks data storage file for items
    public static void setupConfig(){
        try {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("MWeps").getDataFolder(), "weapons.yml");
        }catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("MWeps was not able to access the config folder!");
        }

        if(!file.exists()){
            try {
                file.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("MWeps was not able to create the weapons.yml file!");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

    }

    //reloads the weapons file
    public static void reloadWeapons(){
        try {
            config.load(file);
            mwepconfig = WeaponStorage.getWeapons();
            mwepkeys = WeaponStorage.getWeaponKeys();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("MWeps was not able reload weapons.yml!");
        }
    }

    //saves the weapons file
    public static void saveWeapons(){
        try {
            config.save(file);
            mwepconfig = WeaponStorage.getWeapons();
            mwepkeys = WeaponStorage.getWeaponKeys();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("MWeps was not able to save to weapons.yml!");
        }
    }

    //gets the weapons files
    public static FileConfiguration getWeapons(){
        return config;
    }

    //gets keys from the weapons file
    public static Set<String> getWeaponKeys(){
        return config.getKeys(true);
    }

}
