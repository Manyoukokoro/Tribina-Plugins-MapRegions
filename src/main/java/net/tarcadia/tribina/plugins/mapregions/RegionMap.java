package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionMap {
	private final String pathConfig;
	private final String pathMaps;
	private final File fileConfig;

	private final Map<Pair<Integer, Integer>, String> regionMap;

	private YamlConfiguration config;
	private ConfigurationSection configRegions;

	private UUID world;
	private int x_offset;
	private int z_offset;
	private int x_length;
	private int z_length;

	public RegionMap(@NonNull String pathConfig, @NonNull String pathMaps) {
		this.pathConfig = pathConfig;
		this.pathMaps = pathMaps;
		this.fileConfig = new File(this.pathConfig);
		this.regionMap = new TreeMap<>(Comparator.comparingInt((Pair<Integer, Integer> pos) -> pos.x()).thenComparingInt(Pair::y));
		try {
			this.load();
		} catch (IOException e)
		{
			// TODO: Log error
		}
	}

	public void load() throws IOException {
		this.loadConfigs();
		this.loadAllMaps();
	}

	public void loadConfigs() throws IOException {
		this.config = YamlConfiguration.loadConfiguration(this.fileConfig);
		this.configRegions = Objects.requireNonNullElseGet(
				this.config.getConfigurationSection("regions"),
				() -> this.config.createSection("regions")
		);
		try {
			this.world = UUID.fromString(this.config.getString("world", ""));
			this.x_offset = this.config.getInt("x_offset");
			this.z_offset = this.config.getInt("z_offset");
			this.x_length = this.config.getInt("x_length");
			this.z_length = this.config.getInt("z_length");
		} catch (Exception e) {
			this.world = null;
			this.x_offset = 0;
			this.x_length = 0;
			this.z_offset = 0;
			this.z_length = 0;
			throw new IOException("Load config file failed.", e);
		}
	}

	public void loadMap(@NonNull String regionId) throws IOException {
		try {
			File fileMap = new File(this.pathMaps + "/" + regionId + ".bmp");
			BufferedImage image = ImageIO.read(fileMap);
			// TODO: load from a Bitmap file to this.regionMap
		} catch (Exception e) {
			throw new IOException("Load map file failed.", e);
		}
	}

	public void loadAllMaps() throws IOException {
		List<Exception> es = new LinkedList<>();
		for (String regionId : this.configRegions.getKeys(false)) {
			try {
				this.loadMap(regionId);
			} catch (Exception e) {
				es.add(e);
			}
		}
		if (!es.isEmpty()) {
			throw new IOException("Load map files failed.", es.get(0));
		}
	}

	public void save() throws IOException {
		this.saveConfigs();
		this.saveAllMaps();
	}

	public void saveConfigs() throws IOException {
		try {
			this.config.set("world", this.world);
			this.config.set("x_offset", this.x_offset);
			this.config.set("z_offset", this.z_offset);
			this.config.set("x_length", this.x_length);
			this.config.set("z_length", this.z_length);
			this.config.save(this.fileConfig);
		} catch (Exception e) {
			throw new IOException("Save config file failed.", e);
		}
	}

	public void saveMap(@NonNull String regionId) throws IOException {
		Set<Pair<Integer, Integer>> posSet = new TreeSet<>(Comparator.comparingInt((Pair<Integer, Integer> pos) -> pos.x()).thenComparingInt(Pair::y));

		for (var e : this.regionMap.entrySet()) {
			if (regionId.equals(e.getValue())) {
				posSet.add(e.getKey());
			}
		}
		try {
			// TODO: save posSet into a Bitmap file
		} catch (Exception e) {
			throw new IOException("Save map file failed.", e);
		}
	}

	public void saveAllMaps() throws IOException {
		List<Exception> es = new LinkedList<>();

		Map<String, Set<Pair<Integer, Integer>>> posSets = new HashMap<>();
		for (var e : this.regionMap.entrySet()) {
			var regionId = e.getValue();
			var posSet = posSets.computeIfAbsent(regionId, k -> new TreeSet<>(Comparator.comparingInt((Pair<Integer, Integer> pos3) -> pos3.x()).thenComparingInt(Pair::y)));
			posSet.add(e.getKey());
		}

		for (String regionId : this.configRegions.getKeys(false)) {
			var posSet = posSets.get(regionId);
			try {
				// TODO: save posSet into a Bitmap file
			} catch (Exception e) {
				es.add(e);
			}
		}
		if (!es.isEmpty()) {
			throw new IOException("Save map files failed.", es.get(1));
		}
	}

	public YamlConfiguration getConfig() { return this.config; }

	public boolean setValue(@NonNull String regionId, @NonNull String key, Object obj) {
		this.configRegions.getConfigurationSection(regionId).set(key, obj);
		return true;
	}

	public Object getValue(@NonNull String regionId, @NonNull String key) {
		return this.configRegions.getConfigurationSection(regionId).get(key);
	}

	public void createRegion(@NonNull String regionId) {
		// TODO: check if regionId is a suitable string for id;
		ConfigurationSection configSection = this.configRegions.createSection(regionId);
	}

	public boolean inRegion(int x, int z, @NonNull String regionId) {
		return regionId.equals(this.regionMap.get(new Pair<>(x - this.x_offset, z - this.z_offset)));
	}

	public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull String regionId) {
		return world.equals(this.world) && this.inRegion(x, z, regionId);
	}

	public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull String regionId) {
		return this.inRegion(pos.x(), pos.y(), regionId);
	}

	public boolean inRegion(@NonNull Location loc, @NonNull String regionId) {
		return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), regionId);
	}

	public String getRegion(int x, int z) {
		return this.regionMap.get(new Pair<>(x - this.x_offset, z - this.z_offset));
	}

	public String getRegion(@NonNull UUID world, int x, int z) {
		return (world.equals(this.world)) ? this.getRegion(x, z) : null;
	}

	public String getRegion(@NonNull Pair<Integer, Integer> pos) {
		return this.getRegion(pos.x(), pos.y());
	}

	public String getRegion(@NonNull Location loc) {
		return this.getRegion(Objects.requireNonNull(loc.getWorld()).getUID(), loc.getBlockX(), loc.getBlockZ());
	}

	public void addToRegion(int x, int z, @NonNull String regionId) {
		int _x = x - this.x_offset;
		int _z = z - this.z_offset;
		if (_x >= 0 && _z >= 0 && _x < this.x_length && _z < this.z_length) {
			this.regionMap.putIfAbsent(new Pair<>(_x, _z), regionId);
		}
	}

	public void addToRegion(@NonNull UUID world, int x, int z, @NonNull String regionId) {
		if (world.equals(this.world)) {
			this.addToRegion(x, z, regionId);
		}
	}

	public void addToRegion(@NonNull Pair<Integer, Integer> pos, @NonNull String regionId) {
		this.addToRegion(pos.x(), pos.y(), regionId);
	}

	public void addToRegion(@NonNull UUID world, @NonNull Pair<Integer, Integer> pos, @NonNull String regionId) {
		this.addToRegion(world, pos.x(), pos.y(), regionId);
	}

	public void addToRegion(@NonNull Collection<Pair<Integer, Integer>> posSet, @NonNull String regionId) {
		for (var pos : posSet) {
			this.addToRegion(pos.x(), pos.y(), regionId);
		}
	}

	public void addToRegion(@NonNull UUID world, @NonNull Collection<Pair<Integer, Integer>> posSet, @NonNull String regionId) {
		if (world.equals(this.world)) {
			this.addToRegion(posSet, regionId);
		}
	}

	public void addToRegion(@NonNull Location loc, @NonNull String regionId) {
		this.addToRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), regionId);
	}
}
