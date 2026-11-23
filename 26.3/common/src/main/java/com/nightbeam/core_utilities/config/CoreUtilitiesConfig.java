package com.nightbeam.core_utilities.config;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.platform.Services;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Plain {@code config/core_utilities.properties} file shared by every loader. Missing keys are written back
 * with their defaults so the file always documents every option.
 */
public class CoreUtilitiesConfig {
	private static CoreUtilitiesConfig instance;

	// Generators (FE per tick)
	public int solarTier1Rate = 20;
	public int solarTier2Rate = 80;
	public int solarTier3Rate = 320;
	public int solarTier4Rate = 1280;
	public int combustionGeneratorRate = 40;
	public int geothermalRatePerLava = 15;
	// Cables (FE per tick per network)
	public int cableBasicRate = 512;
	public int cableAdvancedRate = 4096;
	public int cableEliteRate = 32768;
	// Energy cells (capacity FE / transfer FE per tick)
	public int cellBasicCapacity = 1_000_000;
	public int cellBasicRate = 2_000;
	public int cellAdvancedCapacity = 8_000_000;
	public int cellAdvancedRate = 16_000;
	public int cellEliteCapacity = 64_000_000;
	public int cellEliteRate = 128_000;
	// Machines
	public double machineEnergyMultiplier = 1.0;
	public double machineSpeedMultiplier = 1.0;
	public int quarryEnergyPerBlock = 400;
	public int quarryBaseRadius = 4;
	public int quarryMaxRadius = 16;
	public int harvesterEnergyPerAction = 60;
	public int mobGrinderEnergyPerHit = 200;
	public float mobGrinderDamage = 6.0f;
	// Item pipes
	public int itemPipeBasicItems = 4;
	public int itemPipeBasicInterval = 10;
	public int itemPipeRapidItems = 32;
	public int itemPipeRapidInterval = 5;

	public static CoreUtilitiesConfig get() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	private static CoreUtilitiesConfig load() {
		CoreUtilitiesConfig config = new CoreUtilitiesConfig();
		Path file;
		try {
			file = Services.PLATFORM.getConfigDir().resolve(CoreUtilitiesMod.MOD_ID + ".properties");
		} catch (Throwable t) {
			return config;
		}
		Properties properties = new Properties();
		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file)) {
				properties.load(reader);
			} catch (IOException e) {
				CoreUtilitiesMod.LOGGER.warn("Could not read {}, using defaults", file, e);
			}
		}
		boolean missing = false;
		for (Field field : CoreUtilitiesConfig.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			String value = properties.getProperty(field.getName());
			try {
				if (value == null) {
					properties.setProperty(field.getName(), String.valueOf(field.get(config)));
					missing = true;
				} else if (field.getType() == int.class) {
					field.setInt(config, Math.max(0, Integer.parseInt(value.trim())));
				} else if (field.getType() == double.class) {
					field.setDouble(config, Math.max(0.01, Double.parseDouble(value.trim())));
				} else if (field.getType() == float.class) {
					field.setFloat(config, Math.max(0f, Float.parseFloat(value.trim())));
				}
			} catch (ReflectiveOperationException | NumberFormatException e) {
				CoreUtilitiesMod.LOGGER.warn("Invalid value '{}' for {} in {}, using default", value, field.getName(), file);
			}
		}
		if (missing) {
			try {
				Files.createDirectories(file.getParent());
				try (Writer writer = Files.newBufferedWriter(file)) {
					properties.store(writer, "Core Utilities configuration. Energy values are FE (Forge Energy / RF compatible).");
				}
			} catch (IOException e) {
				CoreUtilitiesMod.LOGGER.warn("Could not write {}", file, e);
			}
		}
		return config;
	}
}
