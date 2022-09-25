package cofh.core.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.core.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ShieldEvents {

    private ShieldEvents() {

    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();

        if (!canBlockDamageSource(entity, source)) {
            return;
        }
        if (source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns()) {
            return;
        }
        ItemStack shield = entity.getUseItem();
        shield.getCapability(SHIELD_ITEM_CAPABILITY).ifPresent(cap -> cap.onBlock(entity, source, event.getAmount()));
    }

    // region HELPERS
    public static boolean canBlockDamageSource(LivingEntity living, DamageSource source) {

        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.isBypassArmor() && living.isBlocking()) {
            return canBlockDamagePosition(living, source.getSourcePosition());
        }
        return false;
    }

    public static boolean canBlockDamagePosition(LivingEntity living, Vec3 sourcePos) {

        if (sourcePos != null) {
            return new Vec3(living.getX() - sourcePos.x(), 0.0D, living.getZ() - sourcePos.z()).dot(living.getViewVector(1.0F)) < 0.0D;
        }
        return false;
    }
    // endregion
}
