package cofh.lib.util.raytracer;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class DistanceRayTraceResult extends BlockRayTraceResult implements Comparable<DistanceRayTraceResult> {

    /**
     * The square distance from the start of the raytrace.
     */
    public double dist;

    public DistanceRayTraceResult(Vector3d hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {

        this(false, hitVec, faceIn, posIn, isInside, data, dist);
    }

    protected DistanceRayTraceResult(boolean isMissIn, Vector3d hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {

        super(isMissIn, hitVec, faceIn, posIn, isInside);
        setData(data);
        this.dist = dist;
    }

    public void setData(Object data) {

        if (data instanceof Integer) {
            subHit = (Integer) data;
        }
        hitInfo = data;
    }

    @Override
    public DistanceRayTraceResult withDirection(Direction newFace) {

        return new DistanceRayTraceResult(getType() == Type.MISS, getLocation(), getDirection(), getBlockPos(), isInside(), hitInfo, dist);
    }

    @Deprecated
    public void offsetHit(BlockPos pos) {

        location = location.add(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int compareTo(DistanceRayTraceResult o) {

        return Double.compare(dist, o.dist);
    }

    @Override
    public String toString() {

        return super.toString().replace("}", "") + ", subHit=" + subHit + ", sqDist: " + dist + "}";
    }

}
