package cofh.core.init;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.ENTITY_DATA_SERIALIZERS;

public class CoreEntityDataSerializers {

    private CoreEntityDataSerializers() {

    }

    public static void register() {

    }

    public static final RegistryObject<EntityDataSerializer<FluidStack>> FLUID_STACK_DATA_SERIALIZER = ENTITY_DATA_SERIALIZERS.register("fluid_stack_eds",
            () -> new EntityDataSerializer<>() {

                @Override
                public void write(FriendlyByteBuf buf, FluidStack fluid) {

                    buf.writeFluidStack(fluid);
                }

                @Override
                public FluidStack read(FriendlyByteBuf buf) {

                    return buf.readFluidStack();
                }

                @Override
                public FluidStack copy(FluidStack stack) {

                    return stack.copy();
                }
            }
    );

}
