package cofh.core.data;

import cofh.lib.data.BlockStateProviderCoFH;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class CoreBlockStateProvider extends BlockStateProviderCoFH {

    public CoreBlockStateProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {

        super(gen, ID_COFH_CORE, existingFileHelper);
    }

    @Override
    public String getName() {

        return "CoFH Core: BlockStates";
    }

    @Override
    protected void registerStatesAndModels() {

        // simpleBlock(BLOCKS.getSup(ID_GLOSSED_MAGMA));
    }

}
