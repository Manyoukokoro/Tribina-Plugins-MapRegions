package net.tarcadia.tribina.plugins.mapregions.command;

import net.tarcadia.tribina.plugins.mapregions.Main;
import org.bukkit.command.*;
public class BaseCommand{

    protected PluginCommand command;
    public BaseCommand(String cmd)
    {
        this.command = Main.plugin.getCommand(cmd);
        if (command != null)
        {
            if (this instanceof CommandExecutor) {
                command.setExecutor((CommandExecutor) this);
            }
            if (this instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) this);
            }
            if (!(this instanceof CommandExecutor) && !(this instanceof TabCompleter))
            {
                Main.logger.warning("Not a command implementation: " + this.getClass().getSimpleName());
            }
        }
        else
        {
            Main.logger.severe("Unable to register command: " + this.getClass().getSimpleName());
        }
    }

}
