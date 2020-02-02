package minefantasy.mfr.mechanics.worldGen.structure.dwarven;

import minefantasy.mfr.init.BlockListMFR;
import minefantasy.mfr.mechanics.worldGen.structure.StructureGenAncientForge;
import minefantasy.mfr.mechanics.worldGen.structure.StructureModuleMFR;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class StructureGenDSStairs extends StructureModuleMFR {
    /**
     * The minimal height for a stairway to be created
     */
    public static final int minLevel = 20;
    protected ResourceLocation lootType = LootTableList.CHESTS_SIMPLE_DUNGEON;

    public StructureGenDSStairs(World world, StructureCoordinates position) {
        super(world, position);
    }

    public StructureGenDSStairs(World world, BlockPos pos, int direction) {
        super(world, pos, direction);
    }

    @Override
    public boolean canGenerate() {
        if (lengthId == -100) {
            return true;
        }

        int width_span = getWidthSpan();
        int depth = getDepthSpan();
        int height = getHeight();
        int empty_spaces = 0;
        int filledSpaces = 0, emptySpaces = 0;
        for (int x = -width_span; x <= width_span; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = 1; z <= depth; z++) {
                    Block block = this.getBlock(x, y - z, z);
                    if (!allowBuildOverBlock(block) || this.isUnbreakable(x, y - z, z, direction)) {
                        return false;
                    }
                    if (!block.getMaterial(block.getDefaultState()).isSolid()) {
                        ++emptySpaces;
                    } else {
                        ++filledSpaces;
                    }
                }
            }
        }
        if (WorldGenDwarvenStronghold.debug_air) {
            return true;
        }
        return ((float) emptySpaces / (float) (emptySpaces + filledSpaces)) < WorldGenDwarvenStronghold.maxAir;// at
        // least
        // 75%
        // full
    }

    private boolean allowBuildOverBlock(Block block) {
        if (block == BlockListMFR.REINFORCED_STONE_BRICKS || block == BlockListMFR.REINFORCED_STONE) {
            return false;
        }
        return true;
    }

    @Override
    public void generate() {
        int width_span = getWidthSpan();
        int depth = getDepthSpan();
        int height = getHeight();
        for (int x = -width_span; x <= width_span; x++) {
            for (int z = 0; z <= depth; z++) {
                Object[] blockarray;

                // FLOOR
                blockarray = getFloor(width_span, depth, x, z);
                if (blockarray != null) {
                    int meta = (Integer) blockarray[1];
                    placeBlock((Block) blockarray[0], meta, x, -z - 1, z);

                    placeBlock(BlockListMFR.REINFORCED_STONE_BRICKS, StructureGenAncientForge.getRandomMetadata(rand), x,
                            -z - 2, z);

                    if (x == (width_span - 1) || x == -(width_span - 1)) {
                        placeBlock(BlockListMFR.REINFORCED_STONE, 0, x, -z, z);
                    } else {
                        placeBlock(Blocks.AIR, 0, x, -z, z);
                    }
                }
                // WALLS
                for (int y = 0; y <= height + 1; y++) {
                    blockarray = getWalls(width_span, height, depth, x, y, z);
                    if (blockarray != null) {
                        int meta = 0;
                        if (blockarray[1] instanceof Integer) {
                            meta = (Integer) blockarray[1];
                        }
                        if (blockarray[1] instanceof Boolean) {
                            meta = (Boolean) blockarray[1] ? StructureGenAncientForge.getRandomMetadata(rand) : 0;
                        }
                        if (blockarray[1] instanceof String) {
                            meta = (String) blockarray[1] == "Hall" ? StructureGenDSHall.getRandomEngravedWall(rand)
                                    : 0;
                        }
                        placeBlock((Block) blockarray[0], meta, x, y - z, z);
                    }
                }
                // CEILING
                blockarray = getCeiling(width_span, depth, x, z);
                if (blockarray != null) {
                    int meta = (Boolean) blockarray[1] ? StructureGenAncientForge.getRandomMetadata(rand) : 0;
                    placeBlock((Block) blockarray[0], meta, x, height + 1 - z, z);
                }

                // TRIM
                blockarray = getTrim(width_span, depth, x, z);
                if (blockarray != null) {
                    int meta = (Boolean) blockarray[1] ? StructureGenAncientForge.getRandomMetadata(rand) : 0;
                    placeBlock((Block) blockarray[0], meta, x, height - z, z);
                    if ((Block) blockarray[0] == BlockListMFR.REINFORCED_STONE_FRAMED) {
                        placeBlock(BlockListMFR.REINFORCED_STONE, 0, x, height - z, z);
                        placeBlock(BlockListMFR.REINFORCED_STONE_FRAMED, 0, x, height - z - 1, z);

                        for (int h = height - 1; h > 1; h--) {
                            placeBlock(BlockListMFR.REINFORCED_STONE, h == 2 ? 1 : 0, x, h - z - 1, z);
                        }
                        placeBlock(BlockListMFR.REINFORCED_STONE_FRAMED, 0, x, -z, z);
                    }
                }

            }
        }

        // DOORWAY
        buildDoorway(width_span, depth, height);

        if (lengthId == -100) {
            this.lengthId = -99;
            if ((pos.getY() > 64 && rand.nextInt(3) != 0) || (pos.getY() >= 56 && rand.nextInt(3) == 0)) {
                mapStructure(0, -depth, depth, StructureGenDSStairs.class);
            } else {
                mapStructure(0, -depth, depth, StructureGenDSCrossroads.class);
            }
        }
        ++lengthId;// Stairs don't count toward length
        if (lengthId > 0) {
            buildNext(width_span, depth, height);
        }
    }

    protected void buildDoorway(int width_span, int depth, int height) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 0; z >= -1; z--) {
                    placeBlock(Blocks.AIR, 0, x, y, z);
                }
            }
            placeBlock(BlockListMFR.COBBLE_PAVEMENT_STAIR, getStairDirection(reverse()), x, 0, -1);
        }
    }

    protected void buildNext(int width_span, int depth, int height) {
        if (lengthId > 1 && rand.nextInt(3) == 0 && pos.getY() >= minLevel) {
            mapStructure(0, -depth, depth, StructureGenDSStairs.class);
        } else {
            tryPlaceHall(0, -depth, depth, direction);
        }
    }

    protected void tryPlaceHall(int x, int y, int z, int d) {
        Class extension = getRandomExtension();
        if (extension != null) {
            mapStructure(x, y, z, d, extension);
        }
    }

    protected int getHeight() {
        return 4;
    }

    protected int getDepthSpan() {
        return 9;
    }

    protected int getWidthSpan() {
        return 3;
    }

    protected Class<? extends StructureModuleMFR> getRandomExtension() {
        if (rand.nextInt(20) == 0 && this.pos.getY() > 24) {
            return StructureGenDSStairs.class;
        }
        if (lengthId == 1) {
            return StructureGenDSRoom.class;
        }
        if (deviationCount > 0 && rand.nextInt(4) == 0) {
            return StructureGenDSIntersection.class;
        }
        return StructureGenDSHall.class;
    }

    protected Object[] getTrim(int radius, int depth, int x, int z) {
        if (x == -radius || x == radius) {
            return null;
        }

        if (x == -(radius - 1) || x == (radius - 1)) {
            if (z == Math.floor((float) depth / 2)) {
                return new Object[]{BlockListMFR.REINFORCED_STONE_FRAMED, false};
            }
            return new Object[]{BlockListMFR.REINFORCED_STONE, false};
        }
        return null;
    }

    protected Object[] getCeiling(int radius, int depth, int x, int z) {
        return x == 0 ? new Object[]{BlockListMFR.REINFORCED_STONE, false}
                : new Object[]{BlockListMFR.REINFORCED_STONE_BRICKS, true};
    }

    protected Object[] getFloor(int radius, int depth, int x, int z) {
        if (x >= -1 && x <= 1) {
            if (z >= depth - 1) {
                return new Object[]{BlockListMFR.COBBLE_PAVEMENT, 0};
            }
            return new Object[]{BlockListMFR.COBBLE_PAVEMENT_STAIR, Integer.valueOf(getStairDirection(reverse()))};
        }
        if (x == -radius || x == radius || z == depth || z == 0) {
            return new Object[]{BlockListMFR.REINFORCED_STONE, Integer.valueOf(0)};
        }
        if (x == -(radius - 1) || x == (radius - 1) || z == (depth - 1) || z == 1) {
            return new Object[]{BlockListMFR.REINFORCED_STONE, Integer.valueOf(0)};
        }
        return new Object[]{BlockListMFR.COBBLE_PAVEMENT, 0};
    }

    protected Object[] getWalls(int radius, int height, int depth, int x, int y, int z) {
        if (x != -radius && x != radius && z == 0) {
            return new Object[]{Blocks.AIR, false};
        }
        if (x == -radius || x == radius || z == depth) {
            return y == height / 2 ? new Object[]{BlockListMFR.REINFORCED_STONE, "Hall"}
                    : new Object[]{BlockListMFR.REINFORCED_STONE_BRICKS, true};
        }
        return new Object[]{Blocks.AIR, false};
    }

    public StructureModuleMFR setLoot(ResourceLocation loot) {
        this.lootType = loot;
        return this;
    }
}
