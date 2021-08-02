package cofh.lib.block.impl;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RubberBlock extends Block {

    public RubberBlock(Properties properties) {

        super(properties);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {

        if (entityIn.isSuppressingBounce()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.onLivingFall(fallDistance, 0.1F);
        }
    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn) {

        if (entityIn.isSuppressingBounce() || Math.abs(entityIn.getMotion().y) < 0.1D) {
            super.onLanded(worldIn, entityIn);
        } else {
            this.func_226946_a_(entityIn);
        }
    }

    protected void func_226946_a_(Entity entityIn) {

        Vector3d vec3d = entityIn.getMotion();

        if (vec3d.y < 0.0D) {
            double d0 = entityIn instanceof LivingEntity ? 0.75D : 0.50D;
            entityIn.setMotion(vec3d.x, -vec3d.y * d0, vec3d.z);
        }
    }

}
