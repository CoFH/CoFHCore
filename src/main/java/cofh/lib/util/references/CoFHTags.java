package cofh.lib.util.references;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.constants.ModIds.ID_FORGE;

public class CoFHTags {

    public static class Blocks {

        public static final TagKey<Block> ORES_APATITE = forgeTag("ores/apatite");
        public static final TagKey<Block> ORES_CINNABAR = forgeTag("ores/cinnabar");
        public static final TagKey<Block> ORES_LEAD = forgeTag("ores/lead");
        public static final TagKey<Block> ORES_NICKEL = forgeTag("ores/nickel");
        public static final TagKey<Block> ORES_NITER = forgeTag("ores/niter");
        public static final TagKey<Block> ORES_RUBY = forgeTag("ores/ruby");
        public static final TagKey<Block> ORES_SAPPHIRE = forgeTag("ores/sapphire");
        public static final TagKey<Block> ORES_SILVER = forgeTag("ores/silver");
        public static final TagKey<Block> ORES_SULFUR = forgeTag("ores/sulfur");
        public static final TagKey<Block> ORES_TIN = forgeTag("ores/tin");

        public static final TagKey<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
        public static final TagKey<Block> STORAGE_BLOCKS_BAMBOO = forgeTag("storage_blocks/bamboo");
        public static final TagKey<Block> STORAGE_BLOCKS_BITUMEN = forgeTag("storage_blocks/bitumen");
        public static final TagKey<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
        public static final TagKey<Block> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
        public static final TagKey<Block> STORAGE_BLOCKS_CINNABAR = forgeTag("storage_blocks/cinnabar");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL_COKE = forgeTag("storage_blocks/coal_coke");
        public static final TagKey<Block> STORAGE_BLOCKS_CONSTANTAN = forgeTag("storage_blocks/constantan");
        public static final TagKey<Block> STORAGE_BLOCKS_ELECTRUM = forgeTag("storage_blocks/electrum");
        public static final TagKey<Block> STORAGE_BLOCKS_ENDERIUM = forgeTag("storage_blocks/enderium");
        public static final TagKey<Block> STORAGE_BLOCKS_GUNPOWDER = forgeTag("storage_blocks/gunpowder");
        public static final TagKey<Block> STORAGE_BLOCKS_INVAR = forgeTag("storage_blocks/invar");
        public static final TagKey<Block> STORAGE_BLOCKS_LEAD = forgeTag("storage_blocks/lead");
        public static final TagKey<Block> STORAGE_BLOCKS_LUMIUM = forgeTag("storage_blocks/lumium");
        public static final TagKey<Block> STORAGE_BLOCKS_NICKEL = forgeTag("storage_blocks/nickel");
        public static final TagKey<Block> STORAGE_BLOCKS_NITER = forgeTag("storage_blocks/niter");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = forgeTag("storage_blocks/raw_lead");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_NICKEL = forgeTag("storage_blocks/raw_nickel");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_SILVER = forgeTag("storage_blocks/raw_silver");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = forgeTag("storage_blocks/raw_tin");
        public static final TagKey<Block> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RUBY = forgeTag("storage_blocks/ruby");
        public static final TagKey<Block> STORAGE_BLOCKS_SAPPHIRE = forgeTag("storage_blocks/sapphire");
        public static final TagKey<Block> STORAGE_BLOCKS_SIGNALUM = forgeTag("storage_blocks/signalum");
        public static final TagKey<Block> STORAGE_BLOCKS_SILVER = forgeTag("storage_blocks/silver");
        public static final TagKey<Block> STORAGE_BLOCKS_SLAG = forgeTag("storage_blocks/slag");
        public static final TagKey<Block> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");
        public static final TagKey<Block> STORAGE_BLOCKS_SUGAR_CANE = forgeTag("storage_blocks/sugar_cane");
        public static final TagKey<Block> STORAGE_BLOCKS_SULFUR = forgeTag("storage_blocks/sulfur");
        public static final TagKey<Block> STORAGE_BLOCKS_TAR = forgeTag("storage_blocks/tar");
        public static final TagKey<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");

        public static final TagKey<Block> PUMPKINS_CARVED = forgeTag("pumpkins/carved");

        // region HELPERS
        private static TagKey<Block> forgeTag(String name) {

            return BlockTags.create(new ResourceLocation(ID_FORGE, name));
        }
        // endregion
    }

    public static class Items {

        public static final TagKey<Item> COINS = forgeTag("coins");

        public static final TagKey<Item> COINS_BRONZE = forgeTag("coins/bronze");
        public static final TagKey<Item> COINS_CONSTANTAN = forgeTag("coins/constantan");
        public static final TagKey<Item> COINS_COPPER = forgeTag("coins/copper");
        public static final TagKey<Item> COINS_ELECTRUM = forgeTag("coins/electrum");
        public static final TagKey<Item> COINS_ENDERIUM = forgeTag("coins/enderium");
        public static final TagKey<Item> COINS_GOLD = forgeTag("coins/gold");
        public static final TagKey<Item> COINS_INVAR = forgeTag("coins/invar");
        public static final TagKey<Item> COINS_IRON = forgeTag("coins/iron");
        public static final TagKey<Item> COINS_LEAD = forgeTag("coins/lead");
        public static final TagKey<Item> COINS_LUMIUM = forgeTag("coins/lumium");
        public static final TagKey<Item> COINS_NETHERITE = forgeTag("coins/netherite");
        public static final TagKey<Item> COINS_NICKEL = forgeTag("coins/nickel");
        public static final TagKey<Item> COINS_ROSE_GOLD = forgeTag("coins/rose_gold");
        public static final TagKey<Item> COINS_SIGNALUM = forgeTag("coins/signalum");
        public static final TagKey<Item> COINS_SILVER = forgeTag("coins/silver");
        public static final TagKey<Item> COINS_STEEL = forgeTag("coins/steel");
        public static final TagKey<Item> COINS_TIN = forgeTag("coins/tin");

        public static final TagKey<Item> CROPS_AMARANTH = forgeTag("crops/amaranth");
        public static final TagKey<Item> CROPS_BARLEY = forgeTag("crops/barley");
        public static final TagKey<Item> CROPS_BELL_PEPPER = forgeTag("crops/bell_pepper");
        public static final TagKey<Item> CROPS_COFFEE = forgeTag("crops/coffee");
        public static final TagKey<Item> CROPS_CORN = forgeTag("crops/corn");
        public static final TagKey<Item> CROPS_EGGPLANT = forgeTag("crops/eggplant");
        public static final TagKey<Item> CROPS_FLAX = forgeTag("crops/flax");
        public static final TagKey<Item> CROPS_GREEN_BEAN = forgeTag("crops/green_bean");
        public static final TagKey<Item> CROPS_HOPS = forgeTag("crops/hops");
        public static final TagKey<Item> CROPS_ONION = forgeTag("crops/onion");
        public static final TagKey<Item> CROPS_PEANUT = forgeTag("crops/peanut");
        public static final TagKey<Item> CROPS_RADISH = forgeTag("crops/radish");
        public static final TagKey<Item> CROPS_RICE = forgeTag("crops/rice");
        public static final TagKey<Item> CROPS_SADIROOT = forgeTag("crops/sadiroot");
        public static final TagKey<Item> CROPS_SPINACH = forgeTag("crops/spinach");
        public static final TagKey<Item> CROPS_STRAWBERRY = forgeTag("crops/strawberry");
        public static final TagKey<Item> CROPS_TEA = forgeTag("crops/tea");
        public static final TagKey<Item> CROPS_TOMATO = forgeTag("crops/tomato");

        public static final TagKey<Item> DUSTS_APATITE = forgeTag("dusts/apatite");
        public static final TagKey<Item> DUSTS_BRONZE = forgeTag("dusts/bronze");
        public static final TagKey<Item> DUSTS_CINNABAR = forgeTag("dusts/cinnabar");
        public static final TagKey<Item> DUSTS_CONSTANTAN = forgeTag("dusts/constantan");
        public static final TagKey<Item> DUSTS_COPPER = forgeTag("dusts/copper");
        public static final TagKey<Item> DUSTS_DIAMOND = forgeTag("dusts/diamond");
        public static final TagKey<Item> DUSTS_ELECTRUM = forgeTag("dusts/electrum");
        public static final TagKey<Item> DUSTS_EMERALD = forgeTag("dusts/emerald");
        public static final TagKey<Item> DUSTS_ENDER_PEARL = forgeTag("dusts/ender_pearl");
        public static final TagKey<Item> DUSTS_ENDERIUM = forgeTag("dusts/enderium");
        public static final TagKey<Item> DUSTS_GOLD = forgeTag("dusts/gold");
        public static final TagKey<Item> DUSTS_INVAR = forgeTag("dusts/invar");
        public static final TagKey<Item> DUSTS_IRON = forgeTag("dusts/iron");
        public static final TagKey<Item> DUSTS_LAPIS = forgeTag("dusts/lapis");
        public static final TagKey<Item> DUSTS_LEAD = forgeTag("dusts/lead");
        public static final TagKey<Item> DUSTS_LUMIUM = forgeTag("dusts/lumium");
        public static final TagKey<Item> DUSTS_NETHERITE = forgeTag("dusts/netherite");
        public static final TagKey<Item> DUSTS_NICKEL = forgeTag("dusts/nickel");
        public static final TagKey<Item> DUSTS_NITER = forgeTag("dusts/niter");
        public static final TagKey<Item> DUSTS_QUARTZ = forgeTag("dusts/quartz");
        public static final TagKey<Item> DUSTS_ROSE_GOLD = forgeTag("dusts/rose_gold");
        public static final TagKey<Item> DUSTS_RUBY = forgeTag("dusts/ruby");
        public static final TagKey<Item> DUSTS_SAPPHIRE = forgeTag("dusts/sapphire");
        public static final TagKey<Item> DUSTS_SIGNALUM = forgeTag("dusts/signalum");
        public static final TagKey<Item> DUSTS_SILVER = forgeTag("dusts/silver");
        public static final TagKey<Item> DUSTS_STEEL = forgeTag("dusts/steel");
        public static final TagKey<Item> DUSTS_SULFUR = forgeTag("dusts/sulfur");
        public static final TagKey<Item> DUSTS_TIN = forgeTag("dusts/tin");
        public static final TagKey<Item> DUSTS_WOOD = forgeTag("dusts/wood");

        public static final TagKey<Item> GEARS = forgeTag("gears");

        public static final TagKey<Item> GEARS_BRONZE = forgeTag("gears/bronze");
        public static final TagKey<Item> GEARS_CONSTANTAN = forgeTag("gears/constantan");
        public static final TagKey<Item> GEARS_COPPER = forgeTag("gears/copper");
        public static final TagKey<Item> GEARS_DIAMOND = forgeTag("gears/diamond");
        public static final TagKey<Item> GEARS_ELECTRUM = forgeTag("gears/electrum");
        public static final TagKey<Item> GEARS_EMERALD = forgeTag("gears/emerald");
        public static final TagKey<Item> GEARS_ENDERIUM = forgeTag("gears/enderium");
        public static final TagKey<Item> GEARS_GOLD = forgeTag("gears/gold");
        public static final TagKey<Item> GEARS_INVAR = forgeTag("gears/invar");
        public static final TagKey<Item> GEARS_IRON = forgeTag("gears/iron");
        public static final TagKey<Item> GEARS_LAPIS = forgeTag("gears/lapis");
        public static final TagKey<Item> GEARS_LEAD = forgeTag("gears/lead");
        public static final TagKey<Item> GEARS_LUMIUM = forgeTag("gears/lumium");
        public static final TagKey<Item> GEARS_NETHERITE = forgeTag("gears/netherite");
        public static final TagKey<Item> GEARS_NICKEL = forgeTag("gears/nickel");
        public static final TagKey<Item> GEARS_QUARTZ = forgeTag("gears/quartz");
        public static final TagKey<Item> GEARS_ROSE_GOLD = forgeTag("gears/rose_gold");
        public static final TagKey<Item> GEARS_RUBY = forgeTag("gears/ruby");
        public static final TagKey<Item> GEARS_SAPPHIRE = forgeTag("gears/sapphire");
        public static final TagKey<Item> GEARS_SIGNALUM = forgeTag("gears/signalum");
        public static final TagKey<Item> GEARS_SILVER = forgeTag("gears/silver");
        public static final TagKey<Item> GEARS_STEEL = forgeTag("gears/steel");
        public static final TagKey<Item> GEARS_TIN = forgeTag("gears/tin");

        public static final TagKey<Item> GEMS_APATITE = forgeTag("gems/apatite");
        public static final TagKey<Item> GEMS_CINNABAR = forgeTag("gems/cinnabar");
        public static final TagKey<Item> GEMS_NITER = forgeTag("gems/niter");
        public static final TagKey<Item> GEMS_RUBY = forgeTag("gems/ruby");
        public static final TagKey<Item> GEMS_SAPPHIRE = forgeTag("gems/sapphire");
        public static final TagKey<Item> GEMS_SULFUR = forgeTag("gems/sulfur");

        public static final TagKey<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
        public static final TagKey<Item> INGOTS_CONSTANTAN = forgeTag("ingots/constantan");
        public static final TagKey<Item> INGOTS_ELECTRUM = forgeTag("ingots/electrum");
        public static final TagKey<Item> INGOTS_ENDERIUM = forgeTag("ingots/enderium");
        public static final TagKey<Item> INGOTS_INVAR = forgeTag("ingots/invar");
        public static final TagKey<Item> INGOTS_LEAD = forgeTag("ingots/lead");
        public static final TagKey<Item> INGOTS_LUMIUM = forgeTag("ingots/lumium");
        public static final TagKey<Item> INGOTS_NICKEL = forgeTag("ingots/nickel");
        public static final TagKey<Item> INGOTS_ROSE_GOLD = forgeTag("ingots/rose_gold");
        public static final TagKey<Item> INGOTS_SIGNALUM = forgeTag("ingots/signalum");
        public static final TagKey<Item> INGOTS_SILVER = forgeTag("ingots/silver");
        public static final TagKey<Item> INGOTS_STEEL = forgeTag("ingots/steel");
        public static final TagKey<Item> INGOTS_TIN = forgeTag("ingots/tin");

        public static final TagKey<Item> NUGGETS_BRONZE = forgeTag("nuggets/bronze");
        public static final TagKey<Item> NUGGETS_CONSTANTAN = forgeTag("nuggets/constantan");
        public static final TagKey<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
        // public static final TagKey<Item> NUGGETS_DIAMOND = forgeTag("nuggets/diamond");
        public static final TagKey<Item> NUGGETS_ELECTRUM = forgeTag("nuggets/electrum");
        // public static final TagKey<Item> NUGGETS_EMERALD = forgeTag("nuggets/emerald");
        public static final TagKey<Item> NUGGETS_ENDERIUM = forgeTag("nuggets/enderium");
        public static final TagKey<Item> NUGGETS_INVAR = forgeTag("nuggets/invar");
        // public static final TagKey<Item> NUGGETS_LAPIS = forgeTag("nuggets/lapis");
        public static final TagKey<Item> NUGGETS_LEAD = forgeTag("nuggets/lead");
        public static final TagKey<Item> NUGGETS_LUMIUM = forgeTag("nuggets/lumium");
        public static final TagKey<Item> NUGGETS_NETHERITE = forgeTag("nuggets/netherite");
        public static final TagKey<Item> NUGGETS_NICKEL = forgeTag("nuggets/nickel");
        // public static final TagKey<Item> NUGGETS_QUARTZ = forgeTag("nuggets/quartz");
        public static final TagKey<Item> NUGGETS_ROSE_GOLD = forgeTag("nuggets/rose_gold");
        // public static final TagKey<Item> NUGGETS_RUBY = forgeTag("nuggets/ruby");
        // public static final TagKey<Item> NUGGETS_SAPPHIRE = forgeTag("nuggets/sapphire");
        public static final TagKey<Item> NUGGETS_SIGNALUM = forgeTag("nuggets/signalum");
        public static final TagKey<Item> NUGGETS_SILVER = forgeTag("nuggets/silver");
        public static final TagKey<Item> NUGGETS_STEEL = forgeTag("nuggets/steel");
        public static final TagKey<Item> NUGGETS_TIN = forgeTag("nuggets/tin");

        public static final TagKey<Item> ORES_APATITE = forgeTag("ores/apatite");
        public static final TagKey<Item> ORES_CINNABAR = forgeTag("ores/cinnabar");
        public static final TagKey<Item> ORES_LEAD = forgeTag("ores/lead");
        public static final TagKey<Item> ORES_NICKEL = forgeTag("ores/nickel");
        public static final TagKey<Item> ORES_NITER = forgeTag("ores/niter");
        public static final TagKey<Item> ORES_RUBY = forgeTag("ores/ruby");
        public static final TagKey<Item> ORES_SAPPHIRE = forgeTag("ores/sapphire");
        public static final TagKey<Item> ORES_SILVER = forgeTag("ores/silver");
        public static final TagKey<Item> ORES_SULFUR = forgeTag("ores/sulfur");
        public static final TagKey<Item> ORES_TIN = forgeTag("ores/tin");

        public static final TagKey<Item> PLATES = forgeTag("plates");

        public static final TagKey<Item> PLATES_BRONZE = forgeTag("plates/bronze");
        public static final TagKey<Item> PLATES_CONSTANTAN = forgeTag("plates/constantan");
        public static final TagKey<Item> PLATES_COPPER = forgeTag("plates/copper");
        // public static final TagKey<Item> PLATES_DIAMOND = forgeTag("plates/diamond");
        public static final TagKey<Item> PLATES_ELECTRUM = forgeTag("plates/electrum");
        // public static final TagKey<Item> PLATES_EMERALD = forgeTag("plates/emerald");
        public static final TagKey<Item> PLATES_ENDERIUM = forgeTag("plates/enderium");
        public static final TagKey<Item> PLATES_GOLD = forgeTag("plates/gold");
        public static final TagKey<Item> PLATES_INVAR = forgeTag("plates/invar");
        public static final TagKey<Item> PLATES_IRON = forgeTag("plates/iron");
        // public static final TagKey<Item> PLATES_LAPIS = forgeTag("plates/lapis");
        public static final TagKey<Item> PLATES_LEAD = forgeTag("plates/lead");
        public static final TagKey<Item> PLATES_LUMIUM = forgeTag("plates/lumium");
        public static final TagKey<Item> PLATES_NETHERITE = forgeTag("plates/netherite");
        public static final TagKey<Item> PLATES_NICKEL = forgeTag("plates/nickel");
        // public static final TagKey<Item> PLATES_QUARTZ = forgeTag("plates/quartz");
        public static final TagKey<Item> PLATES_ROSE_GOLD = forgeTag("plates/rose_gold");
        // public static final TagKey<Item> PLATES_RUBY = forgeTag("plates/ruby");
        // public static final TagKey<Item> PLATES_SAPPHIRE = forgeTag("plates/sapphire");
        public static final TagKey<Item> PLATES_SIGNALUM = forgeTag("plates/signalum");
        public static final TagKey<Item> PLATES_SILVER = forgeTag("plates/silver");
        public static final TagKey<Item> PLATES_STEEL = forgeTag("plates/steel");
        public static final TagKey<Item> PLATES_TIN = forgeTag("plates/tin");

        public static final TagKey<Item> RAW_MATERIALS_LEAD = forgeTag("raw_materials/lead");
        public static final TagKey<Item> RAW_MATERIALS_NICKEL = forgeTag("raw_materials/nickel");
        public static final TagKey<Item> RAW_MATERIALS_SILVER = forgeTag("raw_materials/silver");
        public static final TagKey<Item> RAW_MATERIALS_TIN = forgeTag("raw_materials/tin");

        public static final TagKey<Item> SEEDS_AMARANTH = forgeTag("seeds/amaranth");
        public static final TagKey<Item> SEEDS_BARLEY = forgeTag("seeds/barley");
        public static final TagKey<Item> SEEDS_BELL_PEPPER = forgeTag("seeds/bell_pepper");
        public static final TagKey<Item> SEEDS_COFFEE = forgeTag("seeds/coffee");
        public static final TagKey<Item> SEEDS_CORN = forgeTag("seeds/corn");
        public static final TagKey<Item> SEEDS_FROST_MELON = forgeTag("seeds/frost_melon");
        public static final TagKey<Item> SEEDS_EGGPLANT = forgeTag("seeds/eggplant");
        public static final TagKey<Item> SEEDS_FLAX = forgeTag("seeds/flax");
        public static final TagKey<Item> SEEDS_GREEN_BEAN = forgeTag("seeds/green_bean");
        public static final TagKey<Item> SEEDS_HOPS = forgeTag("seeds/hops");
        public static final TagKey<Item> SEEDS_ONION = forgeTag("seeds/onion");
        public static final TagKey<Item> SEEDS_PEANUT = forgeTag("seeds/peanut");
        public static final TagKey<Item> SEEDS_RADISH = forgeTag("seeds/radish");
        public static final TagKey<Item> SEEDS_RICE = forgeTag("seeds/rice");
        public static final TagKey<Item> SEEDS_SADIROOT = forgeTag("seeds/sadiroot");
        public static final TagKey<Item> SEEDS_SPINACH = forgeTag("seeds/spinach");
        public static final TagKey<Item> SEEDS_STRAWBERRY = forgeTag("seeds/strawberry");
        public static final TagKey<Item> SEEDS_TEA = forgeTag("seeds/tea");
        public static final TagKey<Item> SEEDS_TOMATO = forgeTag("seeds/tomato");

        public static final TagKey<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
        public static final TagKey<Item> STORAGE_BLOCKS_BAMBOO = forgeTag("storage_blocks/bamboo");
        public static final TagKey<Item> STORAGE_BLOCKS_BITUMEN = forgeTag("storage_blocks/bitumen");
        public static final TagKey<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
        public static final TagKey<Item> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
        public static final TagKey<Item> STORAGE_BLOCKS_CINNABAR = forgeTag("storage_blocks/cinnabar");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL_COKE = forgeTag("storage_blocks/coal_coke");
        public static final TagKey<Item> STORAGE_BLOCKS_CONSTANTAN = forgeTag("storage_blocks/constantan");
        public static final TagKey<Item> STORAGE_BLOCKS_ELECTRUM = forgeTag("storage_blocks/electrum");
        public static final TagKey<Item> STORAGE_BLOCKS_ENDERIUM = forgeTag("storage_blocks/enderium");
        public static final TagKey<Item> STORAGE_BLOCKS_GUNPOWDER = forgeTag("storage_blocks/gunpowder");
        public static final TagKey<Item> STORAGE_BLOCKS_INVAR = forgeTag("storage_blocks/invar");
        public static final TagKey<Item> STORAGE_BLOCKS_LEAD = forgeTag("storage_blocks/lead");
        public static final TagKey<Item> STORAGE_BLOCKS_LUMIUM = forgeTag("storage_blocks/lumium");
        public static final TagKey<Item> STORAGE_BLOCKS_NICKEL = forgeTag("storage_blocks/nickel");
        public static final TagKey<Item> STORAGE_BLOCKS_NITER = forgeTag("storage_blocks/niter");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_LEAD = forgeTag("storage_blocks/raw_lead");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_NICKEL = forgeTag("storage_blocks/raw_nickel");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_SILVER = forgeTag("storage_blocks/raw_silver");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_TIN = forgeTag("storage_blocks/raw_tin");
        public static final TagKey<Item> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RUBY = forgeTag("storage_blocks/ruby");
        public static final TagKey<Item> STORAGE_BLOCKS_SAPPHIRE = forgeTag("storage_blocks/sapphire");
        public static final TagKey<Item> STORAGE_BLOCKS_SIGNALUM = forgeTag("storage_blocks/signalum");
        public static final TagKey<Item> STORAGE_BLOCKS_SILVER = forgeTag("storage_blocks/silver");
        public static final TagKey<Item> STORAGE_BLOCKS_SLAG = forgeTag("storage_blocks/slag");
        public static final TagKey<Item> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");
        public static final TagKey<Item> STORAGE_BLOCKS_SUGAR_CANE = forgeTag("storage_blocks/sugar_cane");
        public static final TagKey<Item> STORAGE_BLOCKS_SULFUR = forgeTag("storage_blocks/sulfur");
        public static final TagKey<Item> STORAGE_BLOCKS_TAR = forgeTag("storage_blocks/tar");
        public static final TagKey<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");

        public static final TagKey<Item> PUMPKINS_CARVED = forgeTag("pumpkins/carved");

        public static final TagKey<Item> ARMOR_IRON = forgeTag("armor/iron");
        public static final TagKey<Item> ARMOR_GOLD = forgeTag("armor/gold");
        public static final TagKey<Item> ARMOR_DIAMOND = forgeTag("armor/diamond");
        public static final TagKey<Item> ARMOR_NETHERITE = forgeTag("armor/netherite");
        public static final TagKey<Item> ARMOR_COPPER = forgeTag("armor/copper");
        public static final TagKey<Item> ARMOR_TIN = forgeTag("armor/tin");
        public static final TagKey<Item> ARMOR_LEAD = forgeTag("armor/lead");
        public static final TagKey<Item> ARMOR_SILVER = forgeTag("armor/silver");
        public static final TagKey<Item> ARMOR_NICKEL = forgeTag("armor/nickel");
        public static final TagKey<Item> ARMOR_BRONZE = forgeTag("armor/bronze");
        public static final TagKey<Item> ARMOR_ELECTRUM = forgeTag("armor/electrum");
        public static final TagKey<Item> ARMOR_INVAR = forgeTag("armor/invar");
        public static final TagKey<Item> ARMOR_CONSTANTAN = forgeTag("armor/constantan");
        public static final TagKey<Item> ARMOR_STEEL = forgeTag("armor/steel");
        public static final TagKey<Item> ARMOR_ROSE_GOLD = forgeTag("armor/rose_gold");

        public static final TagKey<Item> TOOLS_IRON = forgeTag("tools/iron");
        public static final TagKey<Item> TOOLS_GOLD = forgeTag("tools/gold");
        public static final TagKey<Item> TOOLS_DIAMOND = forgeTag("tools/diamond");
        public static final TagKey<Item> TOOLS_NETHERITE = forgeTag("tools/netherite");
        public static final TagKey<Item> TOOLS_COPPER = forgeTag("tools/copper");
        public static final TagKey<Item> TOOLS_TIN = forgeTag("tools/tin");
        public static final TagKey<Item> TOOLS_LEAD = forgeTag("tools/lead");
        public static final TagKey<Item> TOOLS_SILVER = forgeTag("tools/silver");
        public static final TagKey<Item> TOOLS_NICKEL = forgeTag("tools/nickel");
        public static final TagKey<Item> TOOLS_BRONZE = forgeTag("tools/bronze");
        public static final TagKey<Item> TOOLS_ELECTRUM = forgeTag("tools/electrum");
        public static final TagKey<Item> TOOLS_INVAR = forgeTag("tools/invar");
        public static final TagKey<Item> TOOLS_CONSTANTAN = forgeTag("tools/constantan");
        public static final TagKey<Item> TOOLS_STEEL = forgeTag("tools/steel");
        public static final TagKey<Item> TOOLS_ROSE_GOLD = forgeTag("tools/rose_gold");

        public static final TagKey<Item> TOOLS_WRENCH = forgeTag("tools/wrench");

        public static final TagKey<Item> COOKED_MEAT = forgeTag("foods/meat/cooked");
        public static final TagKey<Item> RAW_MEAT = forgeTag("foods/meat/raw");

        public static final TagKey<Item> COOKED_FISH = forgeTag("foods/fish/cooked");
        public static final TagKey<Item> RAW_FISH = forgeTag("foods/fish/raw");

        public static final TagKey<Item> LOCKS = cofhTag("crafting/locks");
        public static final TagKey<Item> SECURABLE = cofhTag("crafting/securable");

        // region HELPERS
        private static TagKey<Item> cofhTag(String name) {

            return ItemTags.create(new ResourceLocation(ID_COFH_CORE, name));
        }

        private static TagKey<Item> forgeTag(String name) {

            return ItemTags.create(new ResourceLocation(ID_FORGE, name));
        }
        // endregion
    }

    public static class Fluids {

        public static final TagKey<Fluid> EXPERIENCE = forgeTag("experience");
        public static final TagKey<Fluid> HONEY = forgeTag("honey");
        public static final TagKey<Fluid> POTION = forgeTag("potion");

        // region HELPERS
        private static TagKey<Fluid> forgeTag(String name) {

            return FluidTags.create(new ResourceLocation(ID_FORGE, name));
        }
        // endregion
    }

}
