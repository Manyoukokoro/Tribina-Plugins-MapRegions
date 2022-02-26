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
	private class Region {

		private final Set<Pair<Integer, Integer>> posSet;
		private final ConfigurationSection config;

		public Region(@NonNull ConfigurationSection config) {
			this.posSet = new HashSet<>();
			this.config = config;
		}

		public ConfigurationSection getConfig() {
			return this.config;
		}

		public boolean setValue(@NonNull String key, Object obj) {
			this.config.set(key, obj);
			return true;
		}

		public Object getValue(@NonNull String key) {
			return this.config.get(key);
		}

		public Set<Pair<Integer, Integer>> getPosSet()
		{
			return this.posSet;
		}

		public boolean containsPos(int x, int y) {
			return this.posSet.contains(new Pair<>(x, y));
		}

		public boolean containsPos(Pair<Integer, Integer> pos) {
			return this.posSet.contains(pos);
		}

		public boolean containsPosSet(Collection<Pair<Integer, Integer>> posSet) {
			return this.posSet.containsAll(posSet);
		}

		public boolean addPos(int x, int y) {
			return this.posSet.add(new Pair<>(x, y));
		}

		public boolean addPos(Pair<Integer, Integer> pos) {
			return this.posSet.add(pos.clone());
		}

		public boolean addPosSet(Collection<Pair<Integer, Integer>> posSet) {
			return this.posSet.addAll(posSet);
		}

		public boolean removePos(int x, int y) {
			return this.posSet.remove(new Pair<>(x, y));
		}

		public boolean removePos(Pair<Integer, Integer> pos) {
			return this.posSet.remove(pos);
		}

		public boolean removePosSet(Collection<Pair<Integer, Integer>> posSet) {
			return this.posSet.removeAll(posSet);
		}

		public boolean retainPosSet(Collection<Pair<Integer, Integer>> posSet) {
			return this.posSet.retainAll(posSet);
		}
	}

	private final String pathConfig;
	private final String pathMaps;
	private final File fileConfig;
	private final YamlConfiguration config;
	private final ConfigurationSection configRegions;

	private final Map<String, Region> regionList;
	private final Region[][] regionMap;

	private final UUID world;
	private final int x_offset;
	private final int z_offset;
	private final int x_length;
	private final int z_length;

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

		this.regionList = new HashMap<>();
		this.regionMap = new Region[this.x_length][this.z_length];
		ConfigurationSection configRegions = this.config.getConfigurationSection("regions");
		if (configRegions != null) {
			this.configRegions = configRegions;
			for (String regionId : this.configRegions.getKeys(false)) {
				ConfigurationSection configSection = configRegions.getConfigurationSection(regionId);
				if (configSection == null) { configSection = configRegions.createSection(regionId); }
				if (this.regionList.putIfAbsent(regionId, new Region(configSection)) == null) {
					throw new IllegalArgumentException("Duplicated regionId");
				}
			}
		} else {
			this.configRegions = this.config.createSection("regions");
		}

		for (var region : this.regionList.values()) {
			Set<Pair<Integer, Integer>> posSet = new HashSet<>();
			// TODO: load posSet from a Bitmap file
			for (Pair<Integer, Integer> pos : posSet) {
				if (pos.x() >= 0 && pos.y() >= 0 && pos.x() < this.x_length && pos.y() < this.z_length) {
					if (this.regionMap[pos.x()][pos.y()] == null) {
						this.regionMap[pos.x()][pos.y()] = region;
						region.addPos(pos);
					}
				}
			}
		}
	}

	public boolean save() { // should throw an exception when failed
		try {
			this.config.save(this.fileConfig);
			for (var region : this.regionList.values()) {
				var posSet = region.getPosSet();
				// TODO: save posSet into a Bitmap file
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public YamlConfiguration getConfig() { return this.config; }

	public boolean setValue(@NonNull String regionId, @NonNull String key, Object obj) {
		this.regionList.get(regionId).setValue(key, obj);
		return true;
	}

	public Object getValue(@NonNull String regionId, @NonNull String key) {
		return this.regionList.get(regionId).getValue(key);
	}

	public void createRegion(@NonNull String regionId) {
		ConfigurationSection configSection = this.configRegions.createSection(regionId);
		Region region = new Region(configSection);
		this.regionList.put(regionId, region);
	}

	@Nullable
	private Region getRegion(@NonNull String regionId) {
		return this.regionList.get(regionId);
	}

	@Nullable
	private Region getRegion(int x, int z) {
		int _x = x - this.x_offset;
		int _z = z - this.z_offset;
		if (_x >= 0 && _z >= 0 && _x < this.x_length && _z < this.z_length) {
			return this.regionMap[_x][_z];
		}
		else {
			return null;
		}
	}

	@Nullable
	private Region getRegion(@NonNull UUID world, int x, int z) {
		return (world == this.world ? getRegion(x, z) : null);
	}

	@Nullable
	private Region getRegion(@NonNull Pair<Integer, Integer> pos) {
		return this.getRegion(pos.x(), pos.y());
	}

	@Nullable
	private Region getRegion(@NonNull Location loc) {
		return this.getRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ());
	}

	public boolean inRegion(int x, int z, @NonNull Region region) {
		return region.containsPos(x - this.x_offset, z - this.z_offset);
	}

	public boolean inRegion(int x, int z, @NonNull String regionId) {
		var region = this.getRegion(regionId);
		if (region == null) {
			return false;
		}
		return this.inRegion(x, z, region);
	}

	public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull Region region) {
		return (world == this.world) && this.inRegion(x, z, region);
	}

	public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull String regionId) {
		return (world == this.world) && this.inRegion(x, z, regionId);
	}

	public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull Region region) {
		return this.inRegion(pos.x(), pos.y(), region);
	}

	public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull String regionId) {
		return this.inRegion(pos.x(), pos.y(), regionId);
	}

	public boolean inRegion(@NonNull Location loc, @NonNull Region region) {
		return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), region);
	}

	public boolean inRegion(@NonNull Location loc, @NonNull String regionId) {
		return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), regionId);
	}
}
