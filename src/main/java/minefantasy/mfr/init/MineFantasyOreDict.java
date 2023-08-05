package minefantasy.mfr.init;

import minefantasy.mfr.item.AdvancedFuelHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class MineFantasyOreDict {

	public static void registerOreDictEntries() {
		OreDictionary.registerOre("oreCopper", MineFantasyBlocks.COPPER_ORE);
		OreDictionary.registerOre("oreTin", MineFantasyBlocks.TIN_ORE);
		OreDictionary.registerOre("oreSilver", MineFantasyBlocks.SILVER_ORE);
		OreDictionary.registerOre("oreMythic", MineFantasyBlocks.MYTHIC_ORE);
		OreDictionary.registerOre("oreKaolinite", MineFantasyBlocks.KAOLINITE_ORE);
		OreDictionary.registerOre("oreNitre", MineFantasyBlocks.NITRE_ORE);
		OreDictionary.registerOre("oreSulfur", MineFantasyBlocks.SULFUR_ORE);
		OreDictionary.registerOre("oreBorax", MineFantasyBlocks.BORAX_ORE);
		OreDictionary.registerOre("oreTungsten", MineFantasyBlocks.TUNGSTEN_ORE);
		OreDictionary.registerOre("oreClay", MineFantasyBlocks.CLAY_ORE);
		OreDictionary.registerOre("oreRichCoal", MineFantasyBlocks.COAL_RICH_ORE);

		OreDictionary.registerOre("cobblestone", new ItemStack(MineFantasyBlocks.LIMESTONE, 1, 1));
		OreDictionary.registerOre("stone", new ItemStack(MineFantasyBlocks.LIMESTONE, 1, 0));
		OreDictionary.registerOre("limestone", new ItemStack(MineFantasyBlocks.LIMESTONE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("stoneLimestone", new ItemStack(MineFantasyBlocks.LIMESTONE_COBBLE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("stoneLimestone", new ItemStack(MineFantasyBlocks.LIMESTONE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFantasyBlocks.COBBLE_BRICK, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFantasyBlocks.COBBLESTONE_ROAD, 1, OreDictionary.WILDCARD_VALUE));

		OreDictionary.registerOre("blockGlass", new ItemStack(MineFantasyBlocks.WINDOW, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("blockGlass", new ItemStack(MineFantasyBlocks.FRAMED_GLASS, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("paneGlass", new ItemStack(MineFantasyBlocks.WINDOW_PANE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("paneGlass", new ItemStack(MineFantasyBlocks.FRAMED_GLASS_PANE, 1, OreDictionary.WILDCARD_VALUE));

		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 0));
		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 1));
		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 2));
		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 3));
		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 4));
		OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, 5));
		OreDictionary.registerOre("plankWood", new ItemStack(MineFantasyBlocks.IRONBARK_PLANKS, 1, 0));
		OreDictionary.registerOre("plankWood", new ItemStack(MineFantasyBlocks.EBONY_PLANKS, 1, 0));
		OreDictionary.registerOre("plankWood", new ItemStack(MineFantasyBlocks.YEW_PLANKS, 1, 0));
		OreDictionary.registerOre("plankWood", new ItemStack(MineFantasyBlocks.REFINED_PLANKS, 1, 0));

		NonNullList<ItemStack> plankWoodList = OreDictionary.getOres("plankWood");
		plankWoodList.remove(0);

		OreDictionary.registerOre("ingotCopper", MineFantasyItems.COPPER_INGOT);
		OreDictionary.registerOre("ingotTin", MineFantasyItems.TIN_INGOT);
		OreDictionary.registerOre("ingotBronze", MineFantasyItems.BRONZE_INGOT);
		OreDictionary.registerOre("ingotPigIron", MineFantasyItems.PIG_IRON_INGOT);
		OreDictionary.registerOre("ingotSteel", MineFantasyItems.STEEL_INGOT);
		OreDictionary.registerOre("ingotDiamond", MineFantasyItems.ENCRUSTED_INGOT);
		OreDictionary.registerOre("ingotBlackSteel", MineFantasyItems.BLACK_STEEL_INGOT);
		OreDictionary.registerOre("ingotSilver", MineFantasyItems.SILVER_INGOT);
		OreDictionary.registerOre("ingotRedSteel", MineFantasyItems.RED_STEEL_INGOT);
		OreDictionary.registerOre("ingotBlueSteel", MineFantasyItems.BLUE_STEEL_INGOT);
		OreDictionary.registerOre("ingotAdamantium", MineFantasyItems.ADAMANTIUM_INGOT);
		OreDictionary.registerOre("ingotMithril", MineFantasyItems.MITHRIL_INGOT);
		OreDictionary.registerOre("ingotIgnotumite", MineFantasyItems.IGNOTUMITE_INGOT);
		OreDictionary.registerOre("ingotMithium", MineFantasyItems.MITHIUM_INGOT);
		OreDictionary.registerOre("ingotEnder", MineFantasyItems.ENDER_INGOT);
		OreDictionary.registerOre("ingotTungsten", MineFantasyItems.TUNGSTEN_INGOT);
		OreDictionary.registerOre("ingotObsidian", MineFantasyItems.OBSIDIAN_INGOT);
		OreDictionary.registerOre("ingotCompositeAlloy", MineFantasyItems.COMPOSITE_ALLOY_INGOT);

		String meatRaw = "raw_meat";
		String cookedMeat = "cooked_meat";
		OreDictionary.registerOre(cookedMeat, Items.COOKED_BEEF);
		OreDictionary.registerOre(cookedMeat, Items.COOKED_CHICKEN);
		OreDictionary.registerOre(cookedMeat, Items.COOKED_PORKCHOP);
		OreDictionary.registerOre(cookedMeat, MineFantasyItems.WOLF_COOKED);
		OreDictionary.registerOre(cookedMeat, MineFantasyItems.HORSE_COOKED);
		OreDictionary.registerOre(cookedMeat, Items.COOKED_FISH);
		OreDictionary.registerOre(cookedMeat, new ItemStack(Items.COOKED_FISH, 1, 1));
		addOreDictEntry("listAllporkcooked", cookedMeat);
		addOreDictEntry("listAllmuttoncooked", cookedMeat);
		addOreDictEntry("listAllbeefcooked", cookedMeat);
		addOreDictEntry("listAllchickencooked", cookedMeat);
		addOreDictEntry("listAllfishcooked", cookedMeat);

		OreDictionary.registerOre(meatRaw, MineFantasyItems.GUTS);
		OreDictionary.registerOre(meatRaw, Items.BEEF);
		OreDictionary.registerOre(meatRaw, Items.CHICKEN);
		OreDictionary.registerOre(meatRaw, Items.PORKCHOP);
		OreDictionary.registerOre(meatRaw, MineFantasyItems.WOLF_RAW);
		OreDictionary.registerOre(meatRaw, MineFantasyItems.HORSE_RAW);
		OreDictionary.registerOre(meatRaw, Items.FISH);
		OreDictionary.registerOre(meatRaw, new ItemStack(Items.FISH, 1, 1));
		addOreDictEntry("listAllporkraw", meatRaw);
		addOreDictEntry("listAllmuttonraw", meatRaw);
		addOreDictEntry("listAllbeefraw", meatRaw);
		addOreDictEntry("listAllchickenraw", meatRaw);
		addOreDictEntry("listAllfishraw", meatRaw);

		AdvancedFuelHandler.registerItems();
	}

	public static void registerOreDictCommonIngotEntry(){
		for (String oreString : OreDictionary.getOreNames()) {
			if (oreString.startsWith("ingot") && !oreString.contains("Brick")){
				for (ItemStack ingot : OreDictionary.getOres(oreString)){
					OreDictionary.registerOre("listAllIngots", ingot);
				}
			}
		}
	}

	private static void addOreDictEntry(String list, String mfList) {
		for (ItemStack stack : OreDictionary.getOres(list)) {
			OreDictionary.registerOre(mfList, stack);
		}
	}
}

