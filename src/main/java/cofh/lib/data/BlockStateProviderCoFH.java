package cofh.lib.data;

import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BlockStateProviderCoFH extends BlockStateProvider {

    public BlockStateProviderCoFH(DataGenerator gen, String modid, ExistingFileHelper existingFileHelper) {

        super(gen, modid, existingFileHelper);
    }

    public static final String CROPS = "crops";
    public static final String GLASS = "glass";
    public static final String MISC = "misc";
    public static final String ORES = "ores";
    public static final String RAILS = "rails";
    public static final String STORAGE = "storage";

    protected void glassBlock(Supplier<? extends Block> block) {

        simpleBlock(block.get(), cubeAll(block, GLASS));
    }

    protected void miscBlock(Supplier<? extends Block> block) {

        simpleBlock(block.get(), cubeAll(block, MISC));
    }

    protected void oreBlock(Supplier<? extends Block> block) {

        simpleBlock(block.get(), cubeAll(block, ORES));
    }

    protected void storageBlock(Supplier<? extends Block> block) {

        simpleBlock(block.get(), cubeAll(block, STORAGE));
    }

    // region HELPERS
    protected String name(Supplier<? extends Block> block) {

        return block.get().getRegistryName().getPath();
    }

    protected ResourceLocation blockTexture(Supplier<? extends Block> block) {

        ResourceLocation base = block.get().getRegistryName();
        return new ResourceLocation(base.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + base.getPath());
    }

    protected ResourceLocation blockTexture(Supplier<? extends Block> block, String subfolder) {

        ResourceLocation base = block.get().getRegistryName();
        return new ResourceLocation(base.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + subfolder + "/" + base.getPath());
    }

    protected ResourceLocation modBlockLoc(String texture) {

        return modLoc("block/" + texture);
    }

    protected ResourceLocation modBlockLoc(String texture, String subfolder) {

        return modLoc("block/" + subfolder + "/" + texture);
    }

    protected ModelFile cubeAll(Supplier<? extends Block> block) {

        return cubeAll(block.get());
    }

    protected ModelFile cubeAll(Supplier<? extends Block> block, String subfolder) {

        return models().cubeAll(name(block), blockTexture(block, subfolder));
    }

    protected void simpleBlock(Supplier<? extends Block> block) {

        simpleBlock(block.get());
    }

    protected void simpleBlock(Supplier<? extends Block> block, ModelFile model) {

        simpleBlock(block.get(), model);
    }

    protected void simpleBlock(Supplier<? extends Block> block, Function<ModelFile, ConfiguredModel[]> expander) {

        simpleBlock(block.get(), expander);
    }

    protected void axisBlock(Supplier<? extends Block> block, String texture) {

        axisBlock((RotatedPillarBlock) block.get(), modBlockLoc(texture));
    }

    protected void axisBlock(Supplier<? extends Block> block, String texture, String subfolder) {

        axisBlock((RotatedPillarBlock) block.get(), modBlockLoc(texture, subfolder));
    }

    protected void stairsBlock(Supplier<? extends StairsBlock> block, String name) {

        stairsBlock(block, name, name);
    }

    protected void stairsBlock(Supplier<? extends StairsBlock> block, String side, String topBottom) {

        stairsBlock(block.get(), modBlockLoc(side), modBlockLoc(topBottom), modBlockLoc(topBottom));
    }

    protected void slabBlock(Supplier<? extends SlabBlock> block, Supplier<? extends Block> doubleslab) {

        slabBlock(block, doubleslab, name(doubleslab));
    }

    protected void slabBlock(Supplier<? extends SlabBlock> block, Supplier<? extends Block> doubleslab, String texture) {

        slabBlock(block, doubleslab, texture, texture);
    }

    protected void slabBlock(Supplier<? extends SlabBlock> block, Supplier<? extends Block> doubleslab, String side, String end) {

        slabBlock(block.get(), doubleslab.get().getRegistryName(), modBlockLoc(side), modBlockLoc(end), modBlockLoc(end));
    }

    protected void doorBlock(Supplier<? extends DoorBlock> block) {

        doorBlock(block.get(), modBlockLoc(name(block) + "_bottom"), modBlockLoc(name(block) + "_top"));
    }

    protected void trapdoorBlock(Supplier<? extends TrapDoorBlock> block) {

        trapdoorBlock(block.get(), blockTexture(block), true);
    }
    // endregion
}
