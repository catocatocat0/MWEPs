package catocatocato.mweps.commands.subcommands;

import catocatocato.mweps.commands.CommandFormat;
import catocatocato.mweps.commands.CommandManager;
import catocatocato.mweps.datastorage.WeaponStorage;
import org.bukkit.command.CommandSender;

public class Reload extends CommandFormat {

    private final CommandManager plugin;
    public Reload(CommandManager plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads config and weapons.yml";
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
    public void executeCommand(CommandSender sender, String[] command) {
        plugin.mwepsMain.reloadConfig();

        try {
            WeaponStorage.reloadWeapons();
            sender.sendMessage("MWeps Config Reloaded!");
        }catch (Exception e){
            sender.sendMessage("MWeps was not able to load weapons.yml");
        }

    }
}
