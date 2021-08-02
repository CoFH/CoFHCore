package cofh.lib.fluid;

import cofh.lib.tileentity.ITileCallback;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static cofh.lib.util.constants.NBTTags.TAG_TANK;
import static cofh.lib.util.constants.NBTTags.TAG_TANK_INV;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

/**
 * Fluid "inventory" abstraction using CoFH Fluid Storage objects.
 */
public class SimpleTankInv extends SimpleFluidHandler {

    protected String tag;

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
    public SimpleTankInv read(CompoundNBT nbt) {

        for (FluidStorageCoFH tank : tanks) {
            tank.setFluidStack(FluidStack.EMPTY);
        }
        ListNBT list = nbt.getList(tag, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);
            int tank = tag.getByte(TAG_TANK);
            if (tank >= 0 && tank < tanks.size()) {
                tanks.get(tank).read(tag);
            }
        }
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        if (tanks.size() <= 0) {
            return nbt;
        }
        ListNBT list = new ListNBT();
        for (int i = 0; i < tanks.size(); ++i) {
            if (!tanks.get(i).isEmpty()) {
                CompoundNBT tag = new CompoundNBT();
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
}
