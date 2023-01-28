package cofh.lib.util.raytracer;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MissResult extends HitResult {

    public MissResult(Vec3 location) {

        super(location);
    }

    @Override
    public Type getType() {

        return Type.MISS;
    }

}
