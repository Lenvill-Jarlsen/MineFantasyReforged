package minefantasy.mfr.recipe;

import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Skill;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author AnonymousProductions
 */
public class CraftingManagerCarpenter {
	/**
	 * The static instance of this class
	 */
	private static final CraftingManagerCarpenter instance = new CraftingManagerCarpenter();

	/**
	 * A list of all the recipes added
	 */
	public List recipes = new ArrayList();
	public HashMap<String, Object> recipeMap = new HashMap<>();

	private CraftingManagerCarpenter() {
		Collections.sort(this.recipes, new RecipeSorterCarpenter(this));
	}

	/**
	 * Returns the static instance of this class
	 */
	public static CraftingManagerCarpenter getInstance() {
		return instance;
	}

	public static ICarpenterRecipe getRecipeByName(String name) {
		return (ICarpenterRecipe) getInstance().recipeMap.get(name);
	}

	public ICarpenterRecipe addRecipe(String name, ItemStack output, Skill skill, String research, SoundEvent sound, float experience, String toolType, int toolTier, int blockTier, int time, byte id, Object... input) {
		StringBuilder recipeString = new StringBuilder();
		int inputSelector = 0;
		int width = 0;
		int height = 0;
		int keyStringsLength;

		if (input[inputSelector] instanceof String[]) {
			String[] keyStrings = ((String[]) input[inputSelector++]);
			keyStringsLength = keyStrings.length;

			for (int i = 0; i < keyStringsLength; ++i) {
				String var11 = keyStrings[i];
				++height;
				width = var11.length();
				recipeString.append(var11);
			}
		} else {
			while (input[inputSelector] instanceof String) {
				String keyStringBuilder = (String) input[inputSelector++];
				++height;
				width = keyStringBuilder.length();
				recipeString.append(keyStringBuilder);
			}
		}

		HashMap keyHashMap;

		for (keyHashMap = new HashMap(); inputSelector < input.length; inputSelector += 2) {
			Character inputCharacter = (Character) input[inputSelector];
			ItemStack inputItem = ItemStack.EMPTY;
			ItemStack[] inputItems = null;

			if (input[inputSelector + 1] instanceof Item) {
				inputItem = new ItemStack((Item) input[inputSelector + 1], 1, 32767);
			} else if (input[inputSelector + 1] instanceof Block) {
				inputItem = new ItemStack((Block) input[inputSelector + 1], 1, 32767);
			} else if (input[inputSelector + 1] instanceof ItemStack) {
				inputItem = (ItemStack) input[inputSelector + 1];
			} else if (input[inputSelector + 1] instanceof ItemStack[]){
				inputItems = ((ItemStack[]) input[inputSelector + 1]);
			}

			if (!inputItem.isEmpty()){
				keyHashMap.put(inputCharacter, inputItem);
			}
			else {
				keyHashMap.put(inputCharacter, inputItems);
			}
		}

		ItemStack[] inputs = new ItemStack[width * height];

		for (keyStringsLength = 0; keyStringsLength < width * height; ++keyStringsLength) {
			char charAt = recipeString.charAt(keyStringsLength);

			if (keyHashMap.containsKey(charAt)) {
				inputs[keyStringsLength] = ((ItemStack) keyHashMap.get(charAt)).copy();
			} else {
				inputs[keyStringsLength] = ItemStack.EMPTY;
			}
		}
		ICarpenterRecipe recipe;

		if (id == (byte) 1) {
			recipe = new CustomToolRecipeCarpenter(width, height, inputs, output, toolType, time, toolTier, blockTier, experience, false, sound, research, skill);
		} else {
			recipe = new ShapedCarpenterRecipes(width, height, inputs, output, toolType, time, toolTier, blockTier, experience, false, sound, research, skill);
		}
		if (ConfigCrafting.isCarpenterItemCraftable(recipe.getCarpenterRecipeOutput().getItem())) {
			this.recipes.add(recipe);
			this.recipeMap.put(name, recipe);
		}
		return recipe;
	}

	public ICarpenterRecipe addShapelessRecipe(ItemStack output, Skill skill, String research, SoundEvent sound, float experience, String tool, int hammer, int anvil, int time, Object... input) {
		ArrayList var3 = new ArrayList();
		Object[] var4 = input;
		int var5 = input.length;

		for (int var6 = 0; var6 < var5; ++var6) {
			Object var7 = var4[var6];

			if (var7 instanceof ItemStack) {
				var3.add(((ItemStack) var7).copy());
			} else if (var7 instanceof Item) {
				var3.add(new ItemStack((Item) var7));
			} else {
				if (!(var7 instanceof Block)) {
					throw new RuntimeException("MineFantasy: Invalid shapeless anvil recipe!");
				}

				var3.add(new ItemStack((Block) var7));
			}
		}

		ICarpenterRecipe recipe = new ShapelessCarpenterRecipes(output, tool, experience, hammer, anvil, time, var3, false, sound, research, skill);
		this.recipes.add(recipe);
		return recipe;
	}

	public ItemStack findMatchingRecipe(CarpenterCraftMatrix matrix, World world) {
		int var2 = 0;
		ItemStack var3 = ItemStack.EMPTY;
		ItemStack var4 = ItemStack.EMPTY;

		for (int var5 = 0; var5 < matrix.getSizeInventory(); ++var5) {
			ItemStack var6 = matrix.getStackInSlot(var5);

			if (!var6.isEmpty()) {
				if (var2 == 0) {
					var3 = var6;
				}

				if (var2 == 1) {
					var4 = var6;
				}

				++var2;
			}
		}

		if (var2 == 2 && var3.getItem() == var4.getItem() && var3.getCount() == 1 && var4.getCount() == 1
				&& var3.getItem().isRepairable()) {
			Item var10 = var3.getItem();
			int var12 = var10.getMaxDamage() - var3.getItemDamage();
			int var7 = var10.getMaxDamage() - var4.getItemDamage();
			int var8 = var12 + var7 + var10.getMaxDamage() * 10 / 100;
			int var9 = var10.getMaxDamage() - (var8) * 2;

			if (var9 < 0) {
				var9 = 0;
			}

			return new ItemStack(var3.getItem(), 1, var9);
		} else {
			Iterator var11 = this.recipes.iterator();
			ICarpenterRecipe var13;

			do {
				if (!var11.hasNext()) {
					return ItemStack.EMPTY;
				}

				var13 = (ICarpenterRecipe) var11.next();
			} while (!var13.matches(matrix));

			return var13.getCraftingResult(matrix);
		}
	}

	public ItemStack findMatchingRecipe(ICarpenter bench, CarpenterCraftMatrix matrix, World world) {
		int time;
		int anvil;
		boolean hot;
		int hammer;
		int var2 = 0;
		String toolType;
		SoundEvent sound;
		ItemStack var3 = ItemStack.EMPTY;
		ItemStack var4 = ItemStack.EMPTY;

		for (int var5 = 0; var5 < matrix.getSizeInventory(); ++var5) {
			ItemStack matrixSlot = matrix.getStackInSlot(var5);

			if (!matrixSlot.isEmpty()) {
				if (var2 == 0) {
					var3 = matrixSlot;
				}

				if (var2 == 1) {
					var4 = matrixSlot;
				}

				++var2;
			}
		}

		if (var2 == 2 && var3.getItem() == var4.getItem() && var3.getCount() == 1 && var4.getCount() == 1
				&& var3.getItem().isRepairable()) {
			Item var10 = var3.getItem();
			int var12 = var10.getMaxDamage() - var3.getItemDamage();
			int var7 = var10.getMaxDamage() - var4.getItemDamage();
			int var8 = var12 + var7 + var10.getMaxDamage() * 10 / 100;
			int var9 = var10.getMaxDamage() - var8;

			if (var9 < 0) {
				var9 = 0;
			}

			return new ItemStack(var3.getItem(), 1, var9);
		} else {
			Iterator recipeIterator = this.recipes.iterator();
			ICarpenterRecipe iCarpenterRecipe = null;

			while (recipeIterator.hasNext()) {
				ICarpenterRecipe rec = (ICarpenterRecipe) recipeIterator.next();

				if (((IRecipe) rec).matches(matrix, world)) {
					iCarpenterRecipe = rec;
					break;
				}
			}

			if (iCarpenterRecipe != null) {
				time = iCarpenterRecipe.getCraftTime();
				hammer = iCarpenterRecipe.getRecipeHammer();
				anvil = iCarpenterRecipe.getAnvil();
				hot = iCarpenterRecipe.outputHot();
				toolType = iCarpenterRecipe.getToolType();
				sound = iCarpenterRecipe.getSound();

				bench.setProgressMax(time);
				bench.setRequiredToolTier(hammer);
				bench.setRequiredCarpenter(anvil);
				bench.setHotOutput(hot);
				bench.setRequiredToolType(toolType);
				bench.setCraftingSound(sound);
				bench.setResearch(iCarpenterRecipe.getResearch());
				bench.setRequiredSkill(iCarpenterRecipe.getSkill());

				return iCarpenterRecipe.getCraftingResult(matrix);
			}
			return ItemStack.EMPTY;
		}
	}

	/**
	 * returns the List<> of all recipes
	 */
	public List getRecipeList() {
		return this.recipes;
	}
}