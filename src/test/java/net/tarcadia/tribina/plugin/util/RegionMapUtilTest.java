package net.tarcadia.tribina.plugin.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.Set;
import java.util.TreeSet;
import net.tarcadia.tribina.plugin.util.Pair;
import java.nio.file.Path;
import java.util.Comparator;

public class RegionMapUtilTest {
	@TempDir
	static Path folder;

	@Test
	public void testSave() throws Exception {
		var region = new TreeSet<Pair<Integer, Integer>>(Comparator.comparingInt(Pair<Integer, Integer>::x).thenComparingInt(Pair<Integer, Integer>::y));
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				if ((i * j) % 2 + (i * j) % 3 == 0) {
					region.add(new Pair<>(i, j));
				}
			}
		}
		RegionMapUtil.saveRegionToFile(region, folder.resolve("test.bmp").toFile());
	}
}
