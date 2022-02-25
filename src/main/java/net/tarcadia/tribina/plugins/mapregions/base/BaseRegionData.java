package net.tarcadia.tribina.plugins.mapregions.base;

import org.bukkit.configuration.ConfigurationSection;

public interface BaseRegionData {
    void setConfig(ConfigurationSection config);
    ConfigurationSection getConfig();
}
