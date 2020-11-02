package catocatocato.mweps.datastorage;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

public class DefaultItems {
    public static void writeDefaults() {
        //initialize items.yml
        WeaponStorage.setupConfig();
        FileConfiguration itemstorage = WeaponStorage.getWeapons();

        //create the data for the default ATrident Weapon
        itemstorage.addDefault("ATrident.Data", "{Enchantments:[{id:\"loyalty\",lvl:3}]}");
        itemstorage.addDefault("ATrident.Weapon", Material.TRIDENT.toString());
        itemstorage.addDefault("ATrident.Consumable", false);
        itemstorage.addDefault("ATrident.RightClick.UseTime", 0);
        itemstorage.addDefault("ATrident.RightClick.Ammo", Material.ARROW.toString());
        itemstorage.addDefault("ATrident.RightClick.AmmoUse", 0);
        itemstorage.addDefault("ATrident.RightClick.AmmoName", "trident");
        itemstorage.addDefault("ATrident.RightClick.Sound.Name", Sound.ENTITY_ARROW_SHOOT.toString());
        itemstorage.addDefault("ATrident.RightClick.Sound.Pitch", 1);
        itemstorage.addDefault("ATrident.RightClick.Sound.Volume", 1);
        itemstorage.addDefault("ATrident.RightClick.Spawn.Spread", 5);
        itemstorage.addDefault("ATrident.RightClick.Spawn.Count", 50);
        itemstorage.addDefault("ATrident.RightClick.Spawn.Name", EntityType.SPECTRAL_ARROW.toString());
        itemstorage.addDefault("ATrident.RightClick.Spawn.Velocity", 5);
        itemstorage.addDefault("ATrident.RightClick.Spawn.Data", "{damage:10,life:1180,PierceLevel:100,pickup:2}");
        itemstorage.addDefault("ATrident.LeftClick.UseTime", 0);
        itemstorage.addDefault("ATrident.LeftClick.Ammo", Material.ARROW.toString());
        itemstorage.addDefault("ATrident.LeftClick.AmmoUse", 0);
        itemstorage.addDefault("ATrident.LeftClick.AmmoName", "trident");
        itemstorage.addDefault("ATrident.LeftClick.Sound.Name", Sound.ITEM_TRIDENT_THUNDER.toString());
        itemstorage.addDefault("ATrident.LeftClick.Sound.Pitch", 1);
        itemstorage.addDefault("ATrident.LeftClick.Sound.Volume", 1);
        itemstorage.addDefault("ATrident.LeftClick.Spawn.Spread", 5);
        itemstorage.addDefault("ATrident.LeftClick.Spawn.Count", 50);
        itemstorage.addDefault("ATrident.LeftClick.Spawn.Name", EntityType.TRIDENT.toString());
        itemstorage.addDefault("ATrident.LeftClick.Spawn.Velocity", 4);
        itemstorage.addDefault("ATrident.LeftClick.Spawn.Data", "{life:1180}");

        //creates the default weapon ASign
        itemstorage.addDefault("ASign.Data", "{Enchantments:[{id:\"knockback\",lvl:10}]}");
        itemstorage.addDefault("ASign.Weapon", Material.OAK_SIGN.toString());
        itemstorage.addDefault("ASign.Consumable", false);
        itemstorage.addDefault("ASign.RightClick.UseTime", 40);
        itemstorage.addDefault("ASign.RightClick.Launch.Velocity", 2);

        //save defaults
        itemstorage.options().copyDefaults(true);
        WeaponStorage.saveWeapons();
    }
}
