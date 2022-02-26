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

public class TrRegionMap {

    private class TrRegion {

        private final Set<Pair<Integer, Integer>> posSet;
        private final ConfigurationSection config;

        public TrRegion(@NonNull ConfigurationSection config) {
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

        public boolean containsPos(int x, int y) { return this.posSet.contains(new Pair<>(x, y)); }

        public boolean containsPos(Pair<Integer, Integer> pos) { return this.posSet.contains(pos); }

        public boolean containsPosSet(Collection<Pair<Integer, Integer>> posSet) {return this.posSet.containsAll(posSet); }

        public boolean addPos(int x, int y)
        {
            return this.posSet.add(new Pair<>(x, y));
        }

        public boolean addPos(Pair<Integer, Integer> pos) { return this.posSet.add(pos.copy()); }

        public boolean addPosSet(Collection<Pair<Integer, Integer>> posSet)
        {
            return this.posSet.addAll(posSet);
        }

        public boolean removePos(int x, int y)
        {
            return this.posSet.remove(new Pair<>(x, y));
        }

        public boolean removePos(Pair<Integer, Integer> pos)
        {
            return this.posSet.remove(pos);
        }

        public boolean removePosSet(Collection<Pair<Integer, Integer>> posSet)
        {
            return this.posSet.removeAll(posSet);
        }

        public boolean retainPosSet(Collection<Pair<Integer, Integer>> posSet) { return this.posSet.retainAll(posSet); }
    }

    private final String pathConfig;
    private final String pathMaps;
    private final File fileConfig;
    private final YamlConfiguration config;
    private final ConfigurationSection configRegions;

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
        try {
            this.fileConfig = new File(this.pathConfig);
            this.config = YamlConfiguration.loadConfiguration(this.fileConfig);
            this.world = UUID.fromString(this.config.getString("world", ""));
            this.x_offset = this.config.getInt("x_offset");
            this.z_offset = this.config.getInt("z_offset");
            this.x_length = this.config.getInt("x_length");
            this.z_length = this.config.getInt("z_length");
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal configuration arguments.", e);
        }

        this.regionKeys = new ArrayList<>();
        this.regionList = new HashMap<>();
        this.regionMap = new TrRegion[this.x_length][this.z_length];
        ConfigurationSection configRegions = this.config.getConfigurationSection("regions");
        if (configRegions != null) {
            this.configRegions = configRegions;
            for (String regionId : this.configRegions.getKeys(false)) {
//                if (!isLegalId(regionId)) {
//                    throw new IllegalArgumentException("Illegal configuration keys.");
//                }
                ConfigurationSection configSection = configRegions.getConfigurationSection(regionId);
                if (configSection == null) { configSection = configRegions.createSection(regionId); }
                this.regionKeys.add(regionId);
                this.regionList.put(regionId, new TrRegion(configSection));
            }
        } else {
            this.configRegions = this.config.createSection("regions");
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

    public YamlConfiguration getConfig() { return this.config; }

    public boolean setValue(@NonNull String regionId, @NonNull String key, Object obj) {
        this.regionList.get(regionId).setValue(key, obj);
        return true;
    }

    public Object getValue(@NonNull String regionId, @NonNull String key) {
        return this.regionList.get(regionId).getValue(key);
    }

    public void createRegion(@NonNull String regionId)
    {
//        if (!isLegalId(regionId)) {
//            throw new IllegalArgumentException("Illegal configuration keys.");
//            return null;
//        }
        ConfigurationSection configSection = this.configRegions.createSection(regionId);
        TrRegion region = new TrRegion(configSection);
        this.regionKeys.add(regionId);
        this.regionList.put(regionId, region);
        return;
    }

    @Nullable
    private TrRegion getRegion(@NonNull String regionId) { return this.regionList.get(regionId); }

    @Nullable
    private TrRegion getRegion(int x, int z) {
        int _x = x - this.x_offset;
        int _z = z - this.z_offset;
        if (_x >= 0 && _z >= 0 && _x < this.x_length && _z < this.z_length) {
            return this.regionMap[_x][_z];
        }
        else return null;
    }

    @Nullable
    private TrRegion getRegion(@NonNull UUID world, int x, int z) { return (world == this.world ? getRegion(x, z) : null); }

    @Nullable
    private TrRegion getRegion(@NonNull Pair<Integer, Integer> pos) { return this.getRegion(pos.x(), pos.y()); }

    @Nullable
    private TrRegion getRegion(@NonNull Location loc) {return this.getRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ()); }

    public boolean inRegion(int x, int z, @NonNull TrRegion region) { return region.containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(int x, int z, @NonNull String regionId) { return this.regionKeys.contains(regionId) && this.getRegion(regionId).containsPos(x - this.x_offset, z - this.z_offset); }
    public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull TrRegion region) { return (world == this.world) && this.inRegion(x, z, region); }
    public boolean inRegion(@NonNull UUID world, int x, int z, @NonNull String regionId) { return (world == this.world) && this.inRegion(x, z, regionId); }
    public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull TrRegion region) { return this.inRegion(pos.x(), pos.y(), region); }
    public boolean inRegion(@NonNull Pair<Integer, Integer> pos, @NonNull String regionId) { return this.inRegion(pos.x(), pos.y(), regionId); }
    public boolean inRegion(@NonNull Location loc, @NonNull TrRegion region) { return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), region); }
    public boolean inRegion(@NonNull Location loc, @NonNull String regionId) { return this.inRegion(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ(), regionId); }
}
