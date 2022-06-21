package cofh.core.init;

import cofh.core.content.block.entity.EnderAirTile;
import cofh.core.content.block.entity.GlowAirTile;
import cofh.core.content.block.entity.LightningAirTile;
import cofh.core.content.block.entity.SignalAirTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.core.init.CoreBlocks.*;
import static cofh.core.util.references.CoreIDs.*;

public class CoreTileEntities {

    private CoreTileEntities() {

    }

    public static void register() {


    }

    public static final RegistryObject<BlockEntityType<SignalAirTile>> SIGNAL_AIR_TILE = TILE_ENTITIES.register(ID_SIGNAL_AIR, () -> BlockEntityType.Builder.of(SignalAirTile::new, SIGNAL_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<GlowAirTile>> GLOW_AIR_TILE = TILE_ENTITIES.register(ID_GLOW_AIR, () -> BlockEntityType.Builder.of(GlowAirTile::new, GLOW_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnderAirTile>> ENDER_AIR_TILE = TILE_ENTITIES.register(ID_ENDER_AIR, () -> BlockEntityType.Builder.of(EnderAirTile::new, ENDER_AIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<LightningAirTile>> LIGHTNING_AIR_TILE = TILE_ENTITIES.register(ID_LIGHTNING_AIR, () -> BlockEntityType.Builder.of(LightningAirTile::new, LIGHTNING_AIR.get()).build(null));

}
