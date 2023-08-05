package minefantasy.mfr.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import minefantasy.mfr.api.heating.Heatable;
import minefantasy.mfr.recipe.AnvilRecipeBase;
import minefantasy.mfr.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a JEI "recipe" for the MFR anvil.
 */
public class JEIAnvilRecipe implements IRecipeWrapper {

	private final ItemStack result;
	protected final AnvilRecipeBase recipe;
	private final List<List<ItemStack>> ingredients;

	public JEIAnvilRecipe(AnvilRecipeBase recipe, IStackHelper stackHelper) {
		List<List<ItemStack>> ingredients = stackHelper.expandRecipeItemStackInputs(recipe.inputs);
		this.recipe = recipe;
		this.result = recipe.getAnvilRecipeOutput();
		this.ingredients = ingredients;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, this.ingredients);
		ingredients.setOutput(VanillaTypes.ITEM, result);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		if (minecraft.currentScreen != null) {

			// add hot output icon
			if (recipe.hotOutput) {
				GuiHelper.drawHotItemIcon(minecraft,143, 28);
			}

			// add hot input icons
			Map<Integer, Map<Integer, Integer>> ingredientMap = new HashMap<>();

			int width = AnvilRecipeBase.WIDTH;
			int height = AnvilRecipeBase.HEIGHT;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int slot = y * width + x;
					Map<Integer, Integer> posMap = new HashMap<>();
					// x and y pos of the current ingredient
					posMap.put(1 + x * 18, 1 + y * 18);
					ingredientMap.put(slot, posMap);
				}
			}

			for (int j = 0; j < ingredients.size(); j++) {
				List<ItemStack> stacks = ingredients.get(j);
				if (!stacks.isEmpty()) {
					ItemStack stack = (stacks.get(0));
					if (stack != null && !stack.isEmpty() && Heatable.canHeatItem(ingredients.get(j).get(0))) {
						GuiHelper.drawHotItemIcon(minecraft, ingredientMap.get(j).keySet().iterator().next(), ingredientMap.get(j).values().iterator().next());
					}
				}
			}
		}

		// draw tool icon with required tier int
		GuiHelper.renderToolIcon(minecraft.currentScreen, recipe.toolType, recipe.hammerTier, recipeWidth - 23, recipeHeight - 98, true);

		// draw bench icon with required tier int
		GuiHelper.renderToolIcon(minecraft.currentScreen, "anvil", recipe.anvilTier, recipeWidth - 23, recipeHeight - 48, true);

		//		minecraft.fontRenderer.drawString("X:" + mouseX + ", Y: " + mouseY, mouseX, mouseY, 16777215);

		if (isPointInRegion(recipeWidth - 23, recipeHeight - 98, 20, 20, mouseX, mouseY, 0, 0)) {
			// Shows the tool tooltip text with the name of the tool and the minimum tier
			String s2 = I18n.format("tooltype." + recipe.toolType) + ", " + (recipe.hammerTier > -1
					? I18n.format("attribute.mfcrafttier.name") + " " + recipe.hammerTier
					: I18n.format("attribute.nomfcrafttier.name"));
			minecraft.fontRenderer.drawStringWithShadow(s2, (float) ((recipeWidth / 2) - minecraft.fontRenderer.getStringWidth(s2) / 2), (float) 84, 16777215);
		} else if (isPointInRegion(recipeWidth - 23, recipeHeight - 48, 20, 20, mouseX, mouseY, 0, 0)) {
			// Shows the anvil tooltip text with the minimum anvil tier
			String s2 = I18n.format("tooltype.anvil") + ", " + (recipe.anvilTier > -1
					? I18n.format("attribute.mfcrafttier.name") + " " + recipe.anvilTier
					: I18n.format("attribute.nomfcrafttier.name"));
			minecraft.fontRenderer.drawStringWithShadow(s2, (float) ((recipeWidth / 2) - minecraft.fontRenderer.getStringWidth(s2) / 2), (float) 84, 16777215);
		} else {
			// Just display the required Skill type of for this recipe
			minecraft.fontRenderer.drawStringWithShadow(recipe.requiredSkill.getDisplayName(), (float) ((recipeWidth / 2) - minecraft.fontRenderer.getStringWidth(recipe.requiredSkill.getDisplayName()) / 2), (float) 84, 16777215);
		}
	}

	/**
	 * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
	 * pointY
	 */
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY, int guiLeft, int guiTop) {
		int i = guiLeft;
		int j = guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}

}
