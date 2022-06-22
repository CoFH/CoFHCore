package cofh.core.init;

import cofh.core.content.fluid.ExperienceFluid;
import cofh.core.content.fluid.HoneyFluid;
import cofh.core.content.fluid.PotionFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class CoreFluids {

    private CoreFluids() {

    }

    public static void register() {

    }

    public static final Supplier<Fluid> EXPERIENCE_FLUID = () -> ExperienceFluid.create().still().get();
    public static final Supplier<Fluid> HONEY_FLUID = () -> HoneyFluid.create().still().get();
    public static final Supplier<Fluid> POTION_FLUID = () -> PotionFluid.create().still().get();

}
