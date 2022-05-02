package cofh.lib.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public interface ITNTFactory<T extends PrimedTnt> {

    T createTNT(Level world, double x, double y, double z, LivingEntity igniter);

}