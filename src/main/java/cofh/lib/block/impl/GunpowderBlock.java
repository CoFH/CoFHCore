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

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {

        return -8356741;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {

        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        ItemStack stack = player.getHeldItem(handIn);
        Item item = stack.getItem();
        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        } else {
            catchFire(state, worldIn, pos, hit.getFace(), player);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            if (!player.isCreative()) {
                if (item == Items.FLINT_AND_STEEL) {
                    stack.damageItem(1, player, (entity) -> {
                        entity.sendBreakAnimation(handIn);
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
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosionIn) {

        if (!world.isRemote) {
            explode(world, pos, explosionIn.getExplosivePlacedBy());
        }
    }

    @Override
    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {

        if (!worldIn.isRemote) {
            Entity entity = projectile.func_234616_v_();
            if (projectile.isBurning()) {
                BlockPos blockpos = hit.getPos();
                catchFire(state, worldIn, blockpos, hit.getFace(), entity instanceof LivingEntity ? (LivingEntity) entity : null);
                worldIn.removeBlock(blockpos, false);
            }
        }
    }

    // region HELPERS
    private static void explode(World world, BlockPos pos, @Nullable LivingEntity igniter) {

        if (!world.isRemote) {
            world.createExplosion(igniter, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, EXPLOSION_STRENGTH, Explosion.Mode.BREAK);
        }
    }
    // endregion
}
