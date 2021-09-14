package cofh.lib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static cofh.lib.util.helpers.ItemHelper.cloneStack;

public abstract class AbstractKnifeEntity extends AbstractArrowEntity {

    private ItemStack knifeItem = ItemStack.EMPTY;

    protected AbstractKnifeEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn) {

        super(type, worldIn);
    }

    protected AbstractKnifeEntity(EntityType<? extends AbstractArrowEntity> type, double x, double y, double z, World worldIn) {

        super(type, x, y, z, worldIn);
    }

    protected AbstractKnifeEntity(EntityType<? extends AbstractArrowEntity> type, LivingEntity livingEntityIn, World worldIn) {

        super(type, livingEntityIn, worldIn);
    }

    @Override
    protected ItemStack getPickupItem() {

        return cloneStack(knifeItem);
    }

}
