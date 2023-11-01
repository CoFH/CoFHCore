//package cofh.core.data;
//
//import cofh.lib.tags.BlockTagsCoFH;
//import cofh.lib.tags.FluidTagsCoFH;
//import cofh.lib.tags.ItemTagsCoFH;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.data.PackOutput;
//import net.minecraft.data.tags.FluidTagsProvider;
//import net.minecraft.data.tags.ItemTagsProvider;
//import net.minecraft.data.tags.TagsProvider;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraftforge.common.data.BlockTagsProvider;
//import net.minecraftforge.common.data.ExistingFileHelper;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.concurrent.CompletableFuture;
//
//import static cofh.core.CoFHCore.FLUIDS;
//import static cofh.core.util.references.CoreIDs.*;
//import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
//
//public class CoreTagsProvider {
//
//    public static class Block extends BlockTagsProvider {
//
//        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
//
//            super(output, lookupProvider, ID_COFH_CORE, existingFileHelper);
//        }
//
//        @Override
//        public String getName() {
//
//            return "CoFH Core: Block Tags";
//        }
//
//        @Override
//        protected void addTags(HolderLookup.Provider pProvider) {
//
//            tag(BlockTagsCoFH.PUMPKINS_CARVED).add(Blocks.CARVED_PUMPKIN);
//        }
//
//    }
//
//    public static class Item extends ItemTagsProvider {
//
//        public Item(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagsProvider.TagLookup<net.minecraft.world.level.block.Block>> pBlockTags, ExistingFileHelper existingFileHelper) {
//
//            super(pOutput, pLookupProvider, pBlockTags, ID_COFH_CORE, existingFileHelper);
//        }
//
//        @Override
//        public String getName() {
//
//            return "CoFH Core: Item Tags";
//        }
//
//        @Override
//        protected void addTags(HolderLookup.Provider pProvider) {
//
//            copy(BlockTagsCoFH.PUMPKINS_CARVED, ItemTagsCoFH.PUMPKINS_CARVED);
//
//            tag(ItemTagsCoFH.ARMOR_IRON).add(
//                    Items.IRON_BOOTS,
//                    Items.IRON_CHESTPLATE,
//                    Items.IRON_HELMET,
//                    Items.IRON_LEGGINGS
//            );
//            tag(ItemTagsCoFH.ARMOR_GOLD).add(
//                    Items.GOLDEN_BOOTS,
//                    Items.GOLDEN_CHESTPLATE,
//                    Items.GOLDEN_HELMET,
//                    Items.GOLDEN_LEGGINGS
//            );
//            tag(ItemTagsCoFH.ARMOR_DIAMOND).add(
//                    Items.DIAMOND_BOOTS,
//                    Items.DIAMOND_CHESTPLATE,
//                    Items.DIAMOND_HELMET,
//                    Items.DIAMOND_LEGGINGS
//            );
//            tag(ItemTagsCoFH.ARMOR_NETHERITE).add(
//                    Items.NETHERITE_BOOTS,
//                    Items.NETHERITE_CHESTPLATE,
//                    Items.NETHERITE_HELMET,
//                    Items.NETHERITE_LEGGINGS
//            );
//            tag(ItemTagsCoFH.TOOLS_IRON).add(
//                    Items.IRON_AXE,
//                    Items.IRON_HOE,
//                    Items.IRON_PICKAXE,
//                    Items.IRON_SHOVEL,
//                    Items.IRON_SWORD,
//                    Items.SHEARS
//            );
//            tag(ItemTagsCoFH.TOOLS_GOLD).add(
//                    Items.GOLDEN_AXE,
//                    Items.GOLDEN_HOE,
//                    Items.GOLDEN_PICKAXE,
//                    Items.GOLDEN_SHOVEL,
//                    Items.GOLDEN_SWORD
//            );
//            tag(ItemTagsCoFH.TOOLS_DIAMOND).add(
//                    Items.DIAMOND_AXE,
//                    Items.DIAMOND_HOE,
//                    Items.DIAMOND_PICKAXE,
//                    Items.DIAMOND_SHOVEL,
//                    Items.DIAMOND_SWORD
//            );
//            tag(ItemTagsCoFH.TOOLS_NETHERITE).add(
//                    Items.NETHERITE_AXE,
//                    Items.NETHERITE_HOE,
//                    Items.NETHERITE_PICKAXE,
//                    Items.NETHERITE_SHOVEL,
//                    Items.NETHERITE_SWORD
//            );
//            tag(ItemTagsCoFH.COOKED_FISH).add(
//                    Items.COOKED_COD,
//                    Items.COOKED_SALMON
//            );
//            tag(ItemTagsCoFH.COOKED_MEAT).add(
//                    Items.COOKED_PORKCHOP,
//                    Items.COOKED_BEEF,
//                    Items.COOKED_RABBIT,
//                    Items.COOKED_CHICKEN,
//                    Items.COOKED_MUTTON
//            );
//            tag(ItemTagsCoFH.RAW_FISH).add(
//                    Items.COD,
//                    Items.SALMON,
//                    Items.TROPICAL_FISH
//            );
//            tag(ItemTagsCoFH.RAW_MEAT).add(
//                    Items.PORKCHOP,
//                    Items.BEEF,
//                    Items.RABBIT,
//                    Items.CHICKEN,
//                    Items.MUTTON
//            );
//        }
//
//    }
//
//    public static class Fluid extends FluidTagsProvider {
//
//        public Fluid(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, ExistingFileHelper existingFileHelper) {
//
//            super(pOutput, pProvider, ID_COFH_CORE, existingFileHelper);
//        }
//
//        @Override
//        public String getName() {
//
//            return "CoFH Core: Fluid Tags";
//        }
//
//        @Override
//        protected void addTags(HolderLookup.Provider pProvider) {
//
//            tag(FluidTagsCoFH.EXPERIENCE).add(FLUIDS.get(ID_FLUID_EXPERIENCE));
//            tag(FluidTagsCoFH.HONEY).add(FLUIDS.get(ID_FLUID_HONEY));
//            tag(FluidTagsCoFH.POTION).add(FLUIDS.get(ID_FLUID_POTION));
//        }
//
//    }
//
//}
