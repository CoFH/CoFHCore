package cofh.lib.tileentity;

import cofh.lib.util.helpers.XpHelper;
import cofh.lib.xp.EmptyXpStorage;
import cofh.lib.xp.XpStorage;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ITileXpHandler {

    default boolean claimXP(PlayerEntity player) {

        if (!getXpStorage().isEmpty()) {
            int xp = getXpStorage().getStored();
            XpHelper.addXPToPlayer(player, xp);
            getXpStorage().clear();
            return true;
        }
        return false;

    }

    default void spawnXpOrbs(World world, int xp, Vector3d pos) {

        if (world == null) {
            return;
        }
        while (xp > 0) {
            int orbAmount = ExperienceOrbEntity.getExperienceValue(xp);
            xp -= orbAmount;
            world.addFreshEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, orbAmount));
        }
    }

    default XpStorage getXpStorage() {

        return EmptyXpStorage.INSTANCE;
    }

}
