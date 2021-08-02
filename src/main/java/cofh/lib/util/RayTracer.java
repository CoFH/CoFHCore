package cofh.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class RayTracer {

    public static BlockRayTraceResult retrace(PlayerEntity player) {

        return retrace(player, getBlockReachDistance(player), RayTraceContext.FluidMode.ANY);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, double reach) {

        return retrace(player, reach, RayTraceContext.FluidMode.ANY);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.FluidMode fluidMode) {

        return retrace(player, RayTraceContext.BlockMode.COLLIDER, fluidMode);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, double reach, RayTraceContext.FluidMode fluidMode) {

        return retrace(player, reach, RayTraceContext.BlockMode.COLLIDER, fluidMode);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {

        return player.world.rayTraceBlocks(new RayTraceContext(getStartVec(player), getEndVec(player), blockMode, fluidMode, player));
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, double reach, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {

        return player.world.rayTraceBlocks(new RayTraceContext(getStartVec(player), getEndVec(player, reach), blockMode, fluidMode, player));
    }

    public static Vector3d getStartVec(PlayerEntity player) {

        return getCorrectedHeadVec(player);
    }

    public static Vector3d getEndVec(PlayerEntity player) {

        Vector3d headVec = getCorrectedHeadVec(player);
        Vector3d lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vector3d getEndVec(PlayerEntity player, double reach) {

        Vector3d headVec = getCorrectedHeadVec(player);
        Vector3d lookVec = player.getLook(1.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vector3d getCorrectedHeadVec(PlayerEntity player) {

        return new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
    }

    public static double getBlockReachDistance(PlayerEntity player) {

        return player.world.isRemote ? getBlockReachDistanceClient() : player instanceof ServerPlayerEntity ? getBlockReachDistanceServer((ServerPlayerEntity) player) : 5D;
    }

    private static double getBlockReachDistanceServer(ServerPlayerEntity player) {

        return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
    }

    @OnlyIn(Dist.CLIENT)
    private static double getBlockReachDistanceClient() {

        return Minecraft.getInstance().playerController.getBlockReachDistance();
    }

}
