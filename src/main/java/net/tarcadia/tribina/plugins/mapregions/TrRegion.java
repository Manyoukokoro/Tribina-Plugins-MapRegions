package net.tarcadia.tribina.plugins.mapregions;

import net.tarcadia.tribina.plugins.utils.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TrRegion {

    private final Set<Pair<Integer, Integer>> posSet;
    private final ConfigurationSection config;

    public TrRegion(@NonNull ConfigurationSection config) {
        this.posSet = new HashSet<>();
        this.config = config;
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
