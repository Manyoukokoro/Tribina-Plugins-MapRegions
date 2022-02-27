package net.tarcadia.tribina.plugin.util;

public final class Pair<X, Y> implements Cloneable {
	private final X x;
	private final Y y;

	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return this.x;
	}

	public Y getY() {
		return this.y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public Pair<X, Y> clone() {
		return new Pair<>(this.x, this.y);
	}

	@Override
	public int hashCode() {
		return this.x.hashCode() + this.y.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair pair = (Pair)o;
		return this.x.equals(pair.x) && this.y.equals(pair.y);
	}
}
