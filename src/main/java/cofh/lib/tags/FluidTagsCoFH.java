package cofh.lib.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static cofh.lib.util.Constants.ID_FORGE;

public class FluidTagsCoFH {

    private FluidTagsCoFH() {

    }

    public static final TagKey<Fluid> EXPERIENCE = forgeTag("experience");
    public static final TagKey<Fluid> HONEY = forgeTag("honey");
    public static final TagKey<Fluid> POTION = forgeTag("potion");

    // region HELPERS
    private static TagKey<Fluid> forgeTag(String name) {

        return FluidTags.create(new ResourceLocation(ID_FORGE, name));
    }
    // endregion
}
