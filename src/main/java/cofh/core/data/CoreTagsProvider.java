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

            tag(BlockTagsCoFH.PUMPKINS_CARVED).add(Blocks.CARVED_PUMPKIN);
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

            tag(ItemTagsCoFH.TOOLS_IRON).add(
                    Items.IRON_AXE,
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_SWORD,
                    Items.SHEARS
            );

            tag(ItemTagsCoFH.TOOLS_GOLD).add(
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_SWORD
            );

            tag(ItemTagsCoFH.TOOLS_DIAMOND).add(
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_SWORD
            );

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
