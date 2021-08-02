package cofh.core.fluid;

import cofh.lib.fluid.FluidCoFH;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.references.CoreIDs.ID_FLUID_MILK;

public class MilkFluid extends FluidCoFH {

    public static MilkFluid create() {

        return new MilkFluid(ID_FLUID_MILK, "cofh_core:block/fluids/milk_still", "cofh_core:block/fluids/milk_flow");
    }

    protected MilkFluid(String key, String stillTexture, String flowTexture) {

        super(FLUIDS, key, FluidAttributes.builder(new ResourceLocation(stillTexture), new ResourceLocation(flowTexture)).density(1024).viscosity(1024));
    }

}