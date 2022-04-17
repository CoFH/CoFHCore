package cofh.lib.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IDetonatable {

    void detonate(Vector3d pos);

    interface IDetonateAction {

        void detonate(World world, Entity explosive, @Nullable Entity owner, Vector3d pos, float radius, int duration, int amplifier);
    }

}
