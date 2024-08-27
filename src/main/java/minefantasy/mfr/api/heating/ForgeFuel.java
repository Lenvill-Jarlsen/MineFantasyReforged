package minefantasy.mfr.api.heating;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.ForgeFuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ForgeFuel {
	private String name;

	public ItemStack item;
	public Ingredient ingredient;
	public float duration;
	public int baseHeat;
	/**
	 * Some sources only accept refined fuel
	 */
	public boolean isRefined;
	/**
	 * Applied to lava, auto-lights the forge when placed
	 */
	public boolean doesLight;

	public ForgeFuel(String name, ItemStack item, float dura, int heat, boolean light) {
		this(name, item, dura, heat, light, false);
	}

	public ForgeFuel(String name, ItemStack item, float dura, int heat, boolean light, boolean refined) {
		this.item = item;
		this.duration = dura;
		this.baseHeat = heat;
		this.doesLight = light;
		this.isRefined = refined;
	}

	public static ForgeFuel getStats(ItemStack item) {
		if (item.isEmpty()) {
			return null;
		}
		for (ForgeFuel forgeFuel : ForgeFuelRegistry.forgeFuel) {
			MineFantasyReforged.LOG.info(forgeFuel.item);
			if (item.isItemEqual(forgeFuel.item)) {
				return forgeFuel;
			}
		}
		return null;
	}
}
