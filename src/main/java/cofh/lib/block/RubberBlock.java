package cofh.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RubberBlock extends Block {

    public RubberBlock(Properties properties) {

        super(properties);
    }

    @Override
    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {

        if (entityIn.isSuppressingBounce()) {
            super.fallOn(worldIn, state, pos, entityIn, fallDistance);
        } else {
            entityIn.causeFallDamage(fallDistance, 0.1F, DamageSource.FALL);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {

        if (entityIn.isSuppressingBounce() || Math.abs(entityIn.getDeltaMovement().y) < 0.1D) {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        } else {
            this.bounceUp(entityIn);
        }
    }

    protected void bounceUp(Entity entityIn) {

        Vec3 vec3d = entityIn.getDeltaMovement();

        if (vec3d.y < 0.0D) {
            double d0 = entityIn instanceof LivingEntity ? 0.75D : 0.50D;
            entityIn.setDeltaMovement(vec3d.x, -vec3d.y * d0, vec3d.z);
        }
    }

}
