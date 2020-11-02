package catocatocato.mweps.commands;

import org.bukkit.command.CommandSender;

public abstract class CommandFormat {

    //get name of subcommand
    public abstract String getName();

    //get the command's description
    public abstract String getDescription();

    //get the proper usage of the command
    public abstract String getSyntax();

    //get proper number of arguments
    public abstract int getArgumentMinCount();

    //execute the command
    public abstract void executeCommand(CommandSender e, String[] s);


}
