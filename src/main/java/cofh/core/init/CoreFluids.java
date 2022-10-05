package cofh.core.init;

import cofh.core.fluid.ExperienceFluid;
import cofh.core.fluid.HoneyFluid;
import cofh.core.fluid.PotionFluid;
import cofh.lib.tags.FluidTagsCoFH;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

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

    public static Supplier<ForgeFlowingFluid> EXPERIENCE_FLUID;
    public static Supplier<ForgeFlowingFluid> HONEY_FLUID;
    public static Supplier<ForgeFlowingFluid> POTION_FLUID;

}
