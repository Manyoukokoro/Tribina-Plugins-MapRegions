package net.tarcadia.tribina.plugins.mapregions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {

	public static JavaPlugin plugin = null;
	public static FileConfiguration config = null;
	public static PluginDescriptionFile descrp = null;
	public static Logger logger = null;
	public static String dataPath = null;

	private RegionMaps regionMaps;

	@Override
	public void onLoad() {
		Main.plugin = this;
		Main.config = this.getConfig();
		Main.descrp = this.getDescription();
		Main.logger = this.getLogger();
		Main.dataPath = this.getDataFolder().getPath();
		Main.logger.info("Loaded " + Main.descrp.getName() + " v" + Main.descrp.getVersion());
	}

	@Override
	public void onEnable() {
		this.regionMaps = new RegionMaps(Main.config, Main.dataPath);
		Main.logger.info("Enabled " + Main.descrp.getName() + " v" + Main.descrp.getVersion());
	}

	@Override
	public void onDisable() {
		this.saveDefaultConfig();
		this.regionMaps.save();
		Main.logger.info("Disabled " + Main.descrp.getName() + " v" + Main.descrp.getVersion());
	}
}
