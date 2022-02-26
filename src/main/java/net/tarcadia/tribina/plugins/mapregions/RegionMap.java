package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionMap {
	private final String pathConfig;
	private final String pathMaps;
	private final File fileConfig;
	private final YamlConfiguration config;
	private final ConfigurationSection configRegions;

	private final Map<Pair<Integer, Integer>, String> regionMap;

	private UUID world;
	private int x_offset;
	private int z_offset;
	private int x_length;
	private int z_length;

	public RegionMap(@NonNull String pathConfig, @NonNull String pathMaps) {
		this.pathConfig = pathConfig;
		this.pathMaps = pathMaps;
		try {
			this.fileConfig = new File(this.pathConfig);
			this.config = YamlConfiguration.loadConfiguration(this.fileConfig);
			this.world = UUID.fromString(this.config.getString("world", ""));
			this.x_offset = this.config.getInt("x_offset");
			this.z_offset = this.config.getInt("z_offset");
			this.x_length = this.config.getInt("x_length");
			this.z_length = this.config.getInt("z_length");
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal configuration arguments", e);
		}

		this.regionMap = new TreeMap<>(new Comparator<>() {
			@Override
			public int compare(Pair<Integer, Integer> pos1, Pair<Integer, Integer> pos2) {
				int result = pos1.x().compareTo(pos2.x());
				if (result != 0) {
					return result;
				}
				return pos1.y().compareTo(pos2.y());
			}
		});

		ConfigurationSection configRegions = this.config.getConfigurationSection("regions");
		if (configRegions != null) {
			this.configRegions = configRegions;
			for (String regionId : this.configRegions.getKeys(false)) {
				if (configRegions.getConfigurationSection(regionId) == null) { configRegions.createSection(regionId); }
				Set<Pair<Integer, Integer>> posSet = new HashSet<>();
				// TODO: load posSet from a Bitmap file
				for (Pair<Integer, Integer> pos : posSet) {
					this.regionMap.put(pos, regionId);
				}
			}
		} else {
			this.configRegions = this.config.createSection("regions");
		}
	}

	public boolean save() throws IOException {
		this.config.set("world", this.world);
		this.config.set("x_offset", this.x_offset);
		this.config.set("z_offset", this.z_offset);
		this.config.set("x_length", this.x_length);
		this.config.set("z_length", this.z_length);

		Map<String, Set<Pair<Integer, Integer>>> posSets = new HashMap<>();
		for (var pos : this.regionMap.keySet()) {
			var regionId = this.regionMap.get(pos);
			var posSet = posSets.computeIfAbsent(regionId, k -> new TreeSet<>(new Comparator<>() {
				@Override
				public int compare(Pair<Integer, Integer> pos1, Pair<Integer, Integer> pos2) {
					int result = pos1.x().compareTo(pos2.x());
					if (result != 0) {
						return result;
					}
					return pos1.y().compareTo(pos2.y());
				}
			}));
			posSet.add(pos);
		}
		try {
			this.config.save(this.fileConfig);
			for (String regionId : this.configRegions.getKeys(false)) {
				var posSet = posSets.get(regionId);
				// TODO: save posSet into a Bitmap file
			}
			return true;
		} catch (Exception e) {
			throw new IOException("Save file failed", e);
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
}
