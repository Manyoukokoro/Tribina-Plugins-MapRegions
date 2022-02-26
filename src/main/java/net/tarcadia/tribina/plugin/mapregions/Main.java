package net.tarcadia.tribina.plugin.mapregions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

	public static JavaPlugin plugin;
	public static FileConfiguration config;
	public static PluginDescriptionFile descrp;
	public static Logger logger;

	@Override
	public void onLoad() {
		Main.plugin = this;
		Main.config = this.getConfig();
		Main.descrp = this.getDescription();
		Main.logger = this.getLogger();
	}

	@Override
	public void onEnable() {
		// Plugin startup logic
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
