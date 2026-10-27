package com.nightbeam.core_utilities.compat.jei;

import com.nightbeam.core_utilities.recipe.MachineRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

/** One category per processing machine: inputs, animated arrow, main output, optional by-product with chance. */
public class MachineRecipeCategory implements IRecipeCategory<MachineRecipe> {
	private final RecipeType<MachineRecipe> type;
	private final Component title;
	private final IDrawable icon;
	private final IDrawable background;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final int energyPerTick;
	private final boolean twoInputs;

	public MachineRecipeCategory(IGuiHelper helper, RecipeType<MachineRecipe> type, ItemLike machine, int energyPerTick, boolean twoInputs) {
		this.type = type;
		this.title = Component.translatable(machine.asItem().getDescriptionId());
		this.icon = helper.createDrawableItemStack(new ItemStack(machine));
		this.background = helper.createBlankDrawable(120, 44);
		this.slot = helper.getSlotDrawable();
		this.arrow = helper.createAnimatedRecipeArrow(40);
		this.energyPerTick = energyPerTick;
		this.twoInputs = twoInputs;
	}

	@Override
	public RecipeType<MachineRecipe> getRecipeType() {
		return type;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
		for (int i = 0; i < recipe.inputs().size(); i++) {
			MachineRecipe.CountedIngredient input = recipe.inputs().get(i);
			List<ItemStack> stacks = Arrays.stream(input.ingredient().get().getItems()).map(s -> s.copyWithCount(input.count())).toList();
			int y = twoInputs ? 2 + i * 22 : 13;
			builder.addSlot(RecipeIngredientRole.INPUT, 5, y).setStandardSlotBackground().addItemStacks(stacks);
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 13).setStandardSlotBackground().addItemStack(recipe.result());
		ItemStack byproduct = recipe.byproduct();
		if (!byproduct.isEmpty()) {
			IRecipeSlotBuilder extra = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 13).setStandardSlotBackground().addItemStack(byproduct);
			int percent = Math.round(recipe.secondaryChance() * 100);
			extra.addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.core_utilities.chance", percent).withStyle(ChatFormatting.GOLD)));
		}
	}

	@Override
	public void draw(MachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		arrow.draw(graphics, 32, 14);
		var font = Minecraft.getInstance().font;
		String seconds = String.format(java.util.Locale.ROOT, "%.1f", recipe.ticks() / 20.0);
		graphics.drawString(font, Component.translatable("jei.core_utilities.ticks", seconds), 28, 34, 0xFF808080, false);
		String energy = Component.translatable("jei.core_utilities.energy", recipe.ticks() * energyPerTick).getString();
		graphics.drawString(font, energy, 120 - font.width(energy), 34, 0xFF808080, false);
	}
}
