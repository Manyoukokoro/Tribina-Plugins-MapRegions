package net.tarcadia.tribina.plugins.mapregions.command;

import net.tarcadia.tribina.plugins.mapregions.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class CommandReloadConfigs extends BaseCommand implements TabExecutor {
    public CommandReloadConfigs(String cmd) {
        super(cmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Main.plugin.reloadConfigs();
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
