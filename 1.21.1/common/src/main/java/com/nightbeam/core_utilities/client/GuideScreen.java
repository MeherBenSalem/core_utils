package com.nightbeam.core_utilities.client;

import com.nightbeam.core_utilities.init.CoreUtilitiesModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

/** The Core Utilities Manual: a chapter list on the left, the chapter text on the right. */
public class GuideScreen extends Screen {
	private record Chapter(String key, Supplier<Item> icon) {
	}

	private static final List<Chapter> CHAPTERS = List.of(
			new Chapter("welcome", () -> CoreUtilitiesModItems.GUIDE_BOOK.get()),
			new Chapter("energy", () -> CoreUtilitiesModItems.MULTIMETER.get()),
			new Chapter("solar", () -> CoreUtilitiesModItems.SOLAR_PANEL.get()),
			new Chapter("combustion", () -> CoreUtilitiesModItems.COMBUSTION_GENERATOR.get()),
			new Chapter("geothermal", () -> CoreUtilitiesModItems.GEOTHERMAL_GENERATOR.get()),
			new Chapter("cells", () -> CoreUtilitiesModItems.ENERGY_CELL_BASIC.get()),
			new Chapter("cables", () -> CoreUtilitiesModItems.CABLE.get()),
			new Chapter("pipes", () -> CoreUtilitiesModItems.ITEM_PIPE.get()),
			new Chapter("machines", () -> CoreUtilitiesModItems.CONFIGURATION_WRENCH.get()),
			new Chapter("upgrades", () -> CoreUtilitiesModItems.SPEED_UPGRADE.get()),
			new Chapter("ores", () -> CoreUtilitiesModItems.CRUSHER.get()),
			new Chapter("alloys", () -> CoreUtilitiesModItems.ALLOY_SMELTER.get()),
			new Chapter("quarry", () -> CoreUtilitiesModItems.QUARRY.get()),
			new Chapter("farming", () -> CoreUtilitiesModItems.CROP_HARVESTER.get()),
			new Chapter("grinder", () -> CoreUtilitiesModItems.MOB_GRINDER.get()),
			new Chapter("breaker", () -> CoreUtilitiesModItems.BLOCK_BREAKER.get()),
			new Chapter("utilities", () -> CoreUtilitiesModItems.VECTOR_PLATE_TIER_2.get()));

	private static final int WIDTH = 330;
	private static final int HEIGHT = 222;
	private static final int LIST_WIDTH = 112;
	private static final int ROW = 11;
	private static int selected;
	private int scroll;
	private int left;
	private int top;

	public GuideScreen() {
		super(Component.translatable("item.core_utilities.guide_book"));
	}

	@Override
	protected void init() {
		left = (width - WIDTH) / 2;
		top = (height - HEIGHT) / 2;
		addRenderableWidget(Button.builder(Component.literal("<"), b -> select(selected - 1)).bounds(left + LIST_WIDTH + 8, top + HEIGHT - 24, 20, 18).build());
		addRenderableWidget(Button.builder(Component.literal(">"), b -> select(selected + 1)).bounds(left + WIDTH - 28, top + HEIGHT - 24, 20, 18).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose()).bounds(left + LIST_WIDTH + (WIDTH - LIST_WIDTH) / 2 - 30, top + HEIGHT - 24, 60, 18).build());
	}

	private void select(int index) {
		selected = Math.floorMod(index, CHAPTERS.size());
		scroll = 0;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.renderItem(new ItemStack(CoreUtilitiesModItems.GUIDE_BOOK.get()), left + 3, top + 2);
		graphics.drawString(font, title, left + 23, top + 6, 0xFFFFFFFF, true);
		for (int i = 0; i < CHAPTERS.size(); i++) {
			int y = top + 24 + i * ROW;
			boolean hover = mouseX >= left && mouseX < left + LIST_WIDTH && mouseY >= y - 1 && mouseY < y + ROW - 1;
			if (i == selected) {
				graphics.fill(left + 2, y - 1, left + LIST_WIDTH - 2, y + ROW - 1, 0xFF2E6F9E);
			} else if (hover) {
				graphics.fill(left + 2, y - 1, left + LIST_WIDTH - 2, y + ROW - 1, 0xFF333944);
			}
			Component name = Component.translatable("guide.core_utilities." + CHAPTERS.get(i).key() + ".title");
			graphics.drawString(font, font.plainSubstrByWidth(name.getString(), LIST_WIDTH - 10), left + 6, y + 1, i == selected ? 0xFFFFFFFF : 0xFFB8BEC8, false);
		}
		Chapter chapter = CHAPTERS.get(selected);
		int textLeft = left + LIST_WIDTH + 10;
		int textWidth = WIDTH - LIST_WIDTH - 20;
		graphics.renderItem(new ItemStack(chapter.icon().get()), textLeft, top + 24);
		graphics.drawString(font, Component.translatable("guide.core_utilities." + chapter.key() + ".title"), textLeft + 20, top + 28, 0xFFFFD27A, true);
		List<FormattedCharSequence> lines = font.split(Component.translatable("guide.core_utilities." + chapter.key() + ".text"), textWidth);
		int maxLines = (HEIGHT - 48 - 30) / 10;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, lines.size() - maxLines)));
		for (int i = 0; i < Math.min(maxLines, lines.size() - scroll); i++) {
			graphics.drawString(font, lines.get(i + scroll), textLeft, top + 46 + i * 10, 0xFFE6E9EF, false);
		}
		if (lines.size() > maxLines) {
			String more = (scroll + 1) + "/" + (lines.size() - maxLines + 1);
			graphics.drawString(font, more, left + WIDTH - 8 - font.width(more), top + 28, 0xFF8A919C, false);
		}
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(graphics);
		graphics.fill(left - 2, top - 2, left + WIDTH + 2, top + HEIGHT + 2, 0xFF0B0D10);
		graphics.fill(left, top, left + WIDTH, top + HEIGHT, 0xFF22262D);
		graphics.fill(left, top, left + LIST_WIDTH, top + HEIGHT, 0xFF1A1D23);
		graphics.fill(left, top, left + WIDTH, top + 20, 0xFF2E6F9E);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (int i = 0; i < CHAPTERS.size(); i++) {
			int y = top + 24 + i * ROW;
			if (mouseX >= left && mouseX < left + LIST_WIDTH && mouseY >= y - 1 && mouseY < y + ROW - 1) {
				select(i);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		scroll -= (int) Math.signum(scrollY);
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
