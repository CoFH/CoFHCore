package cofh.lib.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class GunpowderBlock extends FallingBlock {

    private static final float EXPLOSION_STRENGTH = 4.0F;

    public GunpowderBlock(Properties properties) {

        super(properties);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {

        return -8356741;
    }

    @Override
    public boolean dropFromExplosion(Explosion explosionIn) {

        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(handIn);
        Item item = stack.getItem();
        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        } else {
            onCaughtFire(state, worldIn, pos, hit.getDirection(), player);
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
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {

        explode(world, pos, igniter);
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosionIn) {

        if (!world.isClientSide) {
            explode(world, pos, explosionIn.getSourceMob());
        }
    }

    @Override
    public void onProjectileHit(Level worldIn, BlockState state, BlockHitResult hit, Projectile projectile) {

        if (!worldIn.isClientSide) {
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire()) {
                BlockPos blockpos = hit.getBlockPos();
                onCaughtFire(state, worldIn, blockpos, hit.getDirection(), entity instanceof LivingEntity ? (LivingEntity) entity : null);
                worldIn.removeBlock(blockpos, false);
            }
        }
    }

    // region HELPERS
    private static void explode(Level world, BlockPos pos, @Nullable LivingEntity igniter) {

        if (!world.isClientSide) {
            world.explode(igniter, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, EXPLOSION_STRENGTH, Explosion.BlockInteraction.BREAK);
        }
    }
    // endregion
}
