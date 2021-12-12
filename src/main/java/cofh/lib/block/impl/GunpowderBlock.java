package cofh.lib.block.impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class GunpowderBlock extends FallingBlock {

    private static final float EXPLOSION_STRENGTH = 4.0F;

    public GunpowderBlock(Properties properties) {

        super(properties);
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {

        return -8356741;
    }

    @Override
    public boolean dropFromExplosion(Explosion explosionIn) {

        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        ItemStack stack = player.getItemInHand(handIn);
        Item item = stack.getItem();
        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        } else {
            catchFire(state, worldIn, pos, hit.getDirection(), player);
            worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            if (!player.isCreative()) {
                if (item == Items.FLINT_AND_STEEL) {
                    stack.hurtAndBreak(1, player, (entity) -> {
                        entity.broadcastBreakEvent(handIn);
                    });
                } else {
                    stack.shrink(1);
                }
            }
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {

        explode(world, pos, igniter);
    }

    @Override
    public void wasExploded(World world, BlockPos pos, Explosion explosionIn) {

        if (!world.isClientSide) {
            explode(world, pos, explosionIn.getSourceMob());
        }
    }

    @Override
    public void onProjectileHit(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {

        if (!worldIn.isClientSide) {
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire()) {
                BlockPos blockpos = hit.getBlockPos();
                catchFire(state, worldIn, blockpos, hit.getDirection(), entity instanceof LivingEntity ? (LivingEntity) entity : null);
                worldIn.removeBlock(blockpos, false);
            }
        }
    }

    // region HELPERS
    private static void explode(World world, BlockPos pos, @Nullable LivingEntity igniter) {

        if (!world.isClientSide) {
            world.explode(igniter, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, EXPLOSION_STRENGTH, Explosion.Mode.BREAK);
        }
    }
    // endregion
}
