package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrRegionMap {

    private String pathConfig;
    private String pathMaps;
    private File fileConfig;
    private YamlConfiguration config;

    private List<String> regionKeys;
    private Map<String, TrRegion> regionList;
    private TrRegion[][] regionMap;

    private UUID world;
    private int x_offset;
    private int z_offset;
    private int x_length;
    private int z_length;

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
        ConfigurationSection configurationSection = this.config.getConfigurationSection("regions");
        if (configurationSection != null) {
            for (String regionId : configurationSection.getKeys(false)) {
                this.regionKeys.add(regionId);
                this.regionList.put(regionId, new TrRegion(configurationSection.getConfigurationSection(regionId)));
            }
        }

        for (String regionId : this.regionKeys) {
            Set<Pair<Integer, Integer>> posSet = loadFromBmp(this.pathMaps + regionId + ".bmp");
            for (Pair<Integer, Integer> pos : posSet)
            {
                if (pos.x() < this.x_length && pos.y() < this.z_length) {
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
                saveToBmp(this.pathMaps + regionId + ".bmp", posSet);
            }
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }

}
