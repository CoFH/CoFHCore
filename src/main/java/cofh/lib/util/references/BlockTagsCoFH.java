package cofh.lib.util.references;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

import static cofh.lib.util.constants.Constants.ID_FORGE;
import static cofh.lib.util.constants.Constants.ID_THERMAL;

public class BlockTagsCoFH {

    public static final IOptionalNamedTag<Block> HARDENED_GLASS = thermalTag("glass/hardened");
    public static final IOptionalNamedTag<Block> ROCKWOOL = thermalTag("rockwool");

    public static final IOptionalNamedTag<Block> ORES_APATITE = forgeTag("ores/apatite");
    public static final IOptionalNamedTag<Block> ORES_CINNABAR = forgeTag("ores/cinnabar");
    public static final IOptionalNamedTag<Block> ORES_COPPER = forgeTag("ores/copper");
    public static final IOptionalNamedTag<Block> ORES_LEAD = forgeTag("ores/lead");
    public static final IOptionalNamedTag<Block> ORES_NICKEL = forgeTag("ores/nickel");
    public static final IOptionalNamedTag<Block> ORES_NITER = forgeTag("ores/niter");
    public static final IOptionalNamedTag<Block> ORES_RUBY = forgeTag("ores/ruby");
    public static final IOptionalNamedTag<Block> ORES_SAPPHIRE = forgeTag("ores/sapphire");
    public static final IOptionalNamedTag<Block> ORES_SILVER = forgeTag("ores/silver");
    public static final IOptionalNamedTag<Block> ORES_SULFUR = forgeTag("ores/sulfur");
    public static final IOptionalNamedTag<Block> ORES_TIN = forgeTag("ores/tin");

    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_BAMBOO = forgeTag("storage_blocks/bamboo");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_BITUMEN = forgeTag("storage_blocks/bitumen");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_CINNABAR = forgeTag("storage_blocks/cinnabar");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COAL_COKE = forgeTag("storage_blocks/coal_coke");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_CONSTANTAN = forgeTag("storage_blocks/constantan");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ELECTRUM = forgeTag("storage_blocks/electrum");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ENDERIUM = forgeTag("storage_blocks/enderium");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_GUNPOWDER = forgeTag("storage_blocks/gunpowder");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_INVAR = forgeTag("storage_blocks/invar");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_LEAD = forgeTag("storage_blocks/lead");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_LUMIUM = forgeTag("storage_blocks/lumium");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_NICKEL = forgeTag("storage_blocks/nickel");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_NITER = forgeTag("storage_blocks/niter");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_RUBY = forgeTag("storage_blocks/ruby");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SAPPHIRE = forgeTag("storage_blocks/sapphire");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SIGNALUM = forgeTag("storage_blocks/signalum");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SILVER = forgeTag("storage_blocks/silver");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SLAG = forgeTag("storage_blocks/slag");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SUGAR_CANE = forgeTag("storage_blocks/sugar_cane");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SULFUR = forgeTag("storage_blocks/sulfur");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_TAR = forgeTag("storage_blocks/tar");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");

    // region HELPERS
    private static IOptionalNamedTag<Block> thermalTag(String name) {

        return BlockTags.createOptional(new ResourceLocation(ID_THERMAL, name));
    }

    private static IOptionalNamedTag<Block> forgeTag(String name) {

        return BlockTags.createOptional(new ResourceLocation(ID_FORGE, name));
    }
    // endregion
}
