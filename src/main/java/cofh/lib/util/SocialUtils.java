package cofh.lib.util;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import static cofh.lib.util.constants.NBTTags.TAG_NAME;
import static cofh.lib.util.constants.NBTTags.TAG_UUID;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public class SocialUtils {

    private SocialUtils() {

    }

    private static final String TAG_FRIENDS = "cofh:friends";

    private static FriendData friends(ServerPlayerEntity player) {

        return player.getServerWorld().getSavedData().getOrCreate(() -> new FriendData(TAG_FRIENDS), TAG_FRIENDS);
    }

    // region FRIEND PASSTHROUGH
    public synchronized static boolean addFriend(ServerPlayerEntity player, GameProfile friend) {

        return friends(player).addFriend(player, friend);
    }

    public synchronized static boolean removeFriend(ServerPlayerEntity player, GameProfile friend) {

        return friends(player).removeFriend(player, friend);
    }

    public synchronized static boolean clearFriendList(ServerPlayerEntity player) {

        return friends(player).clearFriendList(player);
    }

    public synchronized static boolean clearAllFriendLists(ServerPlayerEntity player) {

        return friends(player).clearAllFriendLists(player);
    }

    public static boolean isFriendOrSelf(GameProfile owner, ServerPlayerEntity player) {

        return friends(player).isFriendOrSelf(owner, player);
    }
    // endregion

    // region FRIEND DATA
    private static class FriendData extends WorldSavedData {

        private final Map<String, Set<GameProfile>> friendLists = new TreeMap<>();

        FriendData(String name) {

            super(name);
        }

        boolean addFriend(PlayerEntity player, GameProfile friend) {

            if (player == null || friend == null) {
                return false;
            }
            String playerUUID = player.getGameProfile().getId().toString();
            Set<GameProfile> set = friendLists.get(playerUUID);
            if (set == null) {
                set = new ObjectOpenHashSet<>();
            }
            set.add(friend);
            friendLists.put(playerUUID, set);
            this.markDirty();
            return true;
        }

        boolean removeFriend(PlayerEntity player, GameProfile friend) {

            if (player == null || friend == null) {
                return false;
            }
            String playerUUID = player.getGameProfile().getId().toString();
            Set<GameProfile> set = friendLists.get(playerUUID);
            this.markDirty();
            return set != null && set.remove(friend);
        }

        public boolean clearFriendList(PlayerEntity player) {

            if (player == null) {
                return false;
            }
            friendLists.remove(player.getGameProfile().getId().toString());
            this.markDirty();
            return true;
        }

        boolean clearAllFriendLists(PlayerEntity player) {

            if (!player.hasPermissionLevel(4)) {
                return false;
            }
            friendLists.clear();
            this.markDirty();
            return true;
        }

        boolean isFriendOrSelf(GameProfile owner, PlayerEntity player) {

            if (owner == null || player == null) {
                return false;
            }
            String friendName = player.getGameProfile().getName();
            if (friendName.equals(owner.getName())) {
                return true;
            }
            String playerUUID = owner.getId().toString();
            Set<GameProfile> set = friendLists.get(playerUUID);
            return set != null && set.contains(player.getGameProfile());
        }

        @Override
        public void read(CompoundNBT nbt) {

            for (String player : nbt.keySet()) {
                ListNBT list = nbt.getList(player, TAG_COMPOUND);
                Set<GameProfile> friendList = new ObjectOpenHashSet<>();
                for (int i = 0; i < list.size(); ++i) {
                    CompoundNBT subTag = list.getCompound(i);
                    friendList.add(new GameProfile(UUID.fromString(subTag.getString(TAG_UUID)), subTag.getString(TAG_NAME)));
                }
                friendLists.put(player, friendList);
            }
        }

        @Override
        public CompoundNBT write(CompoundNBT nbt) {

            for (Map.Entry<String, Set<GameProfile>> friendList : friendLists.entrySet()) {
                ListNBT list = new ListNBT();
                for (GameProfile friend : friendList.getValue()) {
                    CompoundNBT subTag = new CompoundNBT();
                    subTag.putString(TAG_UUID, friend.getId().toString());
                    subTag.putString(TAG_NAME, friend.getName());
                    list.add(subTag);
                }
                nbt.put(friendList.getKey(), list);
            }
            return nbt;
        }

    }
    // endregion
}
