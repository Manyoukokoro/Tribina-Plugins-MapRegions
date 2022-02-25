package net.tarcadia.tribina.plugins.utils;

public record Pair<X, Y>(X x, Y y) {

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Pair<X, Y> copy() { return new Pair<>(this.x, this.y); }
}
