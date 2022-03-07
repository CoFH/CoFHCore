package cofh.lib.util.references;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static cofh.lib.util.constants.Constants.ID_FORGE;
import static cofh.lib.util.constants.Constants.ID_THERMAL;

public class FluidTagsCoFH {

    public static final TagKey<Fluid> EXPERIENCE = forgeTag("experience");
    public static final TagKey<Fluid> HONEY = forgeTag("honey");
    public static final TagKey<Fluid> POTION = forgeTag("potion");

    public static final TagKey<Fluid> REDSTONE = forgeTag("redstone");
    public static final TagKey<Fluid> GLOWSTONE = forgeTag("glowstone");
    public static final TagKey<Fluid> ENDER = forgeTag("ender");

    public static final TagKey<Fluid> LATEX = forgeTag("latex");

    public static final TagKey<Fluid> CREOSOTE = forgeTag("creosote");
    public static final TagKey<Fluid> CRUDE_OIL = forgeTag("crude_oil");

    // region HELPERS
    private static TagKey<Fluid> thermalTag(String name) {

        return FluidTags.create(new ResourceLocation(ID_THERMAL, name));
    }

    private static TagKey<Fluid> forgeTag(String name) {

        return FluidTags.create(new ResourceLocation(ID_FORGE, name));
    }
    // endregion
}
