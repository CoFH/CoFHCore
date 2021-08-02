package cofh.core.fluid;

import cofh.lib.fluid.FluidCoFH;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.references.CoreIDs.ID_FLUID_HONEY;

public class HoneyFluid extends FluidCoFH {

    public static HoneyFluid create() {

        return new HoneyFluid(ID_FLUID_HONEY, "cofh_core:block/fluids/honey_still", "cofh_core:block/fluids/honey_flow");
    }

    protected HoneyFluid(String key, String stillTexture, String flowTexture) {

        super(FLUIDS, key, FluidAttributes.builder(new ResourceLocation(stillTexture), new ResourceLocation(flowTexture))
                .density(1500)
                .viscosity(10000000)
                .sound(SoundEvents.ITEM_BOTTLE_FILL, SoundEvents.ITEM_BOTTLE_EMPTY)
        );
    }

}