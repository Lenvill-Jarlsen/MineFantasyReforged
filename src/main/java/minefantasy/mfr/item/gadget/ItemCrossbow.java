package minefantasy.mfr.item.gadget;

import minefantasy.mfr.MineFantasyReborn;
import minefantasy.mfr.api.archery.AmmoMechanicsMFR;
import minefantasy.mfr.api.archery.IDisplayMFRAmmo;
import minefantasy.mfr.api.archery.IFirearm;
import minefantasy.mfr.api.archery.ISpecialBow;
import minefantasy.mfr.api.crafting.ISpecialSalvage;
import minefantasy.mfr.api.crafting.engineer.ICrossbowPart;
import minefantasy.mfr.api.helpers.PowerArmour;
import minefantasy.mfr.api.weapon.IDamageModifier;
import minefantasy.mfr.api.weapon.IDamageType;
import minefantasy.mfr.api.weapon.IRackItem;
import minefantasy.mfr.client.render.item.RenderCrossbow;
import minefantasy.mfr.entity.EntityArrowMFR;
import minefantasy.mfr.init.ComponentListMFR;
import minefantasy.mfr.init.CreativeTabMFR;
import minefantasy.mfr.init.CustomToolListMFR;
import minefantasy.mfr.init.MineFantasySounds;
import minefantasy.mfr.item.ItemBaseMFR;
import minefantasy.mfr.item.archery.ItemArrowMFR;
import minefantasy.mfr.mechanics.CombatMechanics;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.tile.TileEntityRack;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;

public class ItemCrossbow extends ItemBaseMFR
        implements IFirearm, IDisplayMFRAmmo, IDamageModifier, IRackItem, IDamageType, IScope, ISpecialSalvage {
    private static final String partNBT = "MineFantasy_GunPiece_";
    public static String useTypeNBT = "MF_ActionInUse";
    private String[] fullParts = new String[]{"mod", "muzzle", "mechanism", "stock"};

    public ItemCrossbow() {
        super("crossbow_custom");

        this.setCreativeTab(CreativeTabMFR.tabGadget);
        this.setFull3D();
        this.setMaxDamage(150);
        this.setMaxStackSize(1);
    }

    public static void setUseAction(ItemStack item, String action) {
        AmmoMechanicsMFR.getNBT(item).setString(useTypeNBT, action);
    }

    public static String getUseAction(ItemStack item) {
        String action = AmmoMechanicsMFR.getNBT(item).getString(useTypeNBT);

        return action != null ? action : "null";
    }

    public static void setPart(String part, ItemStack item, int id) {
        NBTTagCompound nbt = getNBT(item);
        nbt.setInteger(partNBT + part, id);
    }

    public static int getPart(String part, ItemStack item) {
        NBTTagCompound nbt = getNBT(item);
        if (nbt.hasKey(partNBT + part)) {
            return nbt.getInteger(partNBT + part);
        }
        return -1;
    }

    public static NBTTagCompound getNBT(ItemStack item) {
        if (!item.hasTagCompound())
            item.setTagCompound(new NBTTagCompound());
        return item.getTagCompound();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack item) {
        String action = getUseAction(item);
        if (action.equalsIgnoreCase("reload")) {
            return EnumAction.BLOCK;
        }
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);
        if (!world.isRemote && player.isSneaking() || AmmoMechanicsMFR.isDepleted(item))// OPEN INV
        {
            player.openGui(MineFantasyReborn.MOD_ID, NetworkHandler.GUI_RELOAD, player.world, 1, 0, 0);
            return ActionResult.newResult(EnumActionResult.FAIL, item);
        }
        ItemStack loaded = AmmoMechanicsMFR.getArrowOnBow(item);
        if (loaded.isEmpty() || player.isSwingInProgress)// RELOAD
        {
            startUse(player, item, "reload");
            return ActionResult.newResult(EnumActionResult.FAIL, item);
        }
        else{
            startUse(player, item, "fire");// FIRE
            return ActionResult.newResult(EnumActionResult.SUCCESS, item);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase user) {
        boolean infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, item) > 0;
        user.swingArm(EnumHand.MAIN_HAND);
        ItemStack loaded = AmmoMechanicsMFR.getArrowOnBow(item);
        ItemStack storage = AmmoMechanicsMFR.getAmmo(item);
        String action = getUseAction(item);
        boolean shouldConsume = true;

        if (action.equalsIgnoreCase("reload")) {
            if (storage.isEmpty() && infinity) {
                shouldConsume = false;
                storage = CustomToolListMFR.STANDARD_BOLT.construct("Magic");
            }
            if (!storage.isEmpty())// RELOAD
            {
                boolean success = false;
                if (loaded.isEmpty()) {
                    ItemStack ammo = storage.copy();
                    ammo.setCount(1);
                    if (shouldConsume)
                        AmmoMechanicsMFR.consumeAmmo((EntityPlayer) user, item);
                    AmmoMechanicsMFR.putAmmoOnFirearm(item, ammo);
                    success = true;
                } else if (loaded.isItemEqual(storage) && loaded.getCount() < getAmmoCapacity(item)) {
                    if (shouldConsume)
                        AmmoMechanicsMFR.consumeAmmo((EntityPlayer) user, item);
                    loaded.grow(1);
                    AmmoMechanicsMFR.putAmmoOnFirearm(item, loaded);
                    success = true;
                }
                if (success) {
                    user.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                }
            }
        }
        return super.onItemUseFinish(item, world, user);
    }

    @Override
    public void onUsingTick(ItemStack item, EntityLivingBase player, int time) {
        ItemStack loaded = AmmoMechanicsMFR.getArrowOnBow(item);
        int max = getMaxItemUseDuration(item);

        if (time == (max - 5) && getUseAction(item).equalsIgnoreCase("reload")
                && (loaded.isEmpty() || loaded.getCount() < getAmmoCapacity(item))) {
            player.playSound(MineFantasySounds.CROSSBOW_LOAD, 1.0F, 1 / (getFullValue(item, "speed") / 4F));
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack item, World world, EntityLivingBase user, int timeLeft) {
        ItemStack loaded = AmmoMechanicsMFR.getArrowOnBow(item);
        String action = getUseAction(item);

        if (action.equalsIgnoreCase("fire") && this.onFireArrow(user.world, AmmoMechanicsMFR.getArrowOnBow(item),
                item, (EntityPlayer) user, this.getFullValue(item, "power"), false)) {
            if (!loaded.isEmpty()) {
                loaded.grow(1);
                AmmoMechanicsMFR.putAmmoOnFirearm(item, (loaded.getCount() > 0 ? loaded : ItemStack.EMPTY));
            }
            recoilUser((EntityPlayer) user, getFullValue(item, "recoil"));
            AmmoMechanicsMFR.damageContainer(item, (EntityPlayer) user, 1);
        }
        stopUse(item);
    }

    public void startUse(EntityPlayer user, ItemStack item, String action) {
        setUseAction(item, action);
        if (user != null)
            user.setActiveHand(EnumHand.MAIN_HAND);
    }

    public void stopUse(ItemStack item) {
        startUse(null, item, "null");
    }

    private void recoilUser(EntityPlayer user, float value) {
        if (PowerArmour.isPowered(user)) {
            return;
        }
        float str = CombatMechanics.getStrengthEnhancement(user) + 1;
        value /= str;

        float angle = value;
        user.rotationPitch -= itemRand.nextFloat() * angle;
        user.rotationYawHead += itemRand.nextFloat() * angle - 0.5F;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack item) {
        String action = getUseAction(item);
        if (action.equalsIgnoreCase("reload")) {
            return (int) (getFullValue(item, "speed") * 20F);
        }
        return 72000;
    }

    @Override
    public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag fullInfo) {
        super.addInformation(item, world, list, fullInfo);

        list.add(I18n.format("attribute.crossbow.power.name", getFullValue(item, "power")));
        list.add(I18n.format("attribute.crossbow.speed.name", getFullValue(item, "speed")));
        list.add(I18n.format("attribute.crossbow.recoil.name", getFullValue(item, "recoil")));
        list.add(I18n.format("attribute.crossbow.spread.name", getFullValue(item, "spread")));
        list.add(I18n.format("attribute.crossbow.capacity.name", getAmmoCapacity(item)));
        list.add(I18n.format("attribute.crossbow.bash.name", getMeleeDmg(item)));
    }

    public String getItemStackDisplayName(ItemStack item) {
        String base = getNameModifier(item, "stock");
        String arms = getNameModifier(item, "mechanism");
        String mod = getNameModifier(item, "mod");

        String fullName = "Crossbow";

        if (base != null)
            fullName = base;
        if (arms != null)
            fullName = arms + " " + fullName;
        if (mod != null)
            fullName = mod + " " + fullName;

        return fullName;
    }

    /**
     * Constructs a crossbow with a list of parts
     */
    public ItemStack constructCrossbow(ICrossbowPart... crossbowParts) {
        ItemStack crossbow = new ItemStack(this);

        for (ICrossbowPart part : crossbowParts) {
            if (part != null) {
                setPart(crossbow, part);
            }
        }
        return crossbow;
    }

    /**
     * Adds a part to a crossbow
     */
    public ItemStack setPart(ItemStack item, ICrossbowPart part) {
        setPart(part.getComponentType(), item, part.getID());
        return item;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(constructCrossbow((ICrossbowPart) ComponentListMFR.CROSSBOW_HANDLE_WOOD, (ICrossbowPart) ComponentListMFR.CROSSBOW_ARMS_BASIC));
        items.add(constructCrossbow((ICrossbowPart) ComponentListMFR.CROSSBOW_STOCK_WOOD, (ICrossbowPart) ComponentListMFR.CROSSBOW_ARMS_LIGHT));

        items.add(constructCrossbow((ICrossbowPart) ComponentListMFR.CROSSBOW_STOCK_WOOD, (ICrossbowPart) ComponentListMFR.CROSSBOW_ARMS_BASIC, (ICrossbowPart) ComponentListMFR.CROSSBOW_AMMO));
        items.add(constructCrossbow((ICrossbowPart) ComponentListMFR.CROSSBOW_STOCK_WOOD, (ICrossbowPart) ComponentListMFR.CROSSBOW_ARMS_HEAVY, (ICrossbowPart) ComponentListMFR.CROSSBOW_BAYONET));
        items.add(constructCrossbow((ICrossbowPart) ComponentListMFR.CROSSBOW_STOCK_IRON, (ICrossbowPart) ComponentListMFR.CROSSBOW_ARMS_ADVANCED, (ICrossbowPart) ComponentListMFR.CROSSBOW_SCOPE));
    }

    public String getNameModifier(ItemStack item, String partname) {
        ICrossbowPart part = ItemCrossbowPart.getPart(partname, getPart(partname, item));
        if (part != null) {
            String name = part.getUnlocalisedName();
            if (name != null) {
                return I18n.format(name);
            }
        }
        return null;
    }

    /**
     * Get the modifier for a part (such as power, speed, recoil, capacity and
     * spread)
     */
    public float getModifierForPart(ItemStack item, String partName, String variable) {
        ICrossbowPart part = ItemCrossbowPart.getPart(partName, getPart(partName, item));
        if (part != null) {
            return part.getModifier(variable);
        }
        return 0F;
    }

    /**
     * Checks all "fullParts" for value modifiers
     */
    public float getFullValue(ItemStack item, String variable) {
        float min = variable.equalsIgnoreCase("speed") ? 0.5F : 0F;
        float value = 0F;

        for (String part : fullParts) {
            value += getModifierForPart(item, part, variable);
        }

        return Math.max(min, value);
    }

    @Override
    public int getAmmoCapacity(ItemStack item) {
        return 1 + (int) getModifierForPart(item, "mod", "capacity");// only mod affects capacity
    }

    public float getMeleeDmg(ItemStack item) {
        return 1 + getModifierForPart(item, "muzzle", "bash");// only muzzle affects capacity
    }

    @Override
    public boolean canAcceptAmmo(ItemStack weapon, String ammo) {
        return ammo.equalsIgnoreCase("bolt");
    }

    @Override
    public float[] getDamageRatio(Object... implement) {
        if (implement.length > 0 && implement[0] instanceof ItemStack) {
            if (this.getMeleeDmg((ItemStack) implement[0]) > 1.0F)// Bayonet is used
            {
                return new float[]{0F, 0F, 1F};
            }
        }

        return new float[]{0F, 1F, 0F};
    }

    @Override
    public float getPenetrationLevel(Object implement) {
        return 0;
    }

    @Override
    public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam,
                              boolean properHit) {
        return initialDam + this.getMeleeDmg(item) - 1;
    }

    public boolean onFireArrow(World world, ItemStack arrow, ItemStack bow, EntityPlayer user, float charge, boolean infinite) {
        if (arrow.isEmpty() || !(arrow.getItem() instanceof ItemArrowMFR)) {
            return false;
        }
        ItemArrowMFR ammo = (ItemArrowMFR) arrow.getItem();
        if (!(ammo.getAmmoType(arrow).equalsIgnoreCase("bolt"))) {
            return false;
        }
        // TODO Arrow entity instance
        EntityArrowMFR entArrow = ammo.getFiredArrow(new EntityArrowMFR(world, user, getFullValue(bow, "spread"), charge * 2.0F), arrow);

        int power_level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
        entArrow.setPower(1 + (0.25F * power_level));

        int punch_level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);

        if (punch_level > 0) {
            entArrow.setKnockbackStrength(punch_level);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0) {
            entArrow.setFire(100);
        }

        if (infinite) {
            entArrow.canBePickedUp = 2;
        }

        if (!bow.isEmpty()) {
            if (bow.getItem() instanceof ISpecialBow) {
                entArrow = (EntityArrowMFR) ((ISpecialBow) bow.getItem()).modifyArrow(bow, entArrow);
            }
        }
        if (!world.isRemote) {
            world.spawnEntity(entArrow);
        }
        world.playSound(user, user.getPosition(), MineFantasySounds.CROSSBOW_FIRE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

        return true;
    }

    @Override
    public float getZoom(ItemStack item) {
        return getUseAction(item).equalsIgnoreCase("fire") ? getModifierForPart(item, "mod", "zoom") : 0F;// only mod
        // affects
        // zoom;
    }

    @Override
    public Object[] getSalvage(ItemStack item) {
        return new Object[]{getItem(item, "stock"), getItem(item, "mechanism"), getItem(item, "muzzle"),
                getItem(item, "mod")};
    }

    public Object getItem(ItemStack item, String type) {
        return ItemCrossbowPart.getPart(type, getPart(type, item));
    }

    @Override
    public int getMaxDamage(ItemStack item) {
        return super.getMaxDamage(item) + (int) getFullValue(item, "durability");
    }

    @Override
    public float getScale(ItemStack itemstack) {
        return 1.0F;
    }

    @Override
    public float getOffsetX(ItemStack itemstack) {
        return 0F;
    }

    @Override
    public float getOffsetY(ItemStack itemstack) {
        return (isHandCrossbow(itemstack) ? 0F : 0.5F) + 1 / 8F;
    }

    @Override
    public float getOffsetZ(ItemStack itemstack) {
        return 0.25F;
    }

    @Override
    public float getRotationOffset(ItemStack itemstack) {
        return 0;
    }

    @Override
    public boolean canHang(TileEntityRack rack, ItemStack item, int slot) {
        if (slot == 0 || slot == 3)
            return false;

        return isHandCrossbow(item) || rack.hasRackBelow(slot);
    }

    @Override
    public boolean isSpecialRender(ItemStack item) {
        return true;
    }

    public boolean isHandCrossbow(ItemStack item) {
        ICrossbowPart part = ItemCrossbowPart.getPart("stock", getPart("stock", item));
        if (part != null) {
            return part.makesSmallWeapon();
        }
        return true;
    }

    @Override
    public void registerClient() {
        ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
        ModelLoaderHelper.registerWrappedItemModel(this, new RenderCrossbow(() -> modelLocation), modelLocation);
    }
}