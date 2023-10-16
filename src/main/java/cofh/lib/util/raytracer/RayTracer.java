package cofh.lib.util.raytracer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RayTracer {

    public static BlockHitResult retrace(Player player) {

        return retrace(player, getBlockReachDistance(player), ClipContext.Fluid.ANY);
    }

    public static BlockHitResult retrace(Player player, double reach) {

        return retrace(player, reach, ClipContext.Fluid.ANY);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Fluid fluidMode) {

        return retrace(player, ClipContext.Block.COLLIDER, fluidMode);
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Fluid fluidMode) {

        return retrace(player, reach, ClipContext.Block.COLLIDER, fluidMode);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {

        return player.level.clip(new ClipContext(getStartVec(player), getEndVec(player), blockMode, fluidMode, player));
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {

        return player.level.clip(new ClipContext(getStartVec(player), getEndVec(player, reach), blockMode, fluidMode, player));
    }

    public static Vec3 getStartVec(Player player) {

        return getCorrectedHeadVec(player);
    }

    public static Vec3 getEndVec(Player player) {

        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getEndVec(Player player, double reach) {

        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getCorrectedHeadVec(Player player) {

        return new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
    }

    public static double getBlockReachDistance(Player player) {

        return player.getBlockReach();
    }

}
