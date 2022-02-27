package net.tarcadia.tribina.plugin.mapregions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionMaps {

    private String pathRegionMaps;
    private ConfigurationSection config;
    private ConfigurationSection configGlobal;
    private List<String> mapsList;
    private Map<String, RegionMap> maps;

    public void RegionMap(@NonNull ConfigurationSection config, @NonNull String pathRegionMaps){
        this.pathRegionMaps = pathRegionMaps;
        this.config = config;
        this.configGlobal = Objects.requireNonNullElseGet(
                config.getConfigurationSection("global"),
                () -> this.config.createSection("global")
        );
        this.mapsList = this.config.getStringList("maps");
        this.maps = new HashMap<>();
        for (String mapId : this.mapsList) {
            this.maps.putIfAbsent(mapId, new RegionMap(
                    this.pathRegionMaps + "/" + mapId + ".yml",
                    this.pathRegionMaps + "/" + mapId
            ));
        }
    }

    public void RegionMap(@NonNull String pathConfig, @NonNull String pathRegionMaps){
        this.pathRegionMaps = pathRegionMaps;
        this.config = YamlConfiguration.loadConfiguration(new File(pathConfig));
        this.configGlobal = Objects.requireNonNullElseGet(
                config.getConfigurationSection("global"),
                () -> config.createSection("global")
        );
        this.mapsList = this.config.getStringList("maps");
        this.maps = new HashMap<>();
        for (String mapId : this.mapsList) {
            this.maps.putIfAbsent(mapId, new RegionMap(
                    this.pathRegionMaps + "/" + mapId + ".yml",
                    this.pathRegionMaps + "/" + mapId
            ));
        }
    }

    public void saveMaps() throws IOException {
        List<Exception> es = new LinkedList<>();
        for (String mapId : this.mapsList) {
            try {
                this.maps.get(mapId).save();
            } catch (Exception e) {
                es.add(e);
            }
        }
        if (!es.isEmpty()) {
            throw new IOException("Save region maps failed.", es.get(0));
        }
    }

}
