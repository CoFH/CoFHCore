package cofh.lib.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IProxyItemPropertyGetter {

    float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity);

}
