package cofh.lib.util.helpers;

import cofh.lib.api.control.ISecurable;
import cofh.lib.api.control.ISecurable.AccessMode;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

import static cofh.lib.util.constants.NBTTags.*;
import static cofh.lib.util.helpers.StringHelper.localize;

public class SecurityHelper {

    private SecurityHelper() {

    }

    public static final GameProfile DEFAULT_GAME_PROFILE = new GameProfile(UUID.fromString("1ef1a6f0-87bc-4e78-0a0b-c6824eb787ea"), "[None]");

    private static UUID cachedId;

    public static boolean isDefaultUUID(UUID uuid) {

        return uuid == null || (uuid.version() == 4 && uuid.variant() == 0);
    }

    public static boolean isDefaultProfile(GameProfile profile) {

        return DEFAULT_GAME_PROFILE.equals(profile);
    }

    public static UUID getID(Entity entity) {

        if (entity == null) {
            return DEFAULT_GAME_PROFILE.getId();
        }
        if (entity instanceof Player player) {
            if (player instanceof ServerPlayer) {
                return player.getGameProfile().getId();
            }
            return getClientID(player);
        }
        return entity.getUUID();
    }

    private static UUID getClientID(Player player) {

        if (player != Minecraft.getInstance().player) {
            return player.getGameProfile().getId();
        }
        if (cachedId == null) {
            cachedId = Minecraft.getInstance().player.getGameProfile().getId();
        }
        return cachedId;
    }

    // region TILE HELPERS
    public static boolean hasSecurity(BlockEntity tile) {

        if (tile instanceof ISecurable) {
            return !isDefaultProfile(((ISecurable) tile).getOwner());
        }
        return false;
    }

    public static String getOwnerName(BlockEntity tile) {

        if (hasSecurity(tile)) {
            return ((ISecurable) tile).getOwnerName();
        }
        return DEFAULT_GAME_PROFILE.getName();
    }
    // endregion

    // region ITEM HELPERS
    public static void createSecurityTag(ItemStack stack) {

        stack.getOrCreateTagElement(TAG_SECURITY);
    }

    public static boolean isItemClaimable(ItemStack stack) {

        return hasSecurity(stack) && getOwner(stack) == DEFAULT_GAME_PROFILE;
    }

    public static boolean attemptClaimItem(ItemStack stack, Player player) {

        if (isItemClaimable(stack)) {
            setOwner(stack, player.getGameProfile());
            setAccess(stack, AccessMode.PUBLIC);
            return true;
        }
        return false;
    }

    public static CompoundTag getSecurityTag(ItemStack stack) {

        CompoundTag nbt = stack.getTagElement(TAG_BLOCK_ENTITY);
        if (nbt != null) {
            return nbt.contains(TAG_SECURITY) ? nbt.getCompound(TAG_SECURITY) : null;
        }
        return stack.getTagElement(TAG_SECURITY);
    }

    public static boolean hasSecurity(ItemStack stack) {

        return getSecurityTag(stack) != null;
    }

    public static void setAccess(ItemStack stack, AccessMode access) {

        CompoundTag secureTag = getSecurityTag(stack);
        if (secureTag != null) {
            secureTag.putByte(TAG_SEC_ACCESS, (byte) access.ordinal());
        }
    }

    public static void setOwner(ItemStack stack, GameProfile profile) {

        CompoundTag secureTag = getSecurityTag(stack);
        if (secureTag != null) {
            secureTag.putString(TAG_SEC_OWNER_UUID, profile.getId().toString());
            secureTag.putString(TAG_SEC_OWNER_NAME, profile.getName());
        }
    }

    public static AccessMode getAccess(ItemStack stack) {

        CompoundTag secureTag = getSecurityTag(stack);
        if (secureTag != null && secureTag.contains(TAG_SEC_ACCESS)) {
            return AccessMode.VALUES[secureTag.getByte(TAG_SEC_ACCESS)];
        }
        return AccessMode.PUBLIC;
    }

    public static GameProfile getOwner(ItemStack stack) {

        CompoundTag secureTag = getSecurityTag(stack);
        if (secureTag != null) {
            String uuid = secureTag.getString(TAG_SEC_OWNER_UUID);
            String name = secureTag.getString(TAG_SEC_OWNER_NAME);
            if (!Strings.isNullOrEmpty(uuid)) {
                return new GameProfile(UUID.fromString(uuid), name);
            } else if (!Strings.isNullOrEmpty(name)) {
                return new GameProfile(OldUsersConverter.convertMobOwnerIfNecessary(ServerLifecycleHooks.getCurrentServer(), name), name);
            }
        }
        return DEFAULT_GAME_PROFILE;
    }

    public static String getOwnerName(ItemStack stack) {

        CompoundTag secureTag = getSecurityTag(stack);
        if (secureTag != null) {
            String name = secureTag.getString(TAG_SEC_OWNER_NAME);
            if (!Strings.isNullOrEmpty(name)) {
                return name;
            }
        }
        return localize("info.cofh.another_player");
    }
    // endregion
}
