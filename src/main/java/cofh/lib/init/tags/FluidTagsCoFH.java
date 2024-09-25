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

    public static final TagKey<Fluid> REDSTONE = commonTag("redstone");
    public static final TagKey<Fluid> GLOWSTONE = commonTag("glowstone");
    public static final TagKey<Fluid> ENDER = commonTag("ender");

    public static final TagKey<Fluid> LATEX = commonTag("latex");
    public static final TagKey<Fluid> CREOSOTE = commonTag("creosote");
    public static final TagKey<Fluid> CRUDE_OIL = commonTag("crude_oil");

    // region HELPERS
    private static TagKey<Fluid> commonTag(String name) {

        return FluidTags.create(new ResourceLocation("c", name));
    }
    // endregion
}
