package cofh.lib.content.fluid;

import cofh.lib.util.DeferredRegisterCoFH;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Not actually a Fluid but more of a fluid cunstruction assistant.
 */
public abstract class FluidCoFH {

    protected RegistryObject<ForgeFlowingFluid> stillFluid;
    protected RegistryObject<ForgeFlowingFluid> flowingFluid;

    protected RegistryObject<LiquidBlock> block;
    protected RegistryObject<Item> bucket;

    protected ForgeFlowingFluid.Properties properties;

    protected FluidCoFH() {

    }

    protected FluidCoFH(DeferredRegisterCoFH<Fluid> reg, String key) {

        stillFluid = reg.register(key, () -> new ForgeFlowingFluid.Source(fluidProperties()));
        flowingFluid = reg.register(flowing(key), () -> new ForgeFlowingFluid.Flowing(fluidProperties()));
    }

    protected ForgeFlowingFluid.Properties fluidProperties() {

        return new ForgeFlowingFluid.Properties(type(), stillFluid, flowingFluid);
    }

    protected abstract Supplier<FluidType> type();

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
