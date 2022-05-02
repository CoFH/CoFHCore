package cofh.lib.block.impl;

import cofh.lib.entity.AbstractTNTEntity;
import cofh.lib.util.ITNTFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TNTBlockCoFH extends TntBlock {

    protected final ITNTFactory<? extends AbstractTNTEntity> factory;

    public ITNTFactory<? extends AbstractTNTEntity> getFactory() {

        return factory;
    }

    public TNTBlockCoFH(ITNTFactory<? extends AbstractTNTEntity> factory, Properties properties) {

        super(properties);
        this.factory = factory;
    }

    @Override
    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {

        if (!world.isClientSide) {
            Entity entity = factory.createTNT(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, igniter);
            world.addFreshEntity(entity);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn) {

        if (!worldIn.isClientSide) {
            AbstractTNTEntity entity = factory.createTNT(worldIn, ((float) pos.getX() + 0.5F), pos.getY(), ((float) pos.getZ() + 0.5F), explosionIn.getSourceMob());
            entity.setFuse((short) (worldIn.random.nextInt(entity.getFuse() / 4) + entity.getFuse() / 8));
            worldIn.addFreshEntity(entity);
        }
    }

    // region DISPENSER BEHAVIOR
    public static final DefaultDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {

            Item tntItem = stack.getItem();
            if (tntItem instanceof BlockItem) {
                TNTBlockCoFH tntBlock = (TNTBlockCoFH) ((BlockItem) tntItem).getBlock();
                Level world = source.getLevel();
                BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

                Entity entity = tntBlock.factory.createTNT(world, (double) blockpos.getX() + 0.5D, blockpos.getY(), (double) blockpos.getZ() + 0.5D, null);
                world.addFreshEntity(entity);
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.shrink(1);
            }
            return stack;
        }
    };
    // endregion
}
