package cofh.core.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IProxyItemPropertyGetter {

    float call(ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, int seed);

}
