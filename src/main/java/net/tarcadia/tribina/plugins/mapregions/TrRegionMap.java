package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrRegionMap {

    private final String pathConfig;
    private final String pathMaps;
    private final File fileConfig;
    private final YamlConfiguration config;

    private final List<String> regionKeys;
    private final Map<String, TrRegion> regionList;
    private final TrRegion[][] regionMap;

    private final UUID world;
    private final int x_offset;
    private final int z_offset;
    private final int x_length;
    private final int z_length;

    public TrRegionMap(@NonNull String pathConfig, @NonNull String pathMaps)
    {
        this.pathConfig = pathConfig;
        this.pathMaps = pathMaps;
        this.fileConfig = new File(this.pathConfig);
        this.config = YamlConfiguration.loadConfiguration(this.fileConfig);
        this.world = UUID.fromString(this.config.getString("world", ""));
        this.x_offset = this.config.getInt("x_offset");
        this.z_offset = this.config.getInt("z_offset");
        this.x_length = this.config.getInt("x_length");
        this.z_length = this.config.getInt("z_length");

        this.regionKeys = new ArrayList<>();
        this.regionList = new HashMap<>();
        this.regionMap = new TrRegion[this.x_length][this.z_length];
        ConfigurationSection configRegions = this.config.getConfigurationSection("regions");
        if (configRegions != null) {
            for (String regionId : configRegions.getKeys(false)) {
                ConfigurationSection configSection = configRegions.getConfigurationSection(regionId);
                if (configSection == null) { configSection = configRegions.createSection(regionId); }
                this.regionKeys.add(regionId);
                this.regionList.put(regionId, new TrRegion(configSection));
            }
        }

        for (String regionId : this.regionKeys) {
            Set<Pair<Integer, Integer>> posSet = new HashSet<>(); //loadFromBmp(this.pathMaps + regionId + ".bmp");
            for (Pair<Integer, Integer> pos : posSet)
            {
                if (pos.x() > 0 && pos.y() > 0 && pos.x() < this.x_length && pos.y() < this.z_length) {
                    if (this.regionMap[pos.x()][pos.y()] == null) {
                        TrRegion region = this.regionList.get(regionId);
                        this.regionMap[pos.x()][pos.y()] = region;
                        region.addPos(pos);
                    }
                }
            }
        }
    }

    public boolean save() {
        try {
            this.config.save(this.fileConfig);
            for (String regionId : this.regionKeys) {
                TrRegion region = this.regionList.get(regionId);
                Set<Pair<Integer, Integer>> posSet = region.getPosSet();
                //saveToBmp(this.pathMaps + regionId + ".bmp", posSet);
            }
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public TrRegion getRegion(@NonNull String regionId) { return this.regionList.get(regionId); }

    public TrRegion getRegion(int x, int z) {
        int _x = x - this.x_offset;
        int _z = z - this.z_offset;
        if (_x >= 0 && _z >= 0 && _x < this.x_length && _z < this.z_length) {
            return this.regionMap[_x][_z];
        }
        else return null;
    }

    public TrRegion getRegion(@NonNull UUID world, int x, int z) { return (world == this.world ? getRegion(x, z) : null); }
    public TrRegion getRegion(@NonNull Pair<Integer, Integer> pos) { return this.getRegion(pos.x(), pos.y()); }
    public TrRegion getRegion(@NonNull Location loc) {return this.getRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ()); }

    public boolean inRegion(int x, int z, @NonNull TrRegion region) { return region.containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(int x, int z, @NonNull String regionId) { return this.getRegion(regionId).containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull TrRegion region) { return (world == this.world) && region.containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull String regionId) { return (world == this.world) && this.getRegion(regionId).containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull TrRegion region) { return this.inRegion(pos.x(), pos.y(), region); }
    public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull String regionId) { return this.inRegion(pos.x(), pos.y(), regionId); }
    public boolean inRegion(@NonNull Location loc, @NonNull TrRegion region) { return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), region); }
    public boolean inRegion(@NonNull Location loc, @NonNull String regionId) { return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), regionId); }
}
