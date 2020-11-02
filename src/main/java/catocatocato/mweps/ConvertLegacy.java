package catocatocato.mweps;

import catocatocato.mweps.datastorage.WeaponStorage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public class ConvertLegacy {

    public void convertLegacy(){
        FileConfiguration file = WeaponStorage.getWeapons();
        Set<String> val = file.getKeys(true);
        Object[] values = val.toArray();

        for (Object o : values) {
            String vals = o.toString();
            if (vals.contains(".")) {
                String name = (vals.split("\\."))[0];

                //converts ammo data
                if (vals.equals(name+".Ammo")) {
                    String value = file.getString(vals);
                    if(val.contains(name+".RightClick")) {
                        file.set(name + ".RightClick.Ammo.Type", value);
                    }
                    if(val.contains(name+".LeftClick")) {
                        file.set(name + ".LeftClick.Ammo.Type", value);
                    }
                    file.set(vals, null);
                }

                if (vals.equals(name+".AmmoName")) {
                    String value = file.getString(vals);
                    if(val.contains(name+".RightClick")) {
                        file.set(name + ".RightClick.Ammo.Name", value);
                    }
                    if(val.contains(name+".LeftClick")) {
                        file.set(name + ".LeftClick.Ammo.Name", value);
                    }
                    file.set(vals, null);
                }

                if (vals.equals(name+".AmmoUse")) {
                    int value = file.getInt(vals);
                    if(val.contains(name+".RightClick")) {
                        file.set(name + ".RightClick.Ammo.Use", value);
                    }
                    if(val.contains(name+".LeftClick")) {
                        file.set(name + ".LeftClick.Ammo.Use", value);
                    }
                    file.set(vals, null);
                }

                //converts usetime data
                if (vals.equals(name+".UseTime")) {
                    int value = file.getInt(vals);
                    if(val.contains(name+".RightClick")) {
                        file.set(name + ".RightClick.UseTime", value);
                    }
                    if(val.contains(name+".LeftClick")) {
                        file.set(name + ".LeftClick.UseTime", value);
                    }
                    file.set(vals, null);
                }
            }
        }
        file.set("Version-DO-NOT-TOUCH", MwepsMain.catocatocatoMwepsCurrentVersion);
        WeaponStorage.saveWeapons();
    }
}
