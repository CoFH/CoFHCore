package cofh.core.data;

import cofh.lib.util.references.BlockTagsCoFH;
import cofh.lib.util.references.FluidTagsCoFH;
import cofh.lib.util.references.ItemTagsCoFH;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreTagsProvider {

    public static class Block extends BlockTagsProvider {

        public Block(DataGenerator gen, ExistingFileHelper existingFileHelper) {

            super(gen, ID_COFH_CORE, existingFileHelper);
        }

        @Override
        public String getName() {

            return "CoFH Core: Block Tags";
        }

        @Override
        protected void addTags() {

            tag(BlockTagsCoFH.LOGS_RUBBERWOOD);

            tag(BlockTagsCoFH.ORES_APATITE);
            tag(BlockTagsCoFH.ORES_CINNABAR);
            tag(BlockTagsCoFH.ORES_COPPER);
            tag(BlockTagsCoFH.ORES_LEAD);
            tag(BlockTagsCoFH.ORES_NICKEL);
            tag(BlockTagsCoFH.ORES_NITER);
            tag(BlockTagsCoFH.ORES_RUBY);
            tag(BlockTagsCoFH.ORES_SAPPHIRE);
            tag(BlockTagsCoFH.ORES_SILVER);
            tag(BlockTagsCoFH.ORES_SULFUR);
            tag(BlockTagsCoFH.ORES_TIN);

            tag(BlockTagsCoFH.STORAGE_BLOCKS_APATITE);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_BAMBOO);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_BITUMEN);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_BRONZE);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_CHARCOAL);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_CINNABAR);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_COAL_COKE);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_CONSTANTAN);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_COPPER);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_ELECTRUM);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_ENDERIUM);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_GUNPOWDER);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_INVAR);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_LEAD);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_LUMIUM);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_NICKEL);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_NITER);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_RUBY);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SAPPHIRE);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SIGNALUM);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SILVER);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SLAG);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SUGAR_CANE);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_SULFUR);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_TAR);
            tag(BlockTagsCoFH.STORAGE_BLOCKS_TIN);

            tag(BlockTagsCoFH.PUMPKINS_CARVED).add(Blocks.CARVED_PUMPKIN);

            tag(BlockTagsCoFH.HARDENED_GLASS);
            tag(BlockTagsCoFH.ROCKWOOL);
        }

    }

    public static class Item extends ItemTagsProvider {

        public Item(DataGenerator gen, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {

            super(gen, blockTagProvider, ID_COFH_CORE, existingFileHelper);
        }

        @Override
        public String getName() {

            return "CoFH Core: Item Tags";
        }

        @Override
        protected void addTags() {

            tag(ItemTagsCoFH.COINS);

            tag(ItemTagsCoFH.COINS_BRONZE);
            tag(ItemTagsCoFH.COINS_CONSTANTAN);
            tag(ItemTagsCoFH.COINS_COPPER);
            tag(ItemTagsCoFH.COINS_ELECTRUM);
            tag(ItemTagsCoFH.COINS_ENDERIUM);
            tag(ItemTagsCoFH.COINS_GOLD);
            tag(ItemTagsCoFH.COINS_INVAR);
            tag(ItemTagsCoFH.COINS_IRON);
            tag(ItemTagsCoFH.COINS_LEAD);
            tag(ItemTagsCoFH.COINS_LUMIUM);
            tag(ItemTagsCoFH.COINS_NETHERITE);
            tag(ItemTagsCoFH.COINS_NICKEL);
            tag(ItemTagsCoFH.COINS_SIGNALUM);
            tag(ItemTagsCoFH.COINS_SILVER);
            tag(ItemTagsCoFH.COINS_TIN);

            tag(ItemTagsCoFH.CROPS_AMARANTH);
            tag(ItemTagsCoFH.CROPS_BARLEY);
            tag(ItemTagsCoFH.CROPS_BELL_PEPPER);
            tag(ItemTagsCoFH.CROPS_COFFEE);
            tag(ItemTagsCoFH.CROPS_CORN);
            tag(ItemTagsCoFH.CROPS_EGGPLANT);
            tag(ItemTagsCoFH.CROPS_FLAX);
            tag(ItemTagsCoFH.CROPS_GREEN_BEAN);
            tag(ItemTagsCoFH.CROPS_HOPS);
            tag(ItemTagsCoFH.CROPS_ONION);
            tag(ItemTagsCoFH.CROPS_PEANUT);
            tag(ItemTagsCoFH.CROPS_RADISH);
            tag(ItemTagsCoFH.CROPS_RICE);
            tag(ItemTagsCoFH.CROPS_SADIROOT);
            tag(ItemTagsCoFH.CROPS_SPINACH);
            tag(ItemTagsCoFH.CROPS_STRAWBERRY);
            tag(ItemTagsCoFH.CROPS_TEA);
            tag(ItemTagsCoFH.CROPS_TOMATO);

            tag(ItemTagsCoFH.DUSTS_APATITE);
            tag(ItemTagsCoFH.DUSTS_BRONZE);
            tag(ItemTagsCoFH.DUSTS_CINNABAR);
            tag(ItemTagsCoFH.DUSTS_CONSTANTAN);
            tag(ItemTagsCoFH.DUSTS_COPPER);
            tag(ItemTagsCoFH.DUSTS_DIAMOND);
            tag(ItemTagsCoFH.DUSTS_ELECTRUM);
            tag(ItemTagsCoFH.DUSTS_EMERALD);
            tag(ItemTagsCoFH.DUSTS_ENDER_PEARL);
            tag(ItemTagsCoFH.DUSTS_ENDERIUM);
            tag(ItemTagsCoFH.DUSTS_GOLD);
            tag(ItemTagsCoFH.DUSTS_INVAR);
            tag(ItemTagsCoFH.DUSTS_IRON);
            tag(ItemTagsCoFH.DUSTS_LAPIS);
            tag(ItemTagsCoFH.DUSTS_LEAD);
            tag(ItemTagsCoFH.DUSTS_LUMIUM);
            tag(ItemTagsCoFH.DUSTS_NETHERITE);
            tag(ItemTagsCoFH.DUSTS_NICKEL);
            tag(ItemTagsCoFH.DUSTS_NITER);
            tag(ItemTagsCoFH.DUSTS_QUARTZ);
            tag(ItemTagsCoFH.DUSTS_RUBY);
            tag(ItemTagsCoFH.DUSTS_SAPPHIRE);
            tag(ItemTagsCoFH.DUSTS_SIGNALUM);
            tag(ItemTagsCoFH.DUSTS_SILVER);
            tag(ItemTagsCoFH.DUSTS_SULFUR);
            tag(ItemTagsCoFH.DUSTS_TIN);
            tag(ItemTagsCoFH.DUSTS_WOOD);

            tag(ItemTagsCoFH.GEARS);

            tag(ItemTagsCoFH.GEARS_BRONZE);
            tag(ItemTagsCoFH.GEARS_CONSTANTAN);
            tag(ItemTagsCoFH.GEARS_COPPER);
            tag(ItemTagsCoFH.GEARS_DIAMOND);
            tag(ItemTagsCoFH.GEARS_ELECTRUM);
            tag(ItemTagsCoFH.GEARS_EMERALD);
            tag(ItemTagsCoFH.GEARS_ENDERIUM);
            tag(ItemTagsCoFH.GEARS_GOLD);
            tag(ItemTagsCoFH.GEARS_INVAR);
            tag(ItemTagsCoFH.GEARS_IRON);
            tag(ItemTagsCoFH.GEARS_LAPIS);
            tag(ItemTagsCoFH.GEARS_LEAD);
            tag(ItemTagsCoFH.GEARS_LUMIUM);
            tag(ItemTagsCoFH.GEARS_NETHERITE);
            tag(ItemTagsCoFH.GEARS_NICKEL);
            tag(ItemTagsCoFH.GEARS_QUARTZ);
            tag(ItemTagsCoFH.GEARS_RUBY);
            tag(ItemTagsCoFH.GEARS_SAPPHIRE);
            tag(ItemTagsCoFH.GEARS_SIGNALUM);
            tag(ItemTagsCoFH.GEARS_SILVER);
            tag(ItemTagsCoFH.GEARS_TIN);

            tag(ItemTagsCoFH.GEMS_APATITE);
            tag(ItemTagsCoFH.GEMS_CINNABAR);
            tag(ItemTagsCoFH.GEMS_NITER);
            tag(ItemTagsCoFH.GEMS_RUBY);
            tag(ItemTagsCoFH.GEMS_SAPPHIRE);
            tag(ItemTagsCoFH.GEMS_SULFUR);

            tag(ItemTagsCoFH.INGOTS_BRONZE);
            tag(ItemTagsCoFH.INGOTS_CONSTANTAN);
            tag(ItemTagsCoFH.INGOTS_COPPER);
            tag(ItemTagsCoFH.INGOTS_ELECTRUM);
            tag(ItemTagsCoFH.INGOTS_ENDERIUM);
            tag(ItemTagsCoFH.INGOTS_INVAR);
            tag(ItemTagsCoFH.INGOTS_LEAD);
            tag(ItemTagsCoFH.INGOTS_LUMIUM);
            tag(ItemTagsCoFH.INGOTS_NICKEL);
            tag(ItemTagsCoFH.INGOTS_SIGNALUM);
            tag(ItemTagsCoFH.INGOTS_SILVER);
            tag(ItemTagsCoFH.INGOTS_TIN);

            copy(BlockTagsCoFH.LOGS_RUBBERWOOD, ItemTagsCoFH.LOGS_RUBBERWOOD);

            tag(ItemTagsCoFH.NUGGETS_BRONZE);
            tag(ItemTagsCoFH.NUGGETS_CONSTANTAN);
            tag(ItemTagsCoFH.NUGGETS_COPPER);
            tag(ItemTagsCoFH.NUGGETS_ELECTRUM);
            tag(ItemTagsCoFH.NUGGETS_ENDERIUM);
            tag(ItemTagsCoFH.NUGGETS_INVAR);
            tag(ItemTagsCoFH.NUGGETS_LEAD);
            tag(ItemTagsCoFH.NUGGETS_LUMIUM);
            tag(ItemTagsCoFH.NUGGETS_NETHERITE);
            tag(ItemTagsCoFH.NUGGETS_NICKEL);
            tag(ItemTagsCoFH.NUGGETS_SIGNALUM);
            tag(ItemTagsCoFH.NUGGETS_SILVER);
            tag(ItemTagsCoFH.NUGGETS_TIN);

            copy(BlockTagsCoFH.ORES_APATITE, ItemTagsCoFH.ORES_APATITE);
            copy(BlockTagsCoFH.ORES_CINNABAR, ItemTagsCoFH.ORES_CINNABAR);
            copy(BlockTagsCoFH.ORES_COPPER, ItemTagsCoFH.ORES_COPPER);
            copy(BlockTagsCoFH.ORES_LEAD, ItemTagsCoFH.ORES_LEAD);
            copy(BlockTagsCoFH.ORES_NICKEL, ItemTagsCoFH.ORES_NICKEL);
            copy(BlockTagsCoFH.ORES_NITER, ItemTagsCoFH.ORES_NITER);
            copy(BlockTagsCoFH.ORES_RUBY, ItemTagsCoFH.ORES_RUBY);
            copy(BlockTagsCoFH.ORES_SAPPHIRE, ItemTagsCoFH.ORES_SAPPHIRE);
            copy(BlockTagsCoFH.ORES_SILVER, ItemTagsCoFH.ORES_SILVER);
            copy(BlockTagsCoFH.ORES_SULFUR, ItemTagsCoFH.ORES_SULFUR);
            copy(BlockTagsCoFH.ORES_TIN, ItemTagsCoFH.ORES_TIN);

            tag(ItemTagsCoFH.PLATES);

            tag(ItemTagsCoFH.PLATES_BRONZE);
            tag(ItemTagsCoFH.PLATES_CONSTANTAN);
            tag(ItemTagsCoFH.PLATES_COPPER);
            tag(ItemTagsCoFH.PLATES_ELECTRUM);
            tag(ItemTagsCoFH.PLATES_ENDERIUM);
            tag(ItemTagsCoFH.PLATES_GOLD);
            tag(ItemTagsCoFH.PLATES_INVAR);
            tag(ItemTagsCoFH.PLATES_IRON);
            tag(ItemTagsCoFH.PLATES_LEAD);
            tag(ItemTagsCoFH.PLATES_LUMIUM);
            tag(ItemTagsCoFH.PLATES_NETHERITE);
            tag(ItemTagsCoFH.PLATES_NICKEL);
            tag(ItemTagsCoFH.PLATES_SIGNALUM);
            tag(ItemTagsCoFH.PLATES_SILVER);
            tag(ItemTagsCoFH.PLATES_TIN);

            tag(ItemTagsCoFH.SEEDS_AMARANTH);
            tag(ItemTagsCoFH.SEEDS_BARLEY);
            tag(ItemTagsCoFH.SEEDS_BELL_PEPPER);
            tag(ItemTagsCoFH.SEEDS_COFFEE);
            tag(ItemTagsCoFH.SEEDS_CORN);
            tag(ItemTagsCoFH.SEEDS_FROST_MELON);
            tag(ItemTagsCoFH.SEEDS_EGGPLANT);
            tag(ItemTagsCoFH.SEEDS_FLAX);
            tag(ItemTagsCoFH.SEEDS_GREEN_BEAN);
            tag(ItemTagsCoFH.SEEDS_HOPS);
            tag(ItemTagsCoFH.SEEDS_ONION);
            tag(ItemTagsCoFH.SEEDS_PEANUT);
            tag(ItemTagsCoFH.SEEDS_RADISH);
            tag(ItemTagsCoFH.SEEDS_RICE);
            tag(ItemTagsCoFH.SEEDS_SADIROOT);
            tag(ItemTagsCoFH.SEEDS_SPINACH);
            tag(ItemTagsCoFH.SEEDS_STRAWBERRY);
            tag(ItemTagsCoFH.SEEDS_TEA);
            tag(ItemTagsCoFH.SEEDS_TOMATO);

            copy(BlockTagsCoFH.STORAGE_BLOCKS_APATITE, ItemTagsCoFH.STORAGE_BLOCKS_APATITE);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_BAMBOO, ItemTagsCoFH.STORAGE_BLOCKS_BAMBOO);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_BITUMEN, ItemTagsCoFH.STORAGE_BLOCKS_BITUMEN);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_BRONZE, ItemTagsCoFH.STORAGE_BLOCKS_BRONZE);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_CHARCOAL, ItemTagsCoFH.STORAGE_BLOCKS_CHARCOAL);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_CINNABAR, ItemTagsCoFH.STORAGE_BLOCKS_CINNABAR);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_COAL_COKE, ItemTagsCoFH.STORAGE_BLOCKS_COAL_COKE);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_CONSTANTAN, ItemTagsCoFH.STORAGE_BLOCKS_CONSTANTAN);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_COPPER, ItemTagsCoFH.STORAGE_BLOCKS_COPPER);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_ELECTRUM, ItemTagsCoFH.STORAGE_BLOCKS_ELECTRUM);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_ENDERIUM, ItemTagsCoFH.STORAGE_BLOCKS_ENDERIUM);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_GUNPOWDER, ItemTagsCoFH.STORAGE_BLOCKS_GUNPOWDER);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_INVAR, ItemTagsCoFH.STORAGE_BLOCKS_INVAR);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_LEAD, ItemTagsCoFH.STORAGE_BLOCKS_LEAD);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_LUMIUM, ItemTagsCoFH.STORAGE_BLOCKS_LUMIUM);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_NICKEL, ItemTagsCoFH.STORAGE_BLOCKS_NICKEL);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_NITER, ItemTagsCoFH.STORAGE_BLOCKS_NITER);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_RUBY, ItemTagsCoFH.STORAGE_BLOCKS_RUBY);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SAPPHIRE, ItemTagsCoFH.STORAGE_BLOCKS_SAPPHIRE);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SIGNALUM, ItemTagsCoFH.STORAGE_BLOCKS_SIGNALUM);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SILVER, ItemTagsCoFH.STORAGE_BLOCKS_SILVER);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SLAG, ItemTagsCoFH.STORAGE_BLOCKS_SLAG);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SUGAR_CANE, ItemTagsCoFH.STORAGE_BLOCKS_SUGAR_CANE);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_SULFUR, ItemTagsCoFH.STORAGE_BLOCKS_SULFUR);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_TAR, ItemTagsCoFH.STORAGE_BLOCKS_TAR);
            copy(BlockTagsCoFH.STORAGE_BLOCKS_TIN, ItemTagsCoFH.STORAGE_BLOCKS_TIN);

            copy(BlockTagsCoFH.PUMPKINS_CARVED, ItemTagsCoFH.PUMPKINS_CARVED);

            tag(ItemTagsCoFH.ARMOR_IRON).add(
                    Items.IRON_BOOTS,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_HELMET,
                    Items.IRON_LEGGINGS
            );
            tag(ItemTagsCoFH.ARMOR_GOLD).add(
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_LEGGINGS
            );
            tag(ItemTagsCoFH.ARMOR_DIAMOND).add(
                    Items.DIAMOND_BOOTS,
                    Items.DIAMOND_CHESTPLATE,
                    Items.DIAMOND_HELMET,
                    Items.DIAMOND_LEGGINGS
            );
            tag(ItemTagsCoFH.ARMOR_NETHERITE).add(
                    Items.NETHERITE_BOOTS,
                    Items.NETHERITE_CHESTPLATE,
                    Items.NETHERITE_HELMET,
                    Items.NETHERITE_LEGGINGS
            );

            tag(ItemTagsCoFH.ARMOR_COPPER);
            tag(ItemTagsCoFH.ARMOR_TIN);
            tag(ItemTagsCoFH.ARMOR_LEAD);
            tag(ItemTagsCoFH.ARMOR_SILVER);
            tag(ItemTagsCoFH.ARMOR_NICKEL);
            tag(ItemTagsCoFH.ARMOR_BRONZE);
            tag(ItemTagsCoFH.ARMOR_ELECTRUM);
            tag(ItemTagsCoFH.ARMOR_INVAR);
            tag(ItemTagsCoFH.ARMOR_CONSTANTAN);

            tag(ItemTagsCoFH.TOOLS_IRON).add(
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_SWORD,
                    Items.SHEARS
            );
            tag(ItemTagsCoFH.TOOLS_GOLD).add(
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_SWORD
            );
            tag(ItemTagsCoFH.TOOLS_DIAMOND).add(
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_HOE,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_SWORD
            );
            tag(ItemTagsCoFH.TOOLS_NETHERITE).add(
                    Items.NETHERITE_AXE,
                    Items.NETHERITE_HOE,
                    Items.NETHERITE_PICKAXE,
                    Items.NETHERITE_SHOVEL,
                    Items.NETHERITE_SWORD
            );

            tag(ItemTagsCoFH.TOOLS_COPPER);
            tag(ItemTagsCoFH.TOOLS_TIN);
            tag(ItemTagsCoFH.TOOLS_LEAD);
            tag(ItemTagsCoFH.TOOLS_SILVER);
            tag(ItemTagsCoFH.TOOLS_NICKEL);
            tag(ItemTagsCoFH.TOOLS_BRONZE);
            tag(ItemTagsCoFH.TOOLS_ELECTRUM);
            tag(ItemTagsCoFH.TOOLS_INVAR);
            tag(ItemTagsCoFH.TOOLS_CONSTANTAN);

            tag(ItemTagsCoFH.TOOLS_WRENCH);

            tag(ItemTagsCoFH.BITUMEN);
            tag(ItemTagsCoFH.COAL_COKE);
            tag(ItemTagsCoFH.SAWDUST);
            tag(ItemTagsCoFH.SLAG);
            tag(ItemTagsCoFH.TAR);

            tag(ItemTagsCoFH.COOKED_FISH).add(
                    Items.COOKED_COD,
                    Items.COOKED_SALMON
            );

            tag(ItemTagsCoFH.COOKED_MEAT).add(
                    Items.COOKED_PORKCHOP,
                    Items.COOKED_BEEF,
                    Items.COOKED_RABBIT,
                    Items.COOKED_CHICKEN,
                    Items.COOKED_MUTTON
            );

            tag(ItemTagsCoFH.RAW_FISH).add(
                    Items.COD,
                    Items.SALMON,
                    Items.TROPICAL_FISH
            );

            tag(ItemTagsCoFH.RAW_MEAT).add(
                    Items.PORKCHOP,
                    Items.BEEF,
                    Items.RABBIT,
                    Items.CHICKEN,
                    Items.MUTTON
            );

            tag(ItemTagsCoFH.LOCKS);
            tag(ItemTagsCoFH.SECURABLE);

            tag(ItemTagsCoFH.MACHINE_DIES);
            tag(ItemTagsCoFH.MACHINE_CASTS);

            copy(BlockTagsCoFH.HARDENED_GLASS, ItemTagsCoFH.HARDENED_GLASS);
            copy(BlockTagsCoFH.ROCKWOOL, ItemTagsCoFH.ROCKWOOL);
        }

    }

    public static class Fluid extends FluidTagsProvider {

        public Fluid(DataGenerator gen, ExistingFileHelper existingFileHelper) {

            super(gen, ID_COFH_CORE, existingFileHelper);
        }

        @Override
        public String getName() {

            return "CoFH Core: Fluid Tags";
        }

        @Override
        protected void addTags() {

            tag(FluidTagsCoFH.HONEY).add(FLUIDS.get(ID_FLUID_HONEY));
            tag(FluidTagsCoFH.POTION).add(FLUIDS.get(ID_FLUID_POTION));
            tag(FluidTagsCoFH.EXPERIENCE).add(FLUIDS.get(ID_FLUID_XP));

            tag(FluidTagsCoFH.REDSTONE);
            tag(FluidTagsCoFH.GLOWSTONE);
            tag(FluidTagsCoFH.ENDER);

            tag(FluidTagsCoFH.LATEX);

            tag(FluidTagsCoFH.CREOSOTE);
            tag(FluidTagsCoFH.CRUDE_OIL);
        }

    }

}
