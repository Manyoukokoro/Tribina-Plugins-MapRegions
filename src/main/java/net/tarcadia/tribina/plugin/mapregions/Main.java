package net.tarcadia.tribina.plugin.mapregions;

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
	}

	@Override
	public void onEnable() {
		this.regionMaps = new RegionMaps(Main.config, Main.dataPath);
	}

	@Override
	public void onDisable() {
		this.regionMaps.save();
	}
}
