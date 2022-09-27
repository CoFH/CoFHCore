package cofh.core.init;

import cofh.core.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.BLOCKS;
import static cofh.core.util.references.CoreIDs.*;
import static cofh.lib.util.helpers.BlockHelper.lightValue;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy;

public class CoreBlocks {

    private CoreBlocks() {

    }

    public static void register() {

    }

    public static final RegistryObject<Block> GLOSSED_MAGMA = BLOCKS.register(ID_GLOSSED_MAGMA, () -> new GlossedMagmaBlock(copy(Blocks.MAGMA_BLOCK).lightLevel(lightValue(6))));
    public static final RegistryObject<Block> SIGNAL_AIR = BLOCKS.register(ID_SIGNAL_AIR, () -> new SignalAirBlock(copy(Blocks.AIR).lightLevel(lightValue(7))));
    public static final RegistryObject<Block> GLOW_AIR = BLOCKS.register(ID_GLOW_AIR, () -> new GlowAirBlock(copy(Blocks.AIR).lightLevel(lightValue(15))));
    public static final RegistryObject<Block> ENDER_AIR = BLOCKS.register(ID_ENDER_AIR, () -> new EnderAirBlock(copy(Blocks.AIR).lightLevel(lightValue(3))));
    public static final RegistryObject<Block> LIGHTNING_AIR = BLOCKS.register(ID_LIGHTNING_AIR, () -> new LightningAirBlock(copy(Blocks.AIR)));

}
