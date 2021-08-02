package cofh.core.data;

import cofh.lib.util.references.FluidTagsCoFH;
import cofh.lib.util.references.ItemTagsCoFH;
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
        protected void registerTags() {

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
        protected void registerTags() {

            getOrCreateBuilder(ItemTagsCoFH.ARMOR_IRON).add(
                    Items.IRON_BOOTS,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_HELMET,
                    Items.IRON_LEGGINGS
            );

            getOrCreateBuilder(ItemTagsCoFH.ARMOR_GOLD).add(
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_LEGGINGS
            );

            getOrCreateBuilder(ItemTagsCoFH.ARMOR_DIAMOND).add(
                    Items.DIAMOND_BOOTS,
                    Items.DIAMOND_CHESTPLATE,
                    Items.DIAMOND_HELMET,
                    Items.DIAMOND_LEGGINGS
            );

            getOrCreateBuilder(ItemTagsCoFH.TOOLS_IRON).add(
                    Items.IRON_AXE,
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_SWORD,
                    Items.SHEARS
            );

            getOrCreateBuilder(ItemTagsCoFH.TOOLS_GOLD).add(
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_SWORD
            );

            getOrCreateBuilder(ItemTagsCoFH.TOOLS_DIAMOND).add(
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_SWORD
            );
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
        protected void registerTags() {

            getOrCreateBuilder(FluidTagsCoFH.HONEY).add(FLUIDS.get(ID_FLUID_HONEY));
            getOrCreateBuilder(FluidTagsCoFH.POTION).add(FLUIDS.get(ID_FLUID_POTION));
            getOrCreateBuilder(FluidTagsCoFH.EXPERIENCE).add(FLUIDS.get(ID_FLUID_XP));

            getOrCreateBuilder(FluidTagsCoFH.REDSTONE);
            getOrCreateBuilder(FluidTagsCoFH.GLOWSTONE);
            getOrCreateBuilder(FluidTagsCoFH.ENDER);

            getOrCreateBuilder(FluidTagsCoFH.LATEX);

            getOrCreateBuilder(FluidTagsCoFH.CREOSOTE);
            getOrCreateBuilder(FluidTagsCoFH.CRUDE_OIL);
        }

    }

}
