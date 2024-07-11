package cofh.core.init;

import cofh.core.common.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;

import static cofh.core.CoFHCore.BLOCKS;
import static cofh.core.util.references.CoreIDs.*;
import static cofh.lib.util.helpers.BlockHelper.lightValue;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public class CoreBlocks {

    private CoreBlocks() {

    }

    public static void register() {

    }

    public static final DeferredHolder<Block, Block> GLOSSED_MAGMA = BLOCKS.register(ID_GLOSSED_MAGMA, () -> new GlossedMagmaBlock(ofFullCopy(Blocks.MAGMA_BLOCK).lightLevel(lightValue(6))));
    public static final DeferredHolder<Block, Block> SIGNAL_AIR = BLOCKS.register(ID_SIGNAL_AIR, () -> new SignalAirBlock(ofFullCopy(Blocks.AIR).lightLevel(lightValue(7))));
    public static final DeferredHolder<Block, Block> GLOW_AIR = BLOCKS.register(ID_GLOW_AIR, () -> new GlowAirBlock(ofFullCopy(Blocks.AIR).lightLevel(lightValue(15))));
    public static final DeferredHolder<Block, Block> ENDER_AIR = BLOCKS.register(ID_ENDER_AIR, () -> new EnderAirBlock(ofFullCopy(Blocks.AIR).lightLevel(lightValue(3))));
    public static final DeferredHolder<Block, Block> LIGHTNING_AIR = BLOCKS.register(ID_LIGHTNING_AIR, () -> new LightningAirBlock(ofFullCopy(Blocks.AIR)));

}
