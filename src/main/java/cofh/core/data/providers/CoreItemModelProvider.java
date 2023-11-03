package cofh.core.data.providers;

import cofh.lib.data.ItemModelProviderCoFH;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class CoreItemModelProvider extends ItemModelProviderCoFH {

    public CoreItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {

        super(output, ID_COFH_CORE, existingFileHelper);
    }

    @Override
    public String getName() {

        return "CoFH Core: Item Models";
    }

    @Override
    protected void registerModels() {

    }

}
