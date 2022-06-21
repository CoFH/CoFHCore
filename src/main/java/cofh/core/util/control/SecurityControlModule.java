package cofh.core.util.control;

import cofh.core.network.packet.server.SecurityControlPacket;
import cofh.lib.api.control.ISecurable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.SecurityHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.function.BooleanSupplier;

import static cofh.lib.util.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.*;

public class SecurityControlModule implements ISecurable {

    protected ISecurableTile tile;
    protected BooleanSupplier enabled;

    protected GameProfile owner = SecurityHelper.DEFAULT_GAME_PROFILE;
    protected AccessMode access = AccessMode.PUBLIC;

    public SecurityControlModule(ISecurableTile tile) {

        this(tile, TRUE);
    }

    public SecurityControlModule(ISecurableTile tile, BooleanSupplier enabled) {

        this.tile = tile;
        this.enabled = enabled;
    }

    public SecurityControlModule setEnabled(BooleanSupplier enabled) {

        this.enabled = enabled;
        return this;
    }

    // region NETWORK
    public void readFromBuffer(FriendlyByteBuf buffer) {

        access = AccessMode.VALUES[buffer.readByte()];
        owner = SecurityHelper.DEFAULT_GAME_PROFILE;
        setOwner(new GameProfile(buffer.readUUID(), buffer.readUtf(1024)));
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {

        buffer.writeByte(access.ordinal());
        buffer.writeUUID(owner.getId());
        buffer.writeUtf(owner.getName());
    }
    // endregion

    // region NBT
    public SecurityControlModule read(CompoundTag nbt) {

        CompoundTag subTag = nbt.getCompound(TAG_SECURITY);

        if (subTag.contains(TAG_SEC_OWNER_UUID)) {
            String uuid = subTag.getString(TAG_SEC_OWNER_UUID);
            String name = subTag.getString(TAG_SEC_OWNER_NAME);
            owner = new GameProfile(UUID.fromString(uuid), name);
        } else {
            owner = SecurityHelper.DEFAULT_GAME_PROFILE;
        }
        access = isSecurable() ? AccessMode.VALUES[subTag.getByte(TAG_SEC_ACCESS)] : AccessMode.PUBLIC;

        return this;
    }

    public CompoundTag write(CompoundTag nbt) {

        if (isSecurable()) {
            CompoundTag subTag = new CompoundTag();

            subTag.putString(TAG_SEC_OWNER_UUID, owner.getId().toString());
            subTag.putString(TAG_SEC_OWNER_NAME, owner.getName());
            subTag.putByte(TAG_SEC_ACCESS, (byte) access.ordinal());

            nbt.put(TAG_SECURITY, subTag);
        }
        return nbt;
    }
    // endregion

    // region ISecurable
    @Override
    public boolean isSecurable() {

        return enabled.getAsBoolean();
    }

    @Override
    public AccessMode getAccess() {

        return access;
    }

    @Override
    public GameProfile getOwner() {

        return owner;
    }

    @Override
    public void setAccess(AccessMode access) {

        AccessMode curAccess = this.access;
        this.access = access;

        if (Utils.isClientWorld(tile.world())) {
            SecurityControlPacket.sendToServer(tile);
            this.access = curAccess;
        } else {
            tile.onControlUpdate();
        }
    }

    @Override
    public boolean setOwner(GameProfile profile) {

        if (!isSecurable()) {
            return false;
        }
        if (SecurityHelper.isDefaultProfile(owner) && !SecurityHelper.isDefaultProfile(profile)) {
            owner = profile;
            if (Utils.isServerWorld(tile.world())) {
                tile.onControlUpdate();
            }
            return true;
        }
        return false;
    }
    // endregion
}
