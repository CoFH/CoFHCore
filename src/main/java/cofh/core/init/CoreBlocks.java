package cofh.core.init;

import cofh.core.block.*;
import cofh.core.tileentity.EnderAirTile;
import cofh.core.tileentity.GlowAirTile;
import cofh.core.tileentity.LightningAirTile;
import cofh.core.tileentity.SignalAirTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static cofh.core.CoFHCore.BLOCKS;
import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.lib.util.helpers.BlockHelper.lightValue;
import static cofh.lib.util.references.CoreIDs.*;
import static cofh.lib.util.references.CoreReferences.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy;

public class CoreBlocks {

    private CoreBlocks() {

    }

    public static void register() {

        BLOCKS.register(ID_GLOSSED_MAGMA, () -> new GlossedMagmaBlock(copy(Blocks.MAGMA_BLOCK).lightLevel(lightValue(6))));

        BLOCKS.register(ID_SIGNAL_AIR, () -> new SignalAirBlock(copy(Blocks.AIR).lightLevel(lightValue(7)).noCollission().noDrops()));
        BLOCKS.register(ID_GLOW_AIR, () -> new GlowAirBlock(copy(Blocks.AIR).lightLevel(lightValue(15))));
        BLOCKS.register(ID_ENDER_AIR, () -> new EnderAirBlock(copy(Blocks.AIR).lightLevel(lightValue(3))));
        BLOCKS.register(ID_LIGHTNING_AIR, () -> new LightningAirBlock(copy(Blocks.AIR)));

        TILE_ENTITIES.register(ID_SIGNAL_AIR, () -> BlockEntityType.Builder.of(SignalAirTile::new, SIGNAL_AIR).build(null));
        TILE_ENTITIES.register(ID_GLOW_AIR, () -> BlockEntityType.Builder.of(GlowAirTile::new, GLOW_AIR).build(null));
        TILE_ENTITIES.register(ID_ENDER_AIR, () -> BlockEntityType.Builder.of(EnderAirTile::new, ENDER_AIR).build(null));
        TILE_ENTITIES.register(ID_LIGHTNING_AIR, () -> BlockEntityType.Builder.of(LightningAirTile::new, LIGHTNING_AIR).build(null));
    }

}
