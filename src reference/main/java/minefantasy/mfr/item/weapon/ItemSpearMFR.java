package minefantasy.mfr.item.weapon;

import minefantasy.mfr.api.helpers.TacticalManager;
import minefantasy.mfr.api.weapon.WeaponClass;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.init.SoundsMFR;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemSpearMFR extends ItemWeaponMFR implements IExtendedReachWeapon {
    /**
     * The spear is for the defensive player, it has a long reach, knockback and can
     * be thrown. Spears are good for keeping enemies at a distance, Parrying is
     * easier when sneaking
     * <p>
     * These are for the defensive player
     */
    public ItemSpearMFR(String name, Item.ToolMaterial material, int rarity, float weight) {
        super(material, name, rarity, weight);
    }

    @Override
    public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
        return offhand == null || offhand.getItem() == Items.SHIELD;
    }

    @Override
    public boolean isHeavyWeapon() {
        return true;
    }

    @Override
    public boolean isOffhandHandDual(ItemStack off) {
        return false;
    }

    @Override
    public boolean sheatheOnBack(ItemStack item) {
        return true;
    }

    @Override
    public float getReachModifierInBlocks(ItemStack stack) {
        return 3.0F;
    }

    @Override
    public void addInformation(ItemStack item, World world, List list, ITooltipFlag flag) {
        super.addInformation(item, world, list, flag);

        if (material != Item.ToolMaterial.WOOD) {
            list.add(TextFormatting.DARK_GREEN + I18n.translateToLocalFormatted(
                    "attribute.modifier.plus." + 0, decimal_format.format(getMountedDamage()),
                    I18n.translateToLocal("attribute.weapon.mountedBonus")));
        }
    }

    @Override
    public float modifyDamage(ItemStack item, EntityLivingBase wielder, Entity hit, float initialDam,
                              boolean properHit) {
        float damage = super.modifyDamage(item, wielder, hit, initialDam, properHit);

        if (!(hit instanceof EntityLivingBase) || this instanceof ItemLance) {
            return damage;
        }
        EntityLivingBase target = (EntityLivingBase) hit;

        if (wielder.isRiding() && tryPerformAbility(wielder, charge_cost)) {
            ItemWaraxeMFR.brutalise(wielder, target, 1.0F);
            return damage + getMountedDamage();
        }
        if (!wielder.isRiding() && wielder.isSprinting()) {
            if (this instanceof ItemHalbeardMFR) {
                return Math.max(damage / 1.25F, 1.0F);
            } else {
                return damage * 1.25F;
            }
        }
        return damage;
    }

    @Override
    public boolean playCustomParrySound(EntityLivingBase blocker, Entity attacker, ItemStack weapon) {
        blocker.world.playSound(blocker.posX, blocker.posY, blocker.posZ, SoundsMFR.WOOD_PARRY, SoundCategory.NEUTRAL, 1.0F, 0.7F, true);
        return true;
    }

    @Override
    public void onParry(DamageSource source, EntityLivingBase user, Entity attacker, float dam) {
        super.onParry(source, user, attacker, dam);
        if (ConfigWeapon.useBalance && user instanceof EntityPlayer) {
            TacticalManager.throwPlayerOffBalance((EntityPlayer) user, getBalance(), rand.nextBoolean());
        }
    }

    private float getMountedDamage() {
        if (material == Item.ToolMaterial.WOOD) {
            return 0;
        }
        return 4F;
    }

    @Override
    protected int getParryDamage(float dam) {
        return (int) dam;
    }

    /**
     * Gets the angle the weapon can parry
     */
    @Override
    public float getParryAngleModifier(EntityLivingBase user) {
        return user.isSneaking() ? 1.2F : 0.85F;
    }

    @Override
    public float getBalance() {
        return 0.5F;
    }

    @Override
    protected float getKnockbackStrength() {
        return 2.5F;
    }

    @Override
    protected boolean canAnyMobParry() {
        return false;
    }

    @Override
    public int modifyHitTime(EntityLivingBase user, ItemStack item) {
        return super.modifyHitTime(user, item) + speedModSpear;
    }

    /**
     * gets the time after being hit your guard will be let down
     */
    @Override
    public int getParryCooldown(DamageSource source, float dam, ItemStack weapon) {
        return spearParryTime;
    }

    @Override
    protected float getStaminaMod() {
        return spearStaminaCost;
    }

    @Override
    public WeaponClass getWeaponClass() {
        return WeaponClass.POLEARM;
    }

    @Override
    protected float[] getWeaponRatio(ItemStack implement) {
        return piercingDamage;
    }

    @Override
    public boolean canCounter() {
        return false;
    }

    @Override
    public float getScale(ItemStack itemstack) {
        return 3.0F;
    }
}
