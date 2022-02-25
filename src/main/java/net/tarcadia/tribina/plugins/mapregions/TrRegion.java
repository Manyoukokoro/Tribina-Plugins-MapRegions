package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public class TrRegion {

    private Set<Pair<Integer, Integer>> posSet;
    private ConfigurationSection config;

    public TrRegion(@NonNull ConfigurationSection config) {
        this.posSet = new HashSet<>();
        this.config = config;
    }

    public boolean setConfig(@NonNull ConfigurationSection config) {
        this.config = config;
        return true;
    }

    public boolean setValue(@NonNull String key, Object obj) {
        this.config.set(key, obj);
        return true;
    }

    public ConfigurationSection getConfig() {
        return this.config;
    }

    public Object getValue(@NonNull String key) {
        return this.config.get(key);
    }

    public Set<Pair<Integer, Integer>> getPosSet()
    {
        return this.posSet;
    }

    public boolean addPos(int x, int y)
    {
        return this.posSet.add(new Pair<>(x, y));
    }

    public boolean addPos(Pair<Integer, Integer> pos)
    {
        return this.posSet.add(pos.clone());
    }

    public boolean addPosSet(Set<Pair<Integer, Integer>> posSet)
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

    public boolean removePosSet(Set<Pair<Integer, Integer>> posSet)
    {
        return this.posSet.removeAll(posSet);
    }

    public boolean retainPosSet(Set<Pair<Integer, Integer>> posSet) { return this.posSet.retainAll(posSet); }
}
