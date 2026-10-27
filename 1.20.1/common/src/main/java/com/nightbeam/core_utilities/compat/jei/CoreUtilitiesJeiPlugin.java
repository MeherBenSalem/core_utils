package com.nightbeam.core_utilities.compat.jei;

import com.nightbeam.core_utilities.client.MachineScreen;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItems;
import com.nightbeam.core_utilities.recipe.MachineRecipe;
import com.nightbeam.core_utilities.recipe.MachineRecipes;
import com.nightbeam.core_utilities.recipe.RecipeKind;
import com.nightbeam.core_utilities.util.Compat;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CoreUtilitiesJeiPlugin implements IModPlugin {
	public static final RecipeType<MachineRecipe> CRUSHING = RecipeType.create("core_utilities", "crushing", MachineRecipe.class);
	public static final RecipeType<MachineRecipe> ALLOYING = RecipeType.create("core_utilities", "alloying", MachineRecipe.class);
	public static final RecipeType<MachineRecipe> COMPRESSING = RecipeType.create("core_utilities", "compressing", MachineRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return Compat.id("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new MachineRecipeCategory(helper, CRUSHING, CoreUtilitiesModBlocks.CRUSHER.get(), 24, false),
				new MachineRecipeCategory(helper, ALLOYING, CoreUtilitiesModBlocks.ALLOY_SMELTER.get(), 32, true),
				new MachineRecipeCategory(helper, COMPRESSING, CoreUtilitiesModBlocks.COMPRESSOR.get(), 24, false));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(CRUSHING, MachineRecipes.get(RecipeKind.CRUSHING));
		registration.addRecipes(ALLOYING, MachineRecipes.get(RecipeKind.ALLOYING));
		registration.addRecipes(COMPRESSING, MachineRecipes.get(RecipeKind.COMPRESSING));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(CoreUtilitiesModItems.CRUSHER.get()), CRUSHING);
		registration.addRecipeCatalyst(new ItemStack(CoreUtilitiesModItems.ALLOY_SMELTER.get()), ALLOYING);
		registration.addRecipeCatalyst(new ItemStack(CoreUtilitiesModItems.COMPRESSOR.get()), COMPRESSING);
		registration.addRecipeCatalyst(new ItemStack(CoreUtilitiesModItems.ELECTRIC_FURNACE.get()), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(CoreUtilitiesModItems.COMBUSTION_GENERATOR.get()), RecipeTypes.FUELING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(MachineScreen.class, new IGuiContainerHandler<>() {
			@Override
			public List<Rect2i> getGuiExtraAreas(MachineScreen screen) {
				List<Rect2i> areas = new ArrayList<>();
				areas.add(new Rect2i(screen.left() - 22, screen.top() + 4, 22, 46));
				areas.add(new Rect2i(screen.left() + 176, screen.top() + 4, MachineScreen.SIDE_PANEL_WIDTH, 70));
				return areas;
			}
		});
	}
}
