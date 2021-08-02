package cofh.core.util.filter;

import cofh.core.util.helpers.FluidHelper;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterOptions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cofh.lib.util.constants.NBTTags.*;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

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
    public IFilter read(CompoundNBT nbt) {

        CompoundNBT subTag = nbt.getCompound(TAG_FILTER);
        //        int size = subTag.getInt(TAG_TANKS);
        //        if (size > 0) {
        //            fluids = new ArrayList<>(size);
        //            for (int i = 0; i < size; ++i) {
        //                fluids.add(FluidStack.EMPTY);
        //            }
        //        }
        ListNBT list = subTag.getList(TAG_TANK_INV, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tankTag = list.getCompound(i);
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
    public CompoundNBT write(CompoundNBT nbt) {

        CompoundNBT subTag = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (int i = 0; i < fluids.size(); ++i) {
            if (!fluids.get(i).isEmpty()) {
                CompoundNBT tankTag = new CompoundNBT();
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

    // region INamedContainerProvider
    @Override
    public ITextComponent getDisplayName() {

        return new TranslationTextComponent("info.cofh.fluid_filter");
    }
    // endregion
}
