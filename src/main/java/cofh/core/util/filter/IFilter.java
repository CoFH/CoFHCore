package cofh.core.util.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public interface IFilter extends INBTSerializable<CompoundTag>, MenuProvider {

    Predicate<ItemStack> ALWAYS_ALLOW_ITEM = (stack) -> true;
    Predicate<FluidStack> ALWAYS_ALLOW_FLUID = (stack) -> true;

    default Predicate<ItemStack> getItemRules() {

        return ALWAYS_ALLOW_ITEM;
    }

    default Predicate<FluidStack> getFluidRules() {

        return ALWAYS_ALLOW_FLUID;
    }

    default boolean valid(ItemStack item) {

        return getItemRules().test(item);
    }

    default boolean valid(FluidStack fluid) {

        return getFluidRules().test(fluid);
    }

    IFilter read(CompoundTag nbt);

    CompoundTag write(CompoundTag nbt);

    @Override
    default CompoundTag serializeNBT() {

        return write(new CompoundTag());
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {

        read(nbt);
    }

}
