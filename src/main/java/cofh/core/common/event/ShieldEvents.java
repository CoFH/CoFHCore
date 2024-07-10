package cofh.core.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.ShieldBlockEvent;

import static cofh.core.common.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
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