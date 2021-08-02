package cofh.core.util.control;

import cofh.core.network.packet.server.SideConfigPacket;
import cofh.lib.util.Utils;
import cofh.lib.util.control.IReconfigurable;
import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.TAG_SIDES;
import static cofh.lib.util.control.IReconfigurable.SideConfig.SIDE_ACCESSIBLE;
import static cofh.lib.util.control.IReconfigurable.SideConfig.SIDE_NONE;

public class ReconfigControlModule implements IReconfigurable {

    protected IReconfigurableTile tile;
    protected BooleanSupplier enabled;

    protected Direction facing = Direction.NORTH;
    protected SideConfig[] sides = {SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};

    public ReconfigControlModule(IReconfigurableTile tile) {

        this(tile, TRUE);
    }

    public ReconfigControlModule(IReconfigurableTile tile, BooleanSupplier enabled) {

        this.tile = tile;
        this.enabled = enabled;
    }

    public ReconfigControlModule setEnabled(BooleanSupplier enabled) {

        this.enabled = enabled;
        return this;
    }

    public SideConfig[] getSideConfig() {

        return sides;
    }

    public byte[] getRawSideConfig() {

        byte[] raw = new byte[6];
        for (int i = 0; i < 6; ++i) {
            raw[i] = (byte) sides[i].ordinal();
        }
        return raw;
    }

    public void initSideConfig(Direction side, SideConfig config) {

        if (side == null || config == null) {
            return;
        }
        this.sides[side.ordinal()] = config;
    }

    public void initSideConfig(SideConfig[] sides) {

        if (sides == null || sides.length != 6) {
            return;
        }
        this.sides = sides;
    }

    public void setFacing(Direction facing) {

        this.facing = facing;
    }

    public void setSideConfig(SideConfig[] sides) {

        this.sides = sides;
        tile.onControlUpdate();
    }

    // region NETWORK
    public void readFromBuffer(PacketBuffer buffer) {

        facing = Direction.byIndex(buffer.readByte());
        for (int i = 0; i < 6; ++i) {
            sides[i] = SideConfig.VALUES[buffer.readByte()];
        }
    }

    public void writeToBuffer(PacketBuffer buffer) {

        buffer.writeByte(facing.getIndex());
        for (int i = 0; i < 6; ++i) {
            buffer.writeByte(sides[i].ordinal());
        }
    }
    // endregion

    // region NBT
    public ReconfigControlModule read(CompoundNBT nbt) {

        byte[] bSides = nbt.getByteArray(TAG_SIDES);

        if (bSides.length == 6) {
            sides[BlockHelper.below(facing).getIndex()] = SideConfig.VALUES[bSides[0]];
            sides[BlockHelper.above(facing).getIndex()] = SideConfig.VALUES[bSides[1]];

            sides[facing.getIndex()] = SideConfig.VALUES[bSides[2]];
            sides[BlockHelper.opposite(facing).getIndex()] = SideConfig.VALUES[bSides[3]];

            sides[BlockHelper.right(facing).getIndex()] = SideConfig.VALUES[bSides[4]];
            sides[BlockHelper.left(facing).getIndex()] = SideConfig.VALUES[bSides[5]];
        }
        return this;
    }

    // Sides are stored to NBT in D-U-N-S-W-E order - North assumed to be front!
    // They are converted to the appropriate rotation using the facing value.
    public CompoundNBT write(CompoundNBT nbt) {

        if (isReconfigurable()) {
            byte[] bSides = new byte[6];

            bSides[0] = (byte) sides[BlockHelper.below(facing).getIndex()].ordinal();
            bSides[1] = (byte) sides[BlockHelper.above(facing).getIndex()].ordinal();

            bSides[2] = (byte) sides[facing.getIndex()].ordinal();
            bSides[3] = (byte) sides[BlockHelper.opposite(facing).getIndex()].ordinal();

            bSides[4] = (byte) sides[BlockHelper.right(facing).getIndex()].ordinal();
            bSides[5] = (byte) sides[BlockHelper.left(facing).getIndex()].ordinal();

            nbt.putByteArray(TAG_SIDES, bSides);
        }
        return nbt;
    }

    public ReconfigControlModule readSettings(CompoundNBT nbt) {

        if (isReconfigurable()) {
            return read(nbt);
        }
        return this;
    }

    public CompoundNBT writeSettings(CompoundNBT nbt) {

        return write(nbt);
    }
    // endregion

    public void disable() {

        this.sides = new SideConfig[]{SIDE_ACCESSIBLE, SIDE_ACCESSIBLE, SIDE_ACCESSIBLE, SIDE_ACCESSIBLE, SIDE_ACCESSIBLE, SIDE_ACCESSIBLE};
        this.sides[facing.ordinal()] = SIDE_NONE;
        tile.onControlUpdate();
    }

    public SideConfig getSideConfig(int side) {

        if (side > 5) {
            return SIDE_ACCESSIBLE;
        }
        return sides[side];
    }

    // region IReconfigurable
    @Override
    public Direction getFacing() {

        return facing;
    }

    @Override
    public boolean isReconfigurable() {

        return enabled.getAsBoolean();
    }

    @Override
    public SideConfig getSideConfig(Direction side) {

        if (side == null || !isReconfigurable()) {
            return SIDE_ACCESSIBLE;
        }
        return sides[side.ordinal()];
    }

    @Override
    public boolean prevSideConfig(Direction side) {

        if (!isReconfigurable() || side == null) {
            return false;
        }
        sides[side.ordinal()] = sides[side.ordinal()].prev();

        if (Utils.isClientWorld(tile.world())) {
            SideConfigPacket.sendToServer(tile);
        } else {
            tile.onControlUpdate();
        }
        return true;
    }

    @Override
    public boolean nextSideConfig(Direction side) {

        if (!isReconfigurable() || side == null) {
            return false;
        }
        sides[side.ordinal()] = sides[side.ordinal()].next();

        if (Utils.isClientWorld(tile.world())) {
            SideConfigPacket.sendToServer(tile);
        } else {
            tile.onControlUpdate();
        }
        return true;
    }

    @Override
    public boolean setSideConfig(Direction side, SideConfig config) {

        if (!isReconfigurable() || side == null || config == null) {
            return false;
        }
        sides[side.ordinal()] = config;

        if (Utils.isClientWorld(tile.world())) {
            SideConfigPacket.sendToServer(tile);
        } else {
            tile.onControlUpdate();
        }
        return true;
    }

    @Override
    public boolean clearAllSides() {

        sides = new SideConfig[]{SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};

        if (Utils.isClientWorld(tile.world())) {
            SideConfigPacket.sendToServer(tile);
        } else {
            tile.onControlUpdate();
        }
        return true;
    }

    @Override
    public boolean hasInputSide() {

        if (!isReconfigurable()) {
            return false;
        }
        for (SideConfig side : sides) {
            if (side.isInput()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasOutputSide() {

        if (!isReconfigurable()) {
            return false;
        }
        for (SideConfig side : sides) {
            if (side.isOutput()) {
                return true;
            }
        }
        return false;
    }
    // endregion
}
