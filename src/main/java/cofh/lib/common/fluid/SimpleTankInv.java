package cofh.lib.common.fluid;

import cofh.lib.api.StorageGroup;
import cofh.lib.api.block.entity.ITileCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cofh.lib.util.constants.NBTTags.TAG_TANK;
import static cofh.lib.util.constants.NBTTags.TAG_TANK_INV;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

/**
 * Fluid "inventory" abstraction using CoFH Fluid Storage objects.
 */
public class SimpleTankInv extends SimpleFluidHandler {

    protected String tag;

    protected IFluidHandler allHandler;

    public SimpleTankInv(@Nullable ITileCallback tile) {

        this(tile, TAG_TANK_INV);
    }

    public SimpleTankInv(@Nullable ITileCallback tile, @Nonnull String tag) {

        this(tile, Collections.emptyList(), tag);
    }

    public SimpleTankInv(@Nullable ITileCallback tile, @Nonnull List<FluidStorageCoFH> tanks, @Nonnull String tag) {

        super(tile, tanks);
        this.tag = tag;
    }

    public void addSlot(FluidStorageCoFH tank) {

        if (allHandler != null) {
            return;
        }
        tanks.add(tank);
    }

    public void clear() {

        for (FluidStorageCoFH tank : tanks) {
            tank.setFluidStack(FluidStack.EMPTY);
        }
    }

    public void set(int tank, FluidStack stack) {

        tanks.get(tank).setFluidStack(stack);
    }

    public FluidStack get(int tank) {

        return tanks.get(tank).getFluidStack();
    }

    public FluidStorageCoFH getTank(int tank) {

        return tanks.get(tank);
    }

    // region NBT
    public SimpleTankInv read(CompoundTag nbt) {

        for (FluidStorageCoFH tank : tanks) {
            tank.setFluidStack(FluidStack.EMPTY);
        }
        ListTag list = nbt.getList(tag, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tag = list.getCompound(i);
            int tank = tag.getByte(TAG_TANK);
            if (tank >= 0 && tank < tanks.size()) {
                tanks.get(tank).read(tag);
            }
        }
        return this;
    }

    public CompoundTag write(CompoundTag nbt) {

        if (tanks.size() <= 0) {
            return nbt;
        }
        ListTag list = new ListTag();
        for (int i = 0; i < tanks.size(); ++i) {
            if (!tanks.get(i).isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putByte(TAG_TANK, (byte) i);
                tanks.get(i).write(tag);
                list.add(tag);
            }
        }
        if (!list.isEmpty()) {
            nbt.put(tag, list);
        }
        return nbt;
    }
    // endregion

    public IFluidHandler getHandler(StorageGroup group) {

        if (allHandler == null) {
            ((ArrayList<FluidStorageCoFH>) tanks).trimToSize();
            allHandler = new SimpleFluidHandler(callback, tanks);
        }
        return allHandler;
    }

}
