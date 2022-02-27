package net.tarcadia.tribina.plugin.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.util.Set;
import net.tarcadia.tribina.plugin.util.Pair;
import java.io.File;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegionMapUtil {
	/**
	 * Save a region to a BMP file.
	 * @param region the region to be saved
	 * @param file destination BMP file
	 * @throws IOException If an input or output exception occurred
	 * @throws IllegalArgumentException If the region is invalid
	 * @throws Exception Other reasons
	 */
	public static void saveRegionToFile(@NonNull Set<Pair<Integer, Integer>> region, @NonNull File file) throws IOException, IllegalArgumentException, Exception {
		int width = 0;
		int height = 0;
		for (var pair : region) {
			int x = pair.x();
			int y = pair.y();
			if (x < 0 || y < 0) {
				throw new IllegalArgumentException("Invalid pos found: " + pair);
			}
			if (x + 1 > width) {
				width = x + 1;
			}
			if (y + 1 > height) {
				height = y + 1;
			}
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		for (var pair : region) {
			image.setRGB(pair.x(), pair.y(), 0xffffff);
		}
		if (!ImageIO.write(image, "BMP", file)) {
			throw new Exception("No appropriate writer is found");
		}
	}
}
