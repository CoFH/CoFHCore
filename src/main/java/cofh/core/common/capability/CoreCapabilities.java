package cofh.core.common.capability;

import cofh.lib.api.capability.IArcheryAmmoItem;
import cofh.lib.api.capability.IArcheryBowItem;
import cofh.lib.api.capability.IAreaEffectHandler;
import cofh.lib.api.capability.IShieldItem;
import cofh.lib.common.energy.IRedstoneFluxStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public final class CoreCapabilities {

    public static final class ArcheryHandler {

        public static final ItemCapability<IArcheryBowItem, Void> BOW = ItemCapability.createVoid(create("archery_bow"), IArcheryBowItem.class);
        public static final ItemCapability<IArcheryAmmoItem, Void> AMMO = ItemCapability.createVoid(create("archery_ammo"), IArcheryAmmoItem.class);

        private ArcheryHandler() {

        }

    }

    public static final class AreaEffectHandler {

        public static final BlockCapability<IAreaEffectHandler, @Nullable Direction> BLOCK = BlockCapability.createSided(create("area"), IAreaEffectHandler.class);
        public static final EntityCapability<IAreaEffectHandler, @Nullable Direction> ENTITY = EntityCapability.createSided(create("area"), IAreaEffectHandler.class);
        public static final ItemCapability<IAreaEffectHandler, Void> ITEM = ItemCapability.createVoid(create("area"), IAreaEffectHandler.class);

        private AreaEffectHandler() {

        }

    }

    public static final class RedstoneFluxStorage {

        public static final BlockCapability<IRedstoneFluxStorage, @Nullable Direction> BLOCK = BlockCapability.createSided(create("energy"), IRedstoneFluxStorage.class);
        public static final EntityCapability<IRedstoneFluxStorage, @Nullable Direction> ENTITY = EntityCapability.createSided(create("energy"), IRedstoneFluxStorage.class);
        public static final ItemCapability<IRedstoneFluxStorage, Void> ITEM = ItemCapability.createVoid(create("energy"), IRedstoneFluxStorage.class);

        private RedstoneFluxStorage() {

        }

    }

    public static final class ShieldHandler {

        public static final ItemCapability<IShieldItem, Void> ITEM = ItemCapability.createVoid(create("shield"), IShieldItem.class);

        private ShieldHandler() {

        }

    }

    private static ResourceLocation create(String path) {

        return new ResourceLocation("cofh", path);
    }

    private CoreCapabilities() {

    }

}
