package cofh.core.fluid;

import cofh.lib.fluid.FluidCoFH;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.references.CoreIDs.ID_FLUID_STEAM;

public class SteamFluid extends FluidCoFH {

    public static SteamFluid create() {

        return new SteamFluid(ID_FLUID_STEAM, "cofh_core:block/fluids/steam_still", "cofh_core:block/fluids/steam_flow");
    }

    protected SteamFluid(String key, String stillTexture, String flowTexture) {

        super(FLUIDS, key, FluidAttributes.builder(new ResourceLocation(stillTexture), new ResourceLocation(flowTexture))
                .density(-60)
                .temperature(750)
                .viscosity(200)
                .gaseous()
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
        );
    }

}