package cofh.lib.init.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.constants.ModIds.ID_FORGE;

public class DamageTypeTagsCoFH {

    private DamageTypeTagsCoFH() {

    }

    public static final TagKey<DamageType> IS_MAGIC = forgeTag("is_magic");
    public static final TagKey<DamageType> IS_EARTH = forgeTag("is_earth");
    public static final TagKey<DamageType> IS_AIR = forgeTag("is_air");

    // region HELPERS
    private static TagKey<DamageType> cofhTag(String name) {

        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ID_COFH_CORE, name));
    }

    private static TagKey<DamageType> forgeTag(String name) {

        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ID_FORGE, name));
    }
    // endregion
}
