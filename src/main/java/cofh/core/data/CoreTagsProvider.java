package cofh.core.data;

import cofh.lib.util.references.CoFHTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
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

            tag(CoFHTags.Blocks.LOGS_RUBBERWOOD);

            tag(CoFHTags.Blocks.ORES_APATITE);
            tag(CoFHTags.Blocks.ORES_CINNABAR);
            tag(CoFHTags.Blocks.ORES_LEAD);
            tag(CoFHTags.Blocks.ORES_NICKEL);
            tag(CoFHTags.Blocks.ORES_NITER);
            tag(CoFHTags.Blocks.ORES_RUBY);
            tag(CoFHTags.Blocks.ORES_SAPPHIRE);
            tag(CoFHTags.Blocks.ORES_SILVER);
            tag(CoFHTags.Blocks.ORES_SULFUR);
            tag(CoFHTags.Blocks.ORES_TIN);

            tag(CoFHTags.Blocks.STORAGE_BLOCKS_APATITE);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_BAMBOO);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_BITUMEN);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_BRONZE);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_CHARCOAL);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_CINNABAR);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_COAL_COKE);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_CONSTANTAN);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_ELECTRUM);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_ENDERIUM);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_GUNPOWDER);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_INVAR);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_LEAD);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_LUMIUM);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_NICKEL);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_NITER);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_LEAD);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_NICKEL);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_SILVER);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_TIN);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_RUBY);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SAPPHIRE);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SIGNALUM);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SILVER);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SLAG);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SUGAR_CANE);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_SULFUR);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_TAR);
            tag(CoFHTags.Blocks.STORAGE_BLOCKS_TIN);

            tag(CoFHTags.Blocks.PUMPKINS_CARVED).add(net.minecraft.world.level.block.Blocks.CARVED_PUMPKIN);

            tag(CoFHTags.Blocks.HARDENED_GLASS);
            tag(CoFHTags.Blocks.ROCKWOOL);
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

            tag(CoFHTags.Items.COINS);

            tag(CoFHTags.Items.COINS_BRONZE);
            tag(CoFHTags.Items.COINS_CONSTANTAN);
            tag(CoFHTags.Items.COINS_COPPER);
            tag(CoFHTags.Items.COINS_ELECTRUM);
            tag(CoFHTags.Items.COINS_ENDERIUM);
            tag(CoFHTags.Items.COINS_GOLD);
            tag(CoFHTags.Items.COINS_INVAR);
            tag(CoFHTags.Items.COINS_IRON);
            tag(CoFHTags.Items.COINS_LEAD);
            tag(CoFHTags.Items.COINS_LUMIUM);
            tag(CoFHTags.Items.COINS_NETHERITE);
            tag(CoFHTags.Items.COINS_NICKEL);
            tag(CoFHTags.Items.COINS_SIGNALUM);
            tag(CoFHTags.Items.COINS_SILVER);
            tag(CoFHTags.Items.COINS_TIN);

            tag(CoFHTags.Items.CROPS_AMARANTH);
            tag(CoFHTags.Items.CROPS_BARLEY);
            tag(CoFHTags.Items.CROPS_BELL_PEPPER);
            tag(CoFHTags.Items.CROPS_COFFEE);
            tag(CoFHTags.Items.CROPS_CORN);
            tag(CoFHTags.Items.CROPS_EGGPLANT);
            tag(CoFHTags.Items.CROPS_FLAX);
            tag(CoFHTags.Items.CROPS_GREEN_BEAN);
            tag(CoFHTags.Items.CROPS_HOPS);
            tag(CoFHTags.Items.CROPS_ONION);
            tag(CoFHTags.Items.CROPS_PEANUT);
            tag(CoFHTags.Items.CROPS_RADISH);
            tag(CoFHTags.Items.CROPS_RICE);
            tag(CoFHTags.Items.CROPS_SADIROOT);
            tag(CoFHTags.Items.CROPS_SPINACH);
            tag(CoFHTags.Items.CROPS_STRAWBERRY);
            tag(CoFHTags.Items.CROPS_TEA);
            tag(CoFHTags.Items.CROPS_TOMATO);

            tag(CoFHTags.Items.DUSTS_APATITE);
            tag(CoFHTags.Items.DUSTS_BRONZE);
            tag(CoFHTags.Items.DUSTS_CINNABAR);
            tag(CoFHTags.Items.DUSTS_CONSTANTAN);
            tag(CoFHTags.Items.DUSTS_COPPER);
            tag(CoFHTags.Items.DUSTS_DIAMOND);
            tag(CoFHTags.Items.DUSTS_ELECTRUM);
            tag(CoFHTags.Items.DUSTS_EMERALD);
            tag(CoFHTags.Items.DUSTS_ENDER_PEARL);
            tag(CoFHTags.Items.DUSTS_ENDERIUM);
            tag(CoFHTags.Items.DUSTS_GOLD);
            tag(CoFHTags.Items.DUSTS_INVAR);
            tag(CoFHTags.Items.DUSTS_IRON);
            tag(CoFHTags.Items.DUSTS_LAPIS);
            tag(CoFHTags.Items.DUSTS_LEAD);
            tag(CoFHTags.Items.DUSTS_LUMIUM);
            tag(CoFHTags.Items.DUSTS_NETHERITE);
            tag(CoFHTags.Items.DUSTS_NICKEL);
            tag(CoFHTags.Items.DUSTS_NITER);
            tag(CoFHTags.Items.DUSTS_QUARTZ);
            tag(CoFHTags.Items.DUSTS_RUBY);
            tag(CoFHTags.Items.DUSTS_SAPPHIRE);
            tag(CoFHTags.Items.DUSTS_SIGNALUM);
            tag(CoFHTags.Items.DUSTS_SILVER);
            tag(CoFHTags.Items.DUSTS_SULFUR);
            tag(CoFHTags.Items.DUSTS_TIN);
            tag(CoFHTags.Items.DUSTS_WOOD);

            tag(CoFHTags.Items.GEARS);

            tag(CoFHTags.Items.GEARS_BRONZE);
            tag(CoFHTags.Items.GEARS_CONSTANTAN);
            tag(CoFHTags.Items.GEARS_COPPER);
            tag(CoFHTags.Items.GEARS_DIAMOND);
            tag(CoFHTags.Items.GEARS_ELECTRUM);
            tag(CoFHTags.Items.GEARS_EMERALD);
            tag(CoFHTags.Items.GEARS_ENDERIUM);
            tag(CoFHTags.Items.GEARS_GOLD);
            tag(CoFHTags.Items.GEARS_INVAR);
            tag(CoFHTags.Items.GEARS_IRON);
            tag(CoFHTags.Items.GEARS_LAPIS);
            tag(CoFHTags.Items.GEARS_LEAD);
            tag(CoFHTags.Items.GEARS_LUMIUM);
            tag(CoFHTags.Items.GEARS_NETHERITE);
            tag(CoFHTags.Items.GEARS_NICKEL);
            tag(CoFHTags.Items.GEARS_QUARTZ);
            tag(CoFHTags.Items.GEARS_RUBY);
            tag(CoFHTags.Items.GEARS_SAPPHIRE);
            tag(CoFHTags.Items.GEARS_SIGNALUM);
            tag(CoFHTags.Items.GEARS_SILVER);
            tag(CoFHTags.Items.GEARS_TIN);

            tag(CoFHTags.Items.GEMS_APATITE);
            tag(CoFHTags.Items.GEMS_CINNABAR);
            tag(CoFHTags.Items.GEMS_NITER);
            tag(CoFHTags.Items.GEMS_RUBY);
            tag(CoFHTags.Items.GEMS_SAPPHIRE);
            tag(CoFHTags.Items.GEMS_SULFUR);

            tag(CoFHTags.Items.INGOTS_BRONZE);
            tag(CoFHTags.Items.INGOTS_CONSTANTAN);
            tag(CoFHTags.Items.INGOTS_ELECTRUM);
            tag(CoFHTags.Items.INGOTS_ENDERIUM);
            tag(CoFHTags.Items.INGOTS_INVAR);
            tag(CoFHTags.Items.INGOTS_LEAD);
            tag(CoFHTags.Items.INGOTS_LUMIUM);
            tag(CoFHTags.Items.INGOTS_NICKEL);
            tag(CoFHTags.Items.INGOTS_SIGNALUM);
            tag(CoFHTags.Items.INGOTS_SILVER);
            tag(CoFHTags.Items.INGOTS_TIN);

            copy(CoFHTags.Blocks.LOGS_RUBBERWOOD, CoFHTags.Items.LOGS_RUBBERWOOD);

            tag(CoFHTags.Items.NUGGETS_BRONZE);
            tag(CoFHTags.Items.NUGGETS_CONSTANTAN);
            tag(CoFHTags.Items.NUGGETS_COPPER);
            tag(CoFHTags.Items.NUGGETS_ELECTRUM);
            tag(CoFHTags.Items.NUGGETS_ENDERIUM);
            tag(CoFHTags.Items.NUGGETS_INVAR);
            tag(CoFHTags.Items.NUGGETS_LEAD);
            tag(CoFHTags.Items.NUGGETS_LUMIUM);
            tag(CoFHTags.Items.NUGGETS_NETHERITE);
            tag(CoFHTags.Items.NUGGETS_NICKEL);
            tag(CoFHTags.Items.NUGGETS_SIGNALUM);
            tag(CoFHTags.Items.NUGGETS_SILVER);
            tag(CoFHTags.Items.NUGGETS_TIN);

            copy(CoFHTags.Blocks.ORES_APATITE, CoFHTags.Items.ORES_APATITE);
            copy(CoFHTags.Blocks.ORES_CINNABAR, CoFHTags.Items.ORES_CINNABAR);
            copy(CoFHTags.Blocks.ORES_LEAD, CoFHTags.Items.ORES_LEAD);
            copy(CoFHTags.Blocks.ORES_NICKEL, CoFHTags.Items.ORES_NICKEL);
            copy(CoFHTags.Blocks.ORES_NITER, CoFHTags.Items.ORES_NITER);
            copy(CoFHTags.Blocks.ORES_RUBY, CoFHTags.Items.ORES_RUBY);
            copy(CoFHTags.Blocks.ORES_SAPPHIRE, CoFHTags.Items.ORES_SAPPHIRE);
            copy(CoFHTags.Blocks.ORES_SILVER, CoFHTags.Items.ORES_SILVER);
            copy(CoFHTags.Blocks.ORES_SULFUR, CoFHTags.Items.ORES_SULFUR);
            copy(CoFHTags.Blocks.ORES_TIN, CoFHTags.Items.ORES_TIN);

            tag(CoFHTags.Items.PLATES);

            tag(CoFHTags.Items.PLATES_BRONZE);
            tag(CoFHTags.Items.PLATES_CONSTANTAN);
            tag(CoFHTags.Items.PLATES_COPPER);
            tag(CoFHTags.Items.PLATES_ELECTRUM);
            tag(CoFHTags.Items.PLATES_ENDERIUM);
            tag(CoFHTags.Items.PLATES_GOLD);
            tag(CoFHTags.Items.PLATES_INVAR);
            tag(CoFHTags.Items.PLATES_IRON);
            tag(CoFHTags.Items.PLATES_LEAD);
            tag(CoFHTags.Items.PLATES_LUMIUM);
            tag(CoFHTags.Items.PLATES_NETHERITE);
            tag(CoFHTags.Items.PLATES_NICKEL);
            tag(CoFHTags.Items.PLATES_SIGNALUM);
            tag(CoFHTags.Items.PLATES_SILVER);
            tag(CoFHTags.Items.PLATES_TIN);

            tag(CoFHTags.Items.SEEDS_AMARANTH);
            tag(CoFHTags.Items.SEEDS_BARLEY);
            tag(CoFHTags.Items.SEEDS_BELL_PEPPER);
            tag(CoFHTags.Items.SEEDS_COFFEE);
            tag(CoFHTags.Items.SEEDS_CORN);
            tag(CoFHTags.Items.SEEDS_FROST_MELON);
            tag(CoFHTags.Items.SEEDS_EGGPLANT);
            tag(CoFHTags.Items.SEEDS_FLAX);
            tag(CoFHTags.Items.SEEDS_GREEN_BEAN);
            tag(CoFHTags.Items.SEEDS_HOPS);
            tag(CoFHTags.Items.SEEDS_ONION);
            tag(CoFHTags.Items.SEEDS_PEANUT);
            tag(CoFHTags.Items.SEEDS_RADISH);
            tag(CoFHTags.Items.SEEDS_RICE);
            tag(CoFHTags.Items.SEEDS_SADIROOT);
            tag(CoFHTags.Items.SEEDS_SPINACH);
            tag(CoFHTags.Items.SEEDS_STRAWBERRY);
            tag(CoFHTags.Items.SEEDS_TEA);
            tag(CoFHTags.Items.SEEDS_TOMATO);

            copy(CoFHTags.Blocks.STORAGE_BLOCKS_APATITE, CoFHTags.Items.STORAGE_BLOCKS_APATITE);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_BAMBOO, CoFHTags.Items.STORAGE_BLOCKS_BAMBOO);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_BITUMEN, CoFHTags.Items.STORAGE_BLOCKS_BITUMEN);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_BRONZE, CoFHTags.Items.STORAGE_BLOCKS_BRONZE);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_CHARCOAL, CoFHTags.Items.STORAGE_BLOCKS_CHARCOAL);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_CINNABAR, CoFHTags.Items.STORAGE_BLOCKS_CINNABAR);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_COAL_COKE, CoFHTags.Items.STORAGE_BLOCKS_COAL_COKE);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_CONSTANTAN, CoFHTags.Items.STORAGE_BLOCKS_CONSTANTAN);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_ELECTRUM, CoFHTags.Items.STORAGE_BLOCKS_ELECTRUM);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_ENDERIUM, CoFHTags.Items.STORAGE_BLOCKS_ENDERIUM);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_GUNPOWDER, CoFHTags.Items.STORAGE_BLOCKS_GUNPOWDER);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_INVAR, CoFHTags.Items.STORAGE_BLOCKS_INVAR);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_LEAD, CoFHTags.Items.STORAGE_BLOCKS_LEAD);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_LUMIUM, CoFHTags.Items.STORAGE_BLOCKS_LUMIUM);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_NICKEL, CoFHTags.Items.STORAGE_BLOCKS_NICKEL);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_NITER, CoFHTags.Items.STORAGE_BLOCKS_NITER);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_LEAD, CoFHTags.Items.STORAGE_BLOCKS_RAW_LEAD);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_NICKEL, CoFHTags.Items.STORAGE_BLOCKS_RAW_NICKEL);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_SILVER, CoFHTags.Items.STORAGE_BLOCKS_RAW_SILVER);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_RAW_TIN, CoFHTags.Items.STORAGE_BLOCKS_RAW_TIN);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_RUBY, CoFHTags.Items.STORAGE_BLOCKS_RUBY);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SAPPHIRE, CoFHTags.Items.STORAGE_BLOCKS_SAPPHIRE);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SIGNALUM, CoFHTags.Items.STORAGE_BLOCKS_SIGNALUM);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SILVER, CoFHTags.Items.STORAGE_BLOCKS_SILVER);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SLAG, CoFHTags.Items.STORAGE_BLOCKS_SLAG);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SUGAR_CANE, CoFHTags.Items.STORAGE_BLOCKS_SUGAR_CANE);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_SULFUR, CoFHTags.Items.STORAGE_BLOCKS_SULFUR);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_TAR, CoFHTags.Items.STORAGE_BLOCKS_TAR);
            copy(CoFHTags.Blocks.STORAGE_BLOCKS_TIN, CoFHTags.Items.STORAGE_BLOCKS_TIN);

            copy(CoFHTags.Blocks.PUMPKINS_CARVED, CoFHTags.Items.PUMPKINS_CARVED);

            tag(CoFHTags.Items.ARMOR_IRON).add(
                    Items.IRON_BOOTS,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_HELMET,
                    Items.IRON_LEGGINGS
            );
            tag(CoFHTags.Items.ARMOR_GOLD).add(
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_LEGGINGS
            );
            tag(CoFHTags.Items.ARMOR_DIAMOND).add(
                    Items.DIAMOND_BOOTS,
                    Items.DIAMOND_CHESTPLATE,
                    Items.DIAMOND_HELMET,
                    Items.DIAMOND_LEGGINGS
            );
            tag(CoFHTags.Items.ARMOR_NETHERITE).add(
                    Items.NETHERITE_BOOTS,
                    Items.NETHERITE_CHESTPLATE,
                    Items.NETHERITE_HELMET,
                    Items.NETHERITE_LEGGINGS
            );

            tag(CoFHTags.Items.ARMOR_COPPER);
            tag(CoFHTags.Items.ARMOR_TIN);
            tag(CoFHTags.Items.ARMOR_LEAD);
            tag(CoFHTags.Items.ARMOR_SILVER);
            tag(CoFHTags.Items.ARMOR_NICKEL);
            tag(CoFHTags.Items.ARMOR_BRONZE);
            tag(CoFHTags.Items.ARMOR_ELECTRUM);
            tag(CoFHTags.Items.ARMOR_INVAR);
            tag(CoFHTags.Items.ARMOR_CONSTANTAN);

            tag(CoFHTags.Items.TOOLS_IRON).add(
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_SWORD,
                    Items.SHEARS
            );
            tag(CoFHTags.Items.TOOLS_GOLD).add(
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_SWORD
            );
            tag(CoFHTags.Items.TOOLS_DIAMOND).add(
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_HOE,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_SWORD
            );
            tag(CoFHTags.Items.TOOLS_NETHERITE).add(
                    Items.NETHERITE_AXE,
                    Items.NETHERITE_HOE,
                    Items.NETHERITE_PICKAXE,
                    Items.NETHERITE_SHOVEL,
                    Items.NETHERITE_SWORD
            );

            tag(CoFHTags.Items.TOOLS_COPPER);
            tag(CoFHTags.Items.TOOLS_TIN);
            tag(CoFHTags.Items.TOOLS_LEAD);
            tag(CoFHTags.Items.TOOLS_SILVER);
            tag(CoFHTags.Items.TOOLS_NICKEL);
            tag(CoFHTags.Items.TOOLS_BRONZE);
            tag(CoFHTags.Items.TOOLS_ELECTRUM);
            tag(CoFHTags.Items.TOOLS_INVAR);
            tag(CoFHTags.Items.TOOLS_CONSTANTAN);

            tag(CoFHTags.Items.TOOLS_WRENCH);

            tag(CoFHTags.Items.BITUMEN);
            tag(CoFHTags.Items.COAL_COKE);
            tag(CoFHTags.Items.SAWDUST);
            tag(CoFHTags.Items.SLAG);
            tag(CoFHTags.Items.TAR);

            tag(CoFHTags.Items.COOKED_FISH).add(
                    Items.COOKED_COD,
                    Items.COOKED_SALMON
            );

            tag(CoFHTags.Items.COOKED_MEAT).add(
                    Items.COOKED_PORKCHOP,
                    Items.COOKED_BEEF,
                    Items.COOKED_RABBIT,
                    Items.COOKED_CHICKEN,
                    Items.COOKED_MUTTON
            );

            tag(CoFHTags.Items.RAW_FISH).add(
                    Items.COD,
                    Items.SALMON,
                    Items.TROPICAL_FISH
            );

            tag(CoFHTags.Items.RAW_MEAT).add(
                    Items.PORKCHOP,
                    Items.BEEF,
                    Items.RABBIT,
                    Items.CHICKEN,
                    Items.MUTTON
            );

            tag(CoFHTags.Items.LOCKS);
            tag(CoFHTags.Items.SECURABLE);

            tag(CoFHTags.Items.MACHINE_DIES);
            tag(CoFHTags.Items.MACHINE_CASTS);

            copy(CoFHTags.Blocks.HARDENED_GLASS, CoFHTags.Items.HARDENED_GLASS);
            copy(CoFHTags.Blocks.ROCKWOOL, CoFHTags.Items.ROCKWOOL);
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

            tag(CoFHTags.Fluids.HONEY).add(FLUIDS.get(ID_FLUID_HONEY));
            tag(CoFHTags.Fluids.POTION).add(FLUIDS.get(ID_FLUID_POTION));
            tag(CoFHTags.Fluids.EXPERIENCE).add(FLUIDS.get(ID_FLUID_XP));

            tag(CoFHTags.Fluids.REDSTONE);
            tag(CoFHTags.Fluids.GLOWSTONE);
            tag(CoFHTags.Fluids.ENDER);

            tag(CoFHTags.Fluids.LATEX);

            tag(CoFHTags.Fluids.CREOSOTE);
            tag(CoFHTags.Fluids.CRUDE_OIL);
        }

    }

}
