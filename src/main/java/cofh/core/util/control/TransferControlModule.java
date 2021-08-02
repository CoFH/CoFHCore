package cofh.core.util.control;

import cofh.core.network.packet.server.TransferControlPacket;
import cofh.lib.util.Utils;
import cofh.lib.util.control.ITransferControllable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.*;

public class TransferControlModule implements ITransferControllable {

    protected ITransferControllableTile tile;
    protected BooleanSupplier enabled;

    protected boolean enableAutoInput;
    protected boolean enableAutoOutput;

    public TransferControlModule(ITransferControllableTile tile) {

        this(tile, TRUE);
    }

    public TransferControlModule(ITransferControllableTile tile, BooleanSupplier enabled) {

        this.tile = tile;
        this.enabled = enabled;
    }

    public TransferControlModule setEnabled(BooleanSupplier enabled) {

        this.enabled = enabled;
        return this;
    }

    public void disable() {

        enableAutoInput = false;
        enableAutoOutput = false;
    }

    // Only ever call this in a constructor!
    public void initControl(boolean input, boolean output) {

        if (hasTransferIn()) {
            this.enableAutoInput = input;
        }
        if (hasTransferOut()) {
            this.enableAutoOutput = output;
        }
    }

    // region NETWORK
    public void readFromBuffer(PacketBuffer buffer) {

        enableAutoInput = buffer.readBoolean();
        enableAutoOutput = buffer.readBoolean();
    }

    public void writeToBuffer(PacketBuffer buffer) {

        buffer.writeBoolean(enableAutoInput);
        buffer.writeBoolean(enableAutoOutput);
    }
    // endregion

    // region NBT
    public TransferControlModule read(CompoundNBT nbt) {

        CompoundNBT subTag = nbt.getCompound(TAG_XFER);

        if (!subTag.isEmpty()) {
            enableAutoInput = subTag.getBoolean(TAG_XFER_IN);
            enableAutoOutput = subTag.getBoolean(TAG_XFER_OUT);
        }
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        CompoundNBT subTag = new CompoundNBT();

        if (enabled.getAsBoolean()) {
            subTag.putBoolean(TAG_XFER_IN, enableAutoInput);
            subTag.putBoolean(TAG_XFER_OUT, enableAutoOutput);

            nbt.put(TAG_XFER, subTag);
        }
        return nbt;
    }

    public TransferControlModule readSettings(CompoundNBT nbt) {

        if (enabled.getAsBoolean()) {
            return read(nbt);
        }
        return this;
    }

    public CompoundNBT writeSettings(CompoundNBT nbt) {

        return write(nbt);
    }
    // endregion

    // region ITransferControl
    @Override
    public boolean hasTransferIn() {

        return enabled.getAsBoolean();
    }

    @Override
    public boolean hasTransferOut() {

        return enabled.getAsBoolean();
    }

    @Override
    public boolean getTransferIn() {

        return hasTransferIn() && enableAutoInput;
    }

    @Override
    public boolean getTransferOut() {

        return hasTransferOut() && enableAutoOutput;
    }

    @Override
    public void setControl(boolean input, boolean output) {

        boolean curInput = this.enableAutoInput;
        boolean curOutput = this.enableAutoOutput;

        if (hasTransferIn()) {
            this.enableAutoInput = input;
        }
        if (hasTransferOut()) {
            this.enableAutoOutput = output;
        }
        if (Utils.isClientWorld(tile.world())) {
            TransferControlPacket.sendToServer(tile);
            this.enableAutoInput = curInput;
            this.enableAutoOutput = curOutput;
        } else {
            tile.onControlUpdate();
        }
    }
    // endregion
}
