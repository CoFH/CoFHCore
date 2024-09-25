package cofh.lib.init.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTagsCoFH {

    private BlockTagsCoFH() {

    }

    public static final TagKey<Block> MINEABLE_WITH_SICKLE = commonTag("mineable/sickle");

    public static final TagKey<Block> PUMPKINS_CARVED = commonTag("pumpkins/carved");

    public static final TagKey<Block> ORES_ALUMINUM = commonTag("ores/aluminum");
    public static final TagKey<Block> ORES_APATITE = commonTag("ores/apatite");
    public static final TagKey<Block> ORES_CINNABAR = commonTag("ores/cinnabar");
    public static final TagKey<Block> ORES_LEAD = commonTag("ores/lead");
    public static final TagKey<Block> ORES_NICKEL = commonTag("ores/nickel");
    public static final TagKey<Block> ORES_NITER = commonTag("ores/niter");
    public static final TagKey<Block> ORES_RUBY = commonTag("ores/ruby");
    public static final TagKey<Block> ORES_SAPPHIRE = commonTag("ores/sapphire");
    public static final TagKey<Block> ORES_SILVER = commonTag("ores/silver");
    public static final TagKey<Block> ORES_SULFUR = commonTag("ores/sulfur");
    public static final TagKey<Block> ORES_TIN = commonTag("ores/tin");
    public static final TagKey<Block> ORES_URANIUM = commonTag("ores/uranium");

    public static final TagKey<Block> STORAGE_BLOCKS_ALUMINUM = commonTag("storage_blocks/aluminum");
    public static final TagKey<Block> STORAGE_BLOCKS_APATITE = commonTag("storage_blocks/apatite");
    public static final TagKey<Block> STORAGE_BLOCKS_APPLE = commonTag("storage_blocks/apple");
    public static final TagKey<Block> STORAGE_BLOCKS_BEETROOT = commonTag("storage_blocks/beetroot");
    public static final TagKey<Block> STORAGE_BLOCKS_BITUMEN = commonTag("storage_blocks/bitumen");
    public static final TagKey<Block> STORAGE_BLOCKS_BRONZE = commonTag("storage_blocks/bronze");
    public static final TagKey<Block> STORAGE_BLOCKS_CARROT = commonTag("storage_blocks/carrot");
    public static final TagKey<Block> STORAGE_BLOCKS_CHARCOAL = commonTag("storage_blocks/charcoal");
    public static final TagKey<Block> STORAGE_BLOCKS_CINNABAR = commonTag("storage_blocks/cinnabar");
    public static final TagKey<Block> STORAGE_BLOCKS_COAL_COKE = commonTag("storage_blocks/coal_coke");
    public static final TagKey<Block> STORAGE_BLOCKS_CONSTANTAN = commonTag("storage_blocks/constantan");
    public static final TagKey<Block> STORAGE_BLOCKS_ELECTRUM = commonTag("storage_blocks/electrum");
    public static final TagKey<Block> STORAGE_BLOCKS_ENDERIUM = commonTag("storage_blocks/enderium");
    public static final TagKey<Block> STORAGE_BLOCKS_GUNPOWDER = commonTag("storage_blocks/gunpowder");
    public static final TagKey<Block> STORAGE_BLOCKS_INVAR = commonTag("storage_blocks/invar");
    public static final TagKey<Block> STORAGE_BLOCKS_LEAD = commonTag("storage_blocks/lead");
    public static final TagKey<Block> STORAGE_BLOCKS_LUMIUM = commonTag("storage_blocks/lumium");
    public static final TagKey<Block> STORAGE_BLOCKS_NICKEL = commonTag("storage_blocks/nickel");
    public static final TagKey<Block> STORAGE_BLOCKS_NITER = commonTag("storage_blocks/niter");
    public static final TagKey<Block> STORAGE_BLOCKS_POTATO = commonTag("storage_blocks/potato");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_ALUMINUM = commonTag("storage_blocks/raw_aluminum");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = commonTag("storage_blocks/raw_lead");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_NICKEL = commonTag("storage_blocks/raw_nickel");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_SILVER = commonTag("storage_blocks/raw_silver");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = commonTag("storage_blocks/raw_tin");
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_URANIUM = commonTag("storage_blocks/raw_uranium");
    public static final TagKey<Block> STORAGE_BLOCKS_ROSE_GOLD = commonTag("storage_blocks/rose_gold");
    public static final TagKey<Block> STORAGE_BLOCKS_RUBY = commonTag("storage_blocks/ruby");
    public static final TagKey<Block> STORAGE_BLOCKS_SAPPHIRE = commonTag("storage_blocks/sapphire");
    public static final TagKey<Block> STORAGE_BLOCKS_SIGNALUM = commonTag("storage_blocks/signalum");
    public static final TagKey<Block> STORAGE_BLOCKS_SILVER = commonTag("storage_blocks/silver");
    public static final TagKey<Block> STORAGE_BLOCKS_SLAG = commonTag("storage_blocks/slag");
    public static final TagKey<Block> STORAGE_BLOCKS_STEEL = commonTag("storage_blocks/steel");
    public static final TagKey<Block> STORAGE_BLOCKS_SUGAR_CANE = commonTag("storage_blocks/sugar_cane");
    public static final TagKey<Block> STORAGE_BLOCKS_SULFUR = commonTag("storage_blocks/sulfur");
    public static final TagKey<Block> STORAGE_BLOCKS_TAR = commonTag("storage_blocks/tar");
    public static final TagKey<Block> STORAGE_BLOCKS_TIN = commonTag("storage_blocks/tin");
    public static final TagKey<Block> STORAGE_BLOCKS_URANIUM = commonTag("storage_blocks/uranium");

    // region HELPERS
    private static TagKey<Block> commonTag(String name) {

        return BlockTags.create(new ResourceLocation("c", name));
    }
    // endregion
}
