package cofh.lib.init.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidTagsCoFH {

    private FluidTagsCoFH() {

    }

    public static final TagKey<Fluid> EXPERIENCE = commonTag("experience");
    public static final TagKey<Fluid> HONEY = commonTag("honey");
    public static final TagKey<Fluid> POTION = commonTag("potion");

    // region HELPERS
    private static TagKey<Fluid> commonTag(String name) {

        return FluidTags.create(new ResourceLocation("c", name));
    }
    // endregion
}
