package net.tarcadia.tribina.plugin.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.HashSet;
import net.tarcadia.tribina.plugin.util.Pair;
import java.nio.file.Path;
import java.util.Comparator;

public class RegionMapUtilTest {
	@TempDir
	static Path folder;

	@Test
	public void testSaveAndLoad() throws Exception {
		var file = folder.resolve("test.bmp").toFile();
		var region = new HashSet<Pair<Integer, Integer>>();
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				if ((i * j) % 2 + (i * j) % 3 == 0) {
					region.add(new Pair<>(i, j));
				}
			}
		}
		RegionMapUtil.saveRegionToFile(region, file);
		var result = RegionMapUtil.loadRegionFromFile(file);
		assertEquals(region, result);
	}
}
