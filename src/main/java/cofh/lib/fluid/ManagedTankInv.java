package cofh.lib.fluid;

import cofh.lib.block.entity.ITileCallback;
import cofh.lib.util.StorageGroup;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagedTankInv extends SimpleTankInv {

    protected List<FluidStorageCoFH> inputTanks = new ArrayList<>();
    protected List<FluidStorageCoFH> outputTanks = new ArrayList<>();
    protected List<FluidStorageCoFH> internalTanks = new ArrayList<>();

    protected IFluidHandler inputHandler;
    protected IFluidHandler outputHandler;
    protected IFluidHandler ioHandler;
    protected IFluidHandler accessibleHandler;
    protected IFluidHandler internalHandler;

    public ManagedTankInv(ITileCallback tile) {

        super(tile);
    }

    public ManagedTankInv(ITileCallback tile, String tag) {

        super(tile, tag);
    }

    public void addTank(FluidStorageCoFH tank, StorageGroup group) {

        if (allHandler != null) {
            return;
        }
        tanks.add(tank);
        switch (group) {
            case INPUT:
                inputTanks.add(tank);
                break;
            case OUTPUT:
                outputTanks.add(tank);
                break;
            case INTERNAL:
                internalTanks.add(tank);
                break;
            case ACCESSIBLE:
                inputTanks.add(tank);
                outputTanks.add(tank);
                break;
            default:
        }
    }

    public void initHandlers() {

        ((ArrayList<FluidStorageCoFH>) tanks).trimToSize();
        ((ArrayList<FluidStorageCoFH>) inputTanks).trimToSize();
        ((ArrayList<FluidStorageCoFH>) outputTanks).trimToSize();
        ((ArrayList<FluidStorageCoFH>) internalTanks).trimToSize();

        inputHandler = new ManagedFluidHandler(callback, inputTanks, Collections.emptyList());
        outputHandler = new ManagedFluidHandler(callback, Collections.emptyList(), outputTanks);
        ioHandler = new ManagedFluidHandler(callback, inputTanks, outputTanks).restrict();
        accessibleHandler = new ManagedFluidHandler(callback, inputTanks, outputTanks);
        internalHandler = new SimpleFluidHandler(callback, internalTanks);
        allHandler = new SimpleFluidHandler(callback, tanks);
    }

    public boolean hasInputTanks() {

        return inputTanks.size() > 0;
    }

    public boolean hasOutputTanks() {

        return outputTanks.size() > 0;
    }

    public boolean hasAccessibleTanks() {

        return hasInputTanks() || hasOutputTanks();
    }

    public List<FluidStorageCoFH> getInputTanks() {

        return inputTanks;
    }

    public List<FluidStorageCoFH> getOutputTanks() {

        return outputTanks;
    }

    public List<FluidStorageCoFH> getInternalTanks() {

        return internalTanks;
    }

    public IFluidHandler getHandler(StorageGroup group) {

        if (allHandler == null) {
            initHandlers();
        }
        switch (group) {
            case INPUT:
                return inputHandler;
            case OUTPUT:
                return outputHandler;
            case INPUT_OUTPUT:
                return ioHandler;
            case ACCESSIBLE:
                return accessibleHandler;
            case INTERNAL:
                return internalHandler;
            case ALL:
                return allHandler;
            default:
        }
        return EmptyFluidHandler.INSTANCE;
    }

}
