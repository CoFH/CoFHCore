package cofh.lib.fluid;

import cofh.lib.util.DeferredRegisterCoFH;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

public abstract class FluidCoFH {

    protected RegistryObject<FlowingFluid> stillFluid;
    protected RegistryObject<FlowingFluid> flowingFluid;

    protected RegistryObject<FlowingFluidBlock> block;
    protected RegistryObject<Item> bucket;

    protected ForgeFlowingFluid.Properties properties;

    protected FluidCoFH() {

    }

    protected FluidCoFH(DeferredRegisterCoFH<Fluid> reg, String key, FluidAttributes.Builder attributes) {

        stillFluid = reg.register(key, () -> new ForgeFlowingFluid.Source(properties));
        flowingFluid = reg.register(flowing(key), () -> new ForgeFlowingFluid.Flowing(properties));

        properties = new ForgeFlowingFluid.Properties(stillFluid, flowingFluid, attributes);
    }

    // region HELPERS
    public static String flowing(String fluid) {

        return fluid + "_flowing";
    }

    public static String bucket(String fluid) {

        return fluid + "_bucket";
    }
    // endregion
}
