package com.nightbeam.core_utilities.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.text.NumberFormat;
import java.util.Locale;

public final class Tooltips {
	private Tooltips() {
	}

	public static String formatEnergy(long value) {
		if (value >= 1_000_000_000L) {
			return String.format(Locale.ROOT, "%.2fG", value / 1_000_000_000.0);
		}
		if (value >= 1_000_000L) {
			return String.format(Locale.ROOT, "%.2fM", value / 1_000_000.0);
		}
		if (value >= 10_000L) {
			return String.format(Locale.ROOT, "%.1fk", value / 1_000.0);
		}
		return NumberFormat.getIntegerInstance(Locale.ROOT).format(value);
	}

	public static MutableComponent rate(String key, int perTick) {
		return Component.translatable(key, formatEnergy(perTick)).withStyle(ChatFormatting.AQUA);
	}

	public static MutableComponent energy(int stored, int capacity) {
		return Component.translatable("tooltip.core_utilities.energy", formatEnergy(stored), formatEnergy(capacity)).withStyle(ChatFormatting.GOLD);
	}
}
