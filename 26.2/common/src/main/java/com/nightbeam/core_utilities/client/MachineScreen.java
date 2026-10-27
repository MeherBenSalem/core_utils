package com.nightbeam.core_utilities.client;

import com.nightbeam.core_utilities.machine.MachineBlockEntity;
import com.nightbeam.core_utilities.machine.MachineMenu;
import com.nightbeam.core_utilities.machine.MachineSpec;
import com.nightbeam.core_utilities.machine.RedstoneMode;
import com.nightbeam.core_utilities.machine.RelativeSide;
import com.nightbeam.core_utilities.machine.SideMode;
import com.nightbeam.core_utilities.util.Compat;
import com.nightbeam.core_utilities.util.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/** Generic screen for every machine; draws whatever its {@link MachineSpec} describes. */
public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
	private static final Identifier TEXTURE = Compat.id("textures/gui/machine.png");
	public static final int SIDE_PANEL_WIDTH = 62;
	private static final int[][] FACE_POS = {{23, 30}, {41, 48}, {5, 30}, {41, 30}, {23, 12}, {23, 48}};
	private static final int[] MODE_COLORS = {0xFF5A5F66, 0xFF3D7BD9, 0xFFE08A1E, 0xFF3FAF55};

	public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
		this.inventoryLabelY = 72;
	}

	public int left() {
		return leftPos;
	}

	public int top() {
		return topPos;
	}

	private MachineSpec spec() {
		return menu.getSpec();
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		List<Component> tooltip = extraTooltip(mouseX - leftPos, mouseY - topPos);
		if (!tooltip.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
		}
	}

	private static void blit(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int width, int height) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, u, v, width, height, 256, 256);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int x = leftPos, y = topPos;
		blit(graphics, x, y, 0, 0, imageWidth, imageHeight);
		for (MachineSpec.SlotDef slot : spec().slots()) {
			if (slot.large()) {
				blit(graphics, x + slot.x(), y + slot.y(), 176, 18, 26, 26);
			} else {
				blit(graphics, x + slot.x(), y + slot.y(), 176, 0, 18, 18);
			}
		}
		if (spec().hasEnergyBar()) {
			blit(graphics, x + 8, y + 16, 202, 0, 14, 52);
			long capacity = Integer.toUnsignedLong(menu.getCapacity());
			long energy = Integer.toUnsignedLong(menu.getEnergy());
			int filled = capacity > 0 ? (int) Math.min(50, 50 * energy / capacity) : 0;
			if (filled > 0) {
				blit(graphics, x + 9, y + 17 + 50 - filled, 216, 1 + 50 - filled, 12, filled);
			}
		}
		int progress = menu.getData(MachineBlockEntity.DATA_PROGRESS);
		int max = menu.getData(MachineBlockEntity.DATA_MAX_PROGRESS);
		if (spec().progress() == MachineSpec.Progress.ARROW) {
			blit(graphics, x + spec().progressX(), y + spec().progressY(), 176, 52, 24, 17);
			int width = max > 0 ? Math.min(24, 24 * progress / max) : 0;
			if (width > 0) {
				blit(graphics, x + spec().progressX(), y + spec().progressY(), 200, 52, width, 17);
			}
		} else if (spec().progress() == MachineSpec.Progress.FLAME) {
			blit(graphics, x + spec().progressX(), y + spec().progressY(), 176, 70, 14, 14);
			int height = max > 0 ? Math.min(14, (int) Math.ceil(14.0 * progress / max)) : 0;
			if (height > 0) {
				blit(graphics, x + spec().progressX(), y + spec().progressY() + 14 - height, 190, 70 + 14 - height, 14, height);
			}
		}
		// Left tabs: redstone mode and auto-eject
		blit(graphics, x - 22, y + 4, 176, 104, 22, 22);
		blit(graphics, x - 19, y + 7, 176 + 16 * menu.getData(MachineBlockEntity.DATA_REDSTONE), 84, 16, 16);
		if (spec().hasSideConfig() && !spec().slots().isEmpty()) {
			blit(graphics, x - 22, y + 28, 176, 104, 22, 22);
			blit(graphics, x - 19, y + 31, menu.getData(MachineBlockEntity.DATA_AUTO_EJECT) != 0 ? 224 : 240, 84, 16, 16);
		}
		if (spec().hasSideConfig()) {
			blit(graphics, x + imageWidth, y + 4, 0, 166, SIDE_PANEL_WIDTH, 70);
			for (RelativeSide side : RelativeSide.values()) {
				int fx = x + imageWidth + FACE_POS[side.ordinal()][0];
				int fy = y + 4 + FACE_POS[side.ordinal()][1];
				SideMode mode = menu.getSide(side);
				graphics.fill(fx, fy, fx + 16, fy + 16, 0xFF1B1E22);
				graphics.fill(fx + 1, fy + 1, fx + 15, fy + 15, MODE_COLORS[mode.ordinal()]);
				String letter = Component.translatable("gui.core_utilities.side.short." + side.name().toLowerCase(java.util.Locale.ROOT)).getString();
				graphics.text(font, letter, fx + 8 - font.width(letter) / 2, fy + 4, 0xFFFFFFFF, true);
			}
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, 0xFFE6E9EF, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFB8BEC8, false);
		String status = statusLine();
		if (!status.isEmpty()) {
			graphics.text(font, status, 28, 62, 0xFF9FE0A8, false);
		}
		if (spec().hasSideConfig()) {
			Component label = Component.translatable("gui.core_utilities.sides");
			graphics.text(font, label, imageWidth + SIDE_PANEL_WIDTH / 2 - font.width(label) / 2, 7, 0xFFE6E9EF, false);
		}
	}

	private String statusLine() {
		String name = spec().name();
		int extra = menu.getData(MachineBlockEntity.DATA_EXTRA);
		int usage = menu.getData(MachineBlockEntity.DATA_USAGE);
		if (name.equals("geothermal_generator")) {
			return Component.translatable("gui.core_utilities.lava_sources", extra).getString();
		}
		if (name.equals("quarry")) {
			return Component.translatable("gui.core_utilities.quarry_layer", (short) extra).getString();
		}
		if (usage > 0) {
			boolean generator = name.contains("generator");
			return (generator ? "+" : "-") + Tooltips.formatEnergy(usage) + " FE/t";
		}
		return "";
	}

	private List<Component> extraTooltip(int mx, int my) {
		List<Component> lines = new ArrayList<>();
		if (spec().hasEnergyBar() && mx >= 8 && mx < 22 && my >= 16 && my < 68) {
			long capacity = Integer.toUnsignedLong(menu.getCapacity());
			long energy = Integer.toUnsignedLong(menu.getEnergy());
			lines.add(Component.translatable("tooltip.core_utilities.energy", Tooltips.formatEnergy(energy), Tooltips.formatEnergy(capacity)).withStyle(ChatFormatting.GOLD));
		}
		if (mx >= -22 && mx < 0 && my >= 4 && my < 26) {
			RedstoneMode mode = RedstoneMode.byId(menu.getData(MachineBlockEntity.DATA_REDSTONE));
			lines.add(Component.translatable("gui.core_utilities.redstone." + mode.name().toLowerCase(java.util.Locale.ROOT)));
			lines.add(Component.translatable("gui.core_utilities.click_to_cycle").withStyle(ChatFormatting.DARK_GRAY));
		}
		if (spec().hasSideConfig() && !spec().slots().isEmpty() && mx >= -22 && mx < 0 && my >= 28 && my < 50) {
			lines.add(Component.translatable(menu.getData(MachineBlockEntity.DATA_AUTO_EJECT) != 0 ? "gui.core_utilities.auto_eject.on" : "gui.core_utilities.auto_eject.off"));
		}
		RelativeSide side = sideAt(mx, my);
		if (side != null) {
			SideMode mode = menu.getSide(side);
			lines.add(Component.translatable("gui.core_utilities.side." + side.name().toLowerCase(java.util.Locale.ROOT)));
			lines.add(Component.translatable("gui.core_utilities.mode." + mode.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.GRAY));
		}
		return lines;
	}

	private RelativeSide sideAt(int mx, int my) {
		if (!spec().hasSideConfig()) {
			return null;
		}
		for (RelativeSide side : RelativeSide.values()) {
			int fx = imageWidth + FACE_POS[side.ordinal()][0];
			int fy = 4 + FACE_POS[side.ordinal()][1];
			if (mx >= fx && mx < fx + 16 && my >= fy && my < fy + 16) {
				return side;
			}
		}
		return null;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		int mx = (int) event.x() - leftPos;
		int my = (int) event.y() - topPos;
		int id = -1;
		if (mx >= -22 && mx < 0 && my >= 4 && my < 26) {
			id = MachineBlockEntity.BUTTON_REDSTONE;
		} else if (spec().hasSideConfig() && !spec().slots().isEmpty() && mx >= -22 && mx < 0 && my >= 28 && my < 50) {
			id = MachineBlockEntity.BUTTON_AUTO_EJECT;
		} else {
			RelativeSide side = sideAt(mx, my);
			if (side != null) {
				id = MachineBlockEntity.BUTTON_SIDE + side.ordinal();
			}
		}
		if (id >= 0 && minecraft != null && minecraft.gameMode != null) {
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
		boolean inSidePanel = spec().hasSideConfig() && mouseX >= left + imageWidth && mouseX < left + imageWidth + SIDE_PANEL_WIDTH && mouseY >= top + 4 && mouseY < top + 74;
		boolean inTabs = mouseX >= left - 22 && mouseX < left && mouseY >= top + 4 && mouseY < top + 50;
		return super.hasClickedOutside(mouseX, mouseY, left, top) && !inSidePanel && !inTabs;
	}
}
