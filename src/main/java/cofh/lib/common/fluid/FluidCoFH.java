package cofh.lib.common.fluid;

import cofh.lib.util.DeferredRegisterCoFH;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.Vector3f;

import java.util.function.Supplier;

/**
 * Not actually a Fluid but more of a fluid construction assistant.
 */
public abstract class FluidCoFH {

    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");
    public static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

    protected DeferredHolder<Fluid, BaseFlowingFluid> stillFluid;
    protected DeferredHolder<Fluid, BaseFlowingFluid> flowingFluid;

    protected DeferredHolder<Block, LiquidBlock> block;
    protected DeferredHolder<Item, Item> bucket;

    protected BaseFlowingFluid.Properties properties;

    protected Vector3f particleColor = new Vector3f(1.0F, 1.0F, 1.0F);

    protected FluidCoFH() {

    }

    protected FluidCoFH(DeferredRegisterCoFH<Fluid> reg, String key) {

        stillFluid = reg.register(key, () -> new BaseFlowingFluid.Source(fluidProperties()));
        flowingFluid = reg.register(flowing(key), () -> new BaseFlowingFluid.Flowing(fluidProperties()));
    }

    protected BaseFlowingFluid.Properties fluidProperties() {

        return new BaseFlowingFluid.Properties(type(), stillFluid, flowingFluid);
    }

    protected abstract Supplier<FluidType> type();

    public Supplier<BaseFlowingFluid> still() {

        return stillFluid;
    }

    public Supplier<BaseFlowingFluid> flowing() {

        return flowingFluid;
    }

    public Supplier<LiquidBlock> block() {

        return block;
    }

    public Supplier<Item> bucket() {

        return bucket;
    }

    // region HELPERS
    public static String fluid(String fluid) {

        return fluid + "_fluid";
    }

    public static String flowing(String fluid) {

        return fluid + "_flowing";
    }

    public static String bucket(String fluid) {

        return fluid + "_bucket";
    }
    // endregion
}
