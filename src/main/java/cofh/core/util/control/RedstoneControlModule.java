package cofh.core.util.control;

import cofh.core.network.packet.server.RedstoneControlPacket;
import cofh.lib.util.Utils;
import cofh.lib.util.control.IRedstoneControllable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.*;

public class RedstoneControlModule implements IRedstoneControllable {

    protected IRedstoneControllableTile tile;
    protected BooleanSupplier enabled;

    protected int power;
    protected int threshold;
    protected ControlMode mode = ControlMode.DISABLED;

    public RedstoneControlModule(IRedstoneControllableTile tile) {

        this(tile, TRUE);
    }

    public RedstoneControlModule(IRedstoneControllableTile tile, BooleanSupplier enabled) {

        this.tile = tile;
        this.enabled = enabled;
    }

    public RedstoneControlModule setEnabled(BooleanSupplier enabled) {

        this.enabled = enabled;
        return this;
    }

    // region NETWORK
    public void readFromBuffer(PacketBuffer buffer) {

        power = buffer.readByte();
        threshold = buffer.readByte();
        mode = ControlMode.VALUES[buffer.readByte()];
    }

    public void writeToBuffer(PacketBuffer buffer) {

        buffer.writeByte(power);
        buffer.writeByte(threshold);
        buffer.writeByte(mode.ordinal());
    }
    // endregion

    // region NBT
    public RedstoneControlModule read(CompoundNBT nbt) {

        CompoundNBT subTag = nbt.getCompound(TAG_RS_CONTROL);

        if (!subTag.isEmpty()) {
            power = subTag.getByte(TAG_RS_POWER);
            threshold = subTag.getByte(TAG_RS_THRESHOLD);
            mode = !isControllable() ? ControlMode.DISABLED : ControlMode.VALUES[subTag.getByte(TAG_RS_MODE)];
        }
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        if (isControllable()) {
            CompoundNBT subTag = new CompoundNBT();

            subTag.putByte(TAG_RS_POWER, (byte) power);
            subTag.putByte(TAG_RS_THRESHOLD, (byte) threshold);
            subTag.putByte(TAG_RS_MODE, (byte) mode.ordinal());

            nbt.put(TAG_RS_CONTROL, subTag);
        }
        return nbt;
    }

    public RedstoneControlModule readSettings(CompoundNBT nbt) {

        CompoundNBT subTag = nbt.getCompound(TAG_RS_CONTROL);

        if (!subTag.isEmpty() && isControllable()) {
            threshold = subTag.getByte(TAG_RS_THRESHOLD);
            mode = !isControllable() ? ControlMode.DISABLED : ControlMode.VALUES[subTag.getByte(TAG_RS_MODE)];
        }
        return this;
    }

    public CompoundNBT writeSettings(CompoundNBT nbt) {

        if (isControllable()) {
            CompoundNBT subTag = new CompoundNBT();

            subTag.putByte(TAG_RS_THRESHOLD, (byte) threshold);
            subTag.putByte(TAG_RS_MODE, (byte) mode.ordinal());

            nbt.put(TAG_RS_CONTROL, subTag);
        }
        return nbt;
    }
    // endregion

    // region IRedstoneControl
    @Override
    public boolean isControllable() {

        return enabled.getAsBoolean();
    }

    @Override
    public int getPower() {

        return power;
    }

    @Override
    public int getThreshold() {

        return threshold;
    }

    @Override
    public ControlMode getMode() {

        return mode;
    }

    @Override
    public void setPower(int power) {

        this.power = power;
    }

    @Override
    public void setControl(int threshold, ControlMode mode) {

        int curThreshold = this.threshold;
        ControlMode curMode = this.mode;
        this.threshold = threshold;
        this.mode = mode;

        if (Utils.isClientWorld(tile.world())) {
            RedstoneControlPacket.sendToServer(this.tile);
            this.threshold = curThreshold;
            this.mode = curMode;
        } else {
            tile.onControlUpdate();
        }
    }
    // endregion
}
