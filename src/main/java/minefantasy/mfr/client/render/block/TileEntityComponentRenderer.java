package minefantasy.mfr.client.render.block;

import minefantasy.mfr.block.BlockComponent;
import minefantasy.mfr.client.model.block.ModelBarStack;
import minefantasy.mfr.client.model.block.ModelBigPlateStack;
import minefantasy.mfr.client.model.block.ModelJugStack;
import minefantasy.mfr.client.model.block.ModelPlankStack;
import minefantasy.mfr.client.model.block.ModelPotStack;
import minefantasy.mfr.client.model.block.ModelSheetStack;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.tile.TileEntityComponent;
import minefantasy.mfr.util.TextureHelperMFR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityComponentRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	private final ModelBarStack bars;
	private final ModelSheetStack sheets;
	private final ModelPlankStack planks;
	private final ModelPotStack pots;
	private final ModelBigPlateStack bigplates;
	private final ModelJugStack jugs;

	public TileEntityComponentRenderer() {
		bars = new ModelBarStack();
		sheets = new ModelSheetStack();
		planks = new ModelPlankStack();
		pots = new ModelPotStack();
		bigplates = new ModelBigPlateStack();
		jugs = new ModelJugStack();
	}

	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		renderModelAt((TileEntityComponent) te, x, y, z); // where to render

	}

	public void renderModelAt(TileEntityComponent tile, double d, double d1, double d2) {
		if (!tile.getItem().isEmpty()) {
			EnumFacing facing = EnumFacing.NORTH;
			if (tile.hasWorld()) {
				IBlockState state = tile.getWorld().getBlockState(tile.getPos());
				facing = state.getValue(BlockComponent.FACING);
			}

			GlStateManager.pushMatrix(); // start
			float scale = 1.0F;
			float yOffset = 1.0F;
			GlStateManager.translate((float) d + 0.5F, (float) d1 + yOffset, (float) d2 + 0.5F); // size
			GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F); // rotate based on metadata
			GlStateManager.scale(scale, -scale, -scale); // if you read this comment out this line and you can see what happens
			GlStateManager.pushMatrix();

			CustomMaterial material = tile.material;
			if (material != CustomMaterial.NONE) {
				GlStateManager.color(material.colourRGB[0] / 255F, material.colourRGB[1] / 255F, material.colourRGB[2] / 255F);
			}

			bindTextureByName("textures/models/object/component/placed_" + tile.tex + ".png"); // texture

			render(tile.type, tile.stackSize, 0.0625F);

			GlStateManager.color(1F, 1F, 1F);

			GlStateManager.popMatrix();
			GlStateManager.color(1F, 1F, 1F);
			GlStateManager.popMatrix(); // end
		}

	}

	private void render(String type, int stackSize, float f) {
		if (type.equalsIgnoreCase("bar")) {
			bars.render(stackSize, f);
		}
		if (type.equalsIgnoreCase("sheet")) {
			sheets.render(stackSize, f);
		}
		if (type.equalsIgnoreCase("plank")) {
			planks.render(stackSize, f);
		}
		if (type.equalsIgnoreCase("pot")) {
			pots.render(stackSize, f);
		}
		if (type.equalsIgnoreCase("bigplate")) {
			bigplates.render(stackSize, f);
		}
		if (type.equalsIgnoreCase("jug")) {
			jugs.render(stackSize, f);
		}
	}

	private void bindTextureByName(String image) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureHelperMFR.getResource(image));
	}

}
