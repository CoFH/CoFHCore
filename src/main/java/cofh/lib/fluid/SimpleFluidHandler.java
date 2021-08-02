package cofh.lib.fluid;

import cofh.lib.tileentity.ITileCallback;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic Fluid Handler implementation using CoFH Fluid Storage objects.
 */
public class SimpleFluidHandler implements IFluidHandler {

    @Nullable
    protected ITileCallback tile;
    protected List<FluidStorageCoFH> tanks;

    public SimpleFluidHandler() {

        this(null);
    }

    public SimpleFluidHandler(@Nullable ITileCallback tile) {

        this.tile = tile;
        this.tanks = new ArrayList<>();
    }

    public SimpleFluidHandler(@Nullable ITileCallback tile, @Nonnull List<FluidStorageCoFH> tanks) {

        this.tile = tile;
        this.tanks = new ArrayList<>(tanks);
    }

    public boolean hasTanks() {

        return tanks.size() > 0;
    }

    public boolean isEmpty() {

        for (FluidStorageCoFH tank : tanks) {
            if (!tank.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void onTankChange(int tank) {

        if (tile == null) {
            return;
        }
        tile.onTankChange(tank);
    }

    // region IFluidHandler
    @Override
    public int getTanks() {

        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        if (tank < 0 || tank >= getTanks()) {
            return FluidStack.EMPTY;
        }
        return tanks.get(tank).getFluidStack();
    }

    @Nonnull
    @Override
    public int fill(FluidStack resource, FluidAction action) {

        int ret;
        for (FluidStorageCoFH tank : tanks) {
            ret = tank.fill(resource, action);
            if (ret > 0) {
                return ret;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        FluidStack ret;
        for (FluidStorageCoFH tank : tanks) {
            ret = tank.drain(resource, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        FluidStack ret;
        for (FluidStorageCoFH tank : tanks) {
            ret = tank.drain(maxDrain, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {

        if (tank < 0 || tank > getTanks()) {
            return 0;
        }
        return tanks.get(tank).getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        if (tank < 0 || tank > getTanks()) {
            return false;
        }
        return tanks.get(tank).isFluidValid(stack);
    }
    // endregion
}
