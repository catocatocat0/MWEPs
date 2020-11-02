package catocatocato.mweps.commands;

import catocatocato.mweps.MwepsMain;
import catocatocato.mweps.commands.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandManager implements CommandExecutor {
    private final HashMap<String, CommandFormat> subcommands = new HashMap<>();
    public final MwepsMain mwepsMain;
    public final FileConfiguration config;

    //constructor
    public CommandManager(MwepsMain plugin){
        this.mwepsMain = plugin;
        config = plugin.getConfig();
        subcommands.put("reload",new Reload(this));
        subcommands.put("editor",new Editor());
        subcommands.put("generate",new Generate(this));
        subcommands.put("give",new Give(this));
        subcommands.put("bind",new Bind(this));
    }

    //gets the command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //executes commands if there are arguments
        if(args.length>0 && subcommands.get(args[0]) != null && subcommands.get(args[0]).getArgumentMinCount() < args.length){
            if(sender.hasPermission("cato.mweps."+args[0])){
                CommandFormat cmd = subcommands.get(args[0]);
                cmd.executeCommand(sender, args);
            }else{
                sender.sendMessage(ChatColor.RED+ "You do not have permission to use: /mweps "+args[0]);
            }
        }else{
            ArrayList<CommandFormat> cmdlist = new ArrayList<>(subcommands.values());
            sender.sendMessage(ChatColor.DARK_RED+"MWeps command list:");
            for (CommandFormat commandFormat : cmdlist) {
                sender.sendMessage(ChatColor.GREEN+"/mweps " + commandFormat.getName() +ChatColor.GOLD+" " + commandFormat.getSyntax() + " - " +ChatColor.WHITE+ commandFormat.getDescription());
            }
        }
        return true;
    }
}
