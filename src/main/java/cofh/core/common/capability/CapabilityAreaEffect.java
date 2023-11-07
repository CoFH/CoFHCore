package cofh.core.common.capability;

import cofh.lib.api.capability.IAreaEffectItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityAreaEffect {

    public static final Capability<IAreaEffectItem> AREA_EFFECT_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        event.register(IAreaEffectItem.class);
    }

}
