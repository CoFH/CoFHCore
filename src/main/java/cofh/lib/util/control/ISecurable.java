package cofh.lib.util.control;

import cofh.lib.util.SocialUtils;
import cofh.lib.util.helpers.SecurityHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;

import java.util.UUID;

import static cofh.lib.util.helpers.SecurityHelper.getID;
import static cofh.lib.util.helpers.SecurityHelper.isDefaultUUID;

public interface ISecurable {

    AccessMode getAccess();

    GameProfile getOwner();

    void setAccess(AccessMode access);

    boolean setOwner(GameProfile profile);

    default String getOwnerName() {

        return getOwner().getName();
    }

    default boolean canAccess(Entity entity) {

        return getAccess().matches(getOwner(), entity);
    }

    /**
     * This returns whether or not security functionality is enabled at all.
     */
    default boolean isSecurable() {

        return true;
    }

    default boolean hasSecurity() {

        return !SecurityHelper.isDefaultProfile(getOwner());
    }

    // region ACCESS MODE
    enum AccessMode {
        PUBLIC, PRIVATE, FRIENDS, TEAM;

        public static final AccessMode[] VALUES = values();

        public boolean matches(GameProfile owner, Entity entity) {

            UUID ownerID = owner.getId();
            if (isDefaultUUID(ownerID)) {
                return true;
            }
            UUID otherID = getID(entity);
            switch (this) {
                case PRIVATE:
                    return ownerID.equals(otherID);
                case FRIENDS:
                    return ownerID.equals(otherID) || entity instanceof ServerPlayerEntity && SocialUtils.isFriendOrSelf(owner, (ServerPlayerEntity) entity);
                case TEAM:
                    if (ownerID.equals(otherID)) {
                        return true;
                    }
                    Team team = entity.getTeam();
                    if (team == null) {
                        return false;
                    }
                    return team.getMembershipCollection().contains(owner.getName());
                default:
                    return true;
            }
        }

        public boolean isPublic() {

            return this == PUBLIC;
        }

        public boolean isPrivate() {

            return this == PRIVATE;
        }

        public boolean isTeamOnly() {

            return this == TEAM;
        }

        public boolean isFriendsOnly() {

            return this == FRIENDS;
        }
    }
    // endregion
}
