package cofh.core.init;

import cofh.core.common.fluid.ExperienceFluid;
import cofh.core.common.fluid.HoneyFluid;
import cofh.core.common.fluid.PotionFluid;
import cofh.lib.init.tags.FluidTagsCoFH;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

import static cofh.core.util.helpers.FluidHelper.*;
import static cofh.lib.util.Constants.BOTTLE_VOLUME;

public class CoreFluids {

    private CoreFluids() {

    }

    public static void register() {

        EXPERIENCE_FLUID = ExperienceFluid.create().still();
        HONEY_FLUID = HoneyFluid.create().still();
        POTION_FLUID = PotionFluid.create().still();
    }

    public static void setup() {

        BOTTLE_DRAIN_MAP.put(Items.POTION, (stack -> PotionFluid.getPotionFluidFromItem(BOTTLE_VOLUME, stack)));
        BOTTLE_DRAIN_MAP.put(Items.HONEY_BOTTLE, (stack -> new FluidStack(HONEY_FLUID.get(), BOTTLE_VOLUME)));
        BOTTLE_DRAIN_MAP.put(Items.EXPERIENCE_BOTTLE, (stack -> new FluidStack(EXPERIENCE_FLUID.get(), BOTTLE_VOLUME)));

        BOTTLE_FILL_MAP.put(fluid -> fluid.getFluid() == net.minecraft.world.level.material.Fluids.WATER || hasPotionTag(fluid), PotionFluid::getItemFromPotionFluid);
        BOTTLE_FILL_MAP.put(fluid -> fluid.getFluid().is(FluidTagsCoFH.HONEY), fluid -> new ItemStack(Items.HONEY_BOTTLE));
        BOTTLE_FILL_MAP.put(fluid -> fluid.getFluid().is(FluidTagsCoFH.EXPERIENCE), fluid -> new ItemStack(Items.EXPERIENCE_BOTTLE));
    }

    public static Supplier<BaseFlowingFluid> EXPERIENCE_FLUID;
    public static Supplier<BaseFlowingFluid> HONEY_FLUID;
    public static Supplier<BaseFlowingFluid> POTION_FLUID;

}
