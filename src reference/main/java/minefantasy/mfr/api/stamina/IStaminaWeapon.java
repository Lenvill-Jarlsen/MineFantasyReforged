package minefantasy.mfr.api.stamina;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IStaminaWeapon {
    public float getStaminaDrainOnHit(EntityLivingBase user, ItemStack item);
}
