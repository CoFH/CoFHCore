package cofh.core.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.core.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraftforge.eventbus.api.EventPriority.HIGH;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ShieldEvents {

    private ShieldEvents() {

    }

    @SubscribeEvent (priority = HIGH)
    public static void handleShieldBlock(ShieldBlockEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        entity.getUseItem().getCapability(SHIELD_ITEM_CAPABILITY).ifPresent(shield -> {
            event.setBlockedDamage(shield.onBlock(entity, event.getDamageSource(), event.getBlockedDamage()));
        });
    }

}