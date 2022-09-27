package cofh.lib.fluid;

import cofh.lib.util.DeferredRegisterCoFH;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public abstract class FluidCoFH {

    protected RegistryObject<ForgeFlowingFluid> stillFluid;
    protected RegistryObject<ForgeFlowingFluid> flowingFluid;

    protected RegistryObject<LiquidBlock> block;
    protected RegistryObject<Item> bucket;

    protected ForgeFlowingFluid.Properties properties;

    protected FluidCoFH() {

    }

    protected FluidCoFH(DeferredRegisterCoFH<Fluid> reg, String key, FluidAttributes.Builder attributes) {

        stillFluid = reg.register(key, () -> new ForgeFlowingFluid.Source(properties));
        flowingFluid = reg.register(flowing(key), () -> new ForgeFlowingFluid.Flowing(properties));

        properties = new ForgeFlowingFluid.Properties(stillFluid, flowingFluid, attributes);
    }

    public Supplier<ForgeFlowingFluid> still() {

        return stillFluid;
    }

    public Supplier<ForgeFlowingFluid> flowing() {

        return flowingFluid;
    }

    public Supplier<LiquidBlock> block() {

        return block;
    }

    public Supplier<Item> bucket() {

        return bucket;
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
