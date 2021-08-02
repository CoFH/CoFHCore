package cofh.lib.block.impl;

import cofh.lib.entity.AbstractTNTEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TNTBlockCoFH extends TNTBlock {

    protected final ITNTFactory<? extends AbstractTNTEntity> factory;

    public TNTBlockCoFH(ITNTFactory<? extends AbstractTNTEntity> factory, Properties properties) {

        super(properties);
        this.factory = factory;
    }

    @Override
    public void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {

        if (!world.isRemote) {
            Entity entity = factory.createTNT(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, igniter);
            world.addEntity(entity);
            world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {

        if (!worldIn.isRemote) {
            AbstractTNTEntity entity = factory.createTNT(worldIn, ((float) pos.getX() + 0.5F), pos.getY(), ((float) pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
            entity.setFuse((short) (worldIn.rand.nextInt(entity.getFuse() / 4) + entity.getFuse() / 8));
            worldIn.addEntity(entity);
        }
    }

    // region FACTORY
    public interface ITNTFactory<T extends AbstractTNTEntity> {

        T createTNT(World world, double posX, double posY, double posZ, @Nullable LivingEntity igniter);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    public static final DefaultDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        @Override
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {

            Item tntItem = stack.getItem();
            if (tntItem instanceof BlockItem) {
                TNTBlockCoFH tntBlock = (TNTBlockCoFH) ((BlockItem) tntItem).getBlock();
                World world = source.getWorld();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                Entity entity = tntBlock.factory.createTNT(world, (double) blockpos.getX() + 0.5D, blockpos.getY(), (double) blockpos.getZ() + 0.5D, null);
                world.addEntity(entity);
                world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.shrink(1);
            }
            return stack;
        }
    };
    // endregion
}
