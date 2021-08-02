package cofh.core.fluid;

import cofh.lib.fluid.FluidCoFH;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.references.CoreIDs.ID_FLUID_XP;

public class XPFluid extends FluidCoFH {

    public static XPFluid create() {

        return new XPFluid(ID_FLUID_XP, "cofh_core:block/fluids/experience_still", "cofh_core:block/fluids/experience_flow");
    }

    protected XPFluid(String key, String stillTexture, String flowTexture) {

        super(FLUIDS, key, FluidAttributes.builder(new ResourceLocation(stillTexture), new ResourceLocation(flowTexture))
                .luminosity(10)
                .density(250)
                .viscosity(500)
                .rarity(Rarity.UNCOMMON)
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
        );
    }

}
