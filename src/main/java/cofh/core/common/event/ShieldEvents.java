package cofh.core.common.event;

import cofh.core.common.capability.CoreCapabilities;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.ShieldBlockEvent;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.neoforged.bus.api.EventPriority.HIGH;

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
        var shield = entity.getUseItem().getCapability(CoreCapabilities.ShieldHandler.ITEM);
        if (shield != null) {
            event.setBlockedDamage(shield.onBlock(entity, event.getDamageSource(), event.getBlockedDamage()));
        }
    }

}