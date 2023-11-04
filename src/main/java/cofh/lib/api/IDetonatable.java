package cofh.lib.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface IDetonatable {

    void detonate(Vec3 pos);

    interface IDetonateAction {

        void detonate(Level world, Entity explosive, @Nullable Entity owner, Vec3 pos, int radius, int duration, int amplifier);

    }

}
