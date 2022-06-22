package cofh.core.content.fluid;

import cofh.lib.content.fluid.FluidCoFH;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.core.CoFHCore.FLUID_TYPES;
import static cofh.core.util.references.CoreIDs.ID_FLUID_EXPERIENCE;

public class ExperienceFluid extends FluidCoFH {

    public static ExperienceFluid create() {

        return new ExperienceFluid();
    }

    protected ExperienceFluid() {

        super(FLUIDS, ID_FLUID_EXPERIENCE);
    }

    @Override
    protected ForgeFlowingFluid.Properties fluidProperties() {

        return new ForgeFlowingFluid.Properties(type(), stillFluid, flowingFluid);
    }

    @Override
    protected Supplier<FluidType> type() {

        return TYPE;
    }

    public static final RegistryObject<FluidType> TYPE = FLUID_TYPES.register(ID_FLUID_EXPERIENCE, () -> new FluidType(FluidType.Properties.create()
            .lightLevel(10)
            .density(250)
            .viscosity(500)
            .rarity(Rarity.UNCOMMON)) {

        @Override
        public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer) {

            consumer.accept(new IFluidTypeRenderProperties() {

                private static final ResourceLocation
                        STILL = new ResourceLocation("cofh_core:block/fluids/experience_still"),
                        FLOW = new ResourceLocation("cofh_core:block/fluids/experience_flow");

                @Override
                public ResourceLocation getStillTexture() {

                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {

                    return FLOW;
                }

            });
        }
    });

}
