package cofh.lib.content.fluid;

import cofh.lib.api.IStorageCallback;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ManagedFluidHandler extends SimpleFluidHandler {

    protected List<FluidStorageCoFH> inputTanks;
    protected List<FluidStorageCoFH> outputTanks;

    protected boolean preventInputDrain = false;

    public ManagedFluidHandler(@Nullable IStorageCallback tile, @Nonnull List<FluidStorageCoFH> inputTanks, @Nonnull List<FluidStorageCoFH> outputTanks) {

        super(tile);

        this.inputTanks = inputTanks;
        this.outputTanks = outputTanks;
        this.tanks.addAll(inputTanks);

        // Do not add a duplicate to the underlying "all tanks" list.
        for (FluidStorageCoFH tank : outputTanks) {
            if (!this.tanks.contains(tank)) {
                this.tanks.add(tank);
            }
        }
    }

    public ManagedFluidHandler restrict() {

        preventInputDrain = true;
        return this;
    }

    // region IFluidHandler
    @Override
    public int fill(FluidStack resource, FluidAction action) {

        int ret;
        for (FluidStorageCoFH tank : inputTanks) {
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
        for (FluidStorageCoFH tank : outputTanks) {
            ret = tank.drain(resource, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        if (!preventInputDrain) {
            for (FluidStorageCoFH tank : inputTanks) {
                ret = tank.drain(resource, action);
                if (action.execute()) {
                    onTankChange(0);
                }
                if (!ret.isEmpty()) {
                    return ret;
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        FluidStack ret;
        for (FluidStorageCoFH tank : outputTanks) {
            ret = tank.drain(maxDrain, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        if (!preventInputDrain) {
            for (FluidStorageCoFH tank : inputTanks) {
                ret = tank.drain(maxDrain, action);
                if (action.execute()) {
                    onTankChange(0);
                }
                if (!ret.isEmpty()) {
                    return ret;
                }
            }
        }
        return FluidStack.EMPTY;
    }
    // endregion
}
