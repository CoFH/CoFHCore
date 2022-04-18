package cofh.lib.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.world.World;

public interface ITNTFactory<T extends TNTEntity> {

    T createTNT(World world, double x, double y, double z, LivingEntity igniter);

}