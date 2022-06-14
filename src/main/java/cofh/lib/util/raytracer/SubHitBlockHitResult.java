package cofh.lib.util.raytracer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * <p>
 * Copied from CCL with permission :)
 * <p>
 */
public class SubHitBlockHitResult extends BlockHitResult implements Comparable<SubHitBlockHitResult> {

    /**
     * The square distance from the start of the raytrace.
     */
    public final double dist;
    public final Object hitInfo;
    public final int subHit;

    public SubHitBlockHitResult(Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {

        this(false, hitVec, faceIn, posIn, isInside, data, dist);
    }

    protected SubHitBlockHitResult(boolean isMissIn, Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {

        super(isMissIn, hitVec, faceIn, posIn, isInside);
        if (data instanceof Integer d) {
            subHit = d;
        } else {
            subHit = -1;
        }
        hitInfo = data;
        this.dist = dist;
    }

    @Override
    public SubHitBlockHitResult withDirection(Direction newFace) {

        return new SubHitBlockHitResult(getType() == Type.MISS, getLocation(), getDirection(), getBlockPos(), isInside(), hitInfo, dist);
    }

    @Override
    public int compareTo(SubHitBlockHitResult o) {

        return Double.compare(dist, o.dist);
    }

    @Override
    public String toString() {

        return super.toString().replace("}", "") + ", subHit=" + subHit + ", sqDist: " + dist + "}";
    }

}
