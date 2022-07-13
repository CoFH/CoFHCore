package cofh.core.fluid;

import cofh.lib.fluid.FluidCoFH;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.core.CoFHCore.FLUID_TYPES;
import static cofh.core.util.references.CoreIDs.ID_FLUID_HONEY;

public class HoneyFluid extends FluidCoFH {

    private static HoneyFluid INSTANCE;

    public static HoneyFluid create() {

        if (INSTANCE == null) {
            INSTANCE = new HoneyFluid();
        }
        return INSTANCE;
    }

    protected HoneyFluid() {

        super(FLUIDS, ID_FLUID_HONEY);
    }

    @Override
    protected ForgeFlowingFluid.Properties fluidProperties() {

        return new ForgeFlowingFluid.Properties(type(), stillFluid, flowingFluid);
    }

    @Override
    protected Supplier<FluidType> type() {

        return TYPE;
    }

    public static final RegistryObject<FluidType> TYPE = FLUID_TYPES.register(ID_FLUID_HONEY, () -> new FluidType(FluidType.Properties.create()
            .density(1500)
            .viscosity(1000000)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)) {

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {

            consumer.accept(new IClientFluidTypeExtensions() {

                private static final ResourceLocation
                        STILL = new ResourceLocation("cofh_core:block/fluids/honey_still"),
                        FLOW = new ResourceLocation("cofh_core:block/fluids/honey_flow");

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
