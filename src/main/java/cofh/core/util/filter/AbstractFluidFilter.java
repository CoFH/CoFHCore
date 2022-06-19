package cofh.core.util.filter;

import cofh.core.util.helpers.FluidHelper;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterOptions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cofh.lib.util.constants.NBTTags.*;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

public abstract class AbstractFluidFilter implements IFilter, IFilterOptions {

    public static final int SIZE = 12;

    protected List<FluidStack> fluids;
    protected Predicate<FluidStack> rules;

    protected boolean allowList = false;
    protected boolean checkNBT = false;

    public AbstractFluidFilter(int size) {

        fluids = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            fluids.add(FluidStack.EMPTY);
        }
    }

    public int size() {

        return fluids.size();
    }

    public List<FluidStack> getFluids() {

        return fluids;
    }

    public void setFluids(List<FluidStack> fluids) {

        this.fluids = fluids;
    }

    @Override
    public Predicate<FluidStack> getFluidRules() {

        if (rules == null) {
            Set<Fluid> fluidSet = new ObjectOpenHashSet<>();
            for (FluidStack fluid : fluids) {
                fluidSet.add(fluid.getFluid());
            }
            rules = stack -> {
                if (stack.isEmpty()) {
                    return false;
                }
                if (allowList != fluidSet.contains(stack.getFluid())) {
                    return false;
                }
                if (checkNBT) {
                    for (FluidStack fluid : fluids) {
                        if (FluidHelper.fluidsEqual(stack, fluid)) {
                            return allowList;
                        }
                    }
                }
                return true;
            };
        }
        return rules;
    }

    @Override
    public IFilter read(CompoundTag nbt) {

        CompoundTag subTag = nbt.getCompound(TAG_FILTER);
        //        int size = subTag.getInt(TAG_TANKS);
        //        if (size > 0) {
        //            fluids = new ArrayList<>(size);
        //            for (int i = 0; i < size; ++i) {
        //                fluids.add(FluidStack.EMPTY);
        //            }
        //        }
        ListTag list = subTag.getList(TAG_TANK_INV, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tankTag = list.getCompound(i);
            int tank = tankTag.getByte(TAG_TANK);
            if (tank >= 0 && tank < fluids.size()) {
                fluids.set(tank, FluidStack.loadFluidStackFromNBT(tankTag));
            }
        }
        allowList = subTag.getBoolean(TAG_FILTER_OPT_LIST);
        checkNBT = subTag.getBoolean(TAG_FILTER_OPT_NBT);
        return this;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        CompoundTag subTag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < fluids.size(); ++i) {
            if (!fluids.get(i).isEmpty()) {
                CompoundTag tankTag = new CompoundTag();
                tankTag.putByte(TAG_TANK, (byte) i);
                fluids.get(i).writeToNBT(tankTag);
                list.add(tankTag);
            }
        }
        subTag.put(TAG_TANK_INV, list);

        subTag.putBoolean(TAG_FILTER_OPT_LIST, allowList);
        subTag.putBoolean(TAG_FILTER_OPT_NBT, checkNBT);

        nbt.put(TAG_FILTER, subTag);
        return nbt;
    }

    // region IFilterOptions
    @Override
    public boolean getAllowList() {

        return allowList;
    }

    @Override
    public boolean setAllowList(boolean allowList) {

        this.allowList = allowList;
        return true;
    }

    @Override
    public boolean getCheckNBT() {

        return checkNBT;
    }

    @Override
    public boolean setCheckNBT(boolean checkNBT) {

        this.checkNBT = checkNBT;
        return true;
    }
    // endregion

    // region MenuProvider
    @Override
    public Component getDisplayName() {

        return Component.translatable("info.cofh.fluid_filter");
    }
    // endregion
}
