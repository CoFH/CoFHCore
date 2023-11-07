package cofh.lib.init.data.loot;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;

public abstract class EntityLootSubProviderCoFH extends EntityLootSubProvider {

    protected EntityLootSubProviderCoFH() {

        super(FeatureFlags.VANILLA_SET);
    }

}
