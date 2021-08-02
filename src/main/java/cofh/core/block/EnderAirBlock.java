package cofh.core.block;

import cofh.core.tileentity.EnderAirTile;
import cofh.lib.util.Utils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static cofh.lib.util.references.CoreReferences.ENDERFERENCE;

public class EnderAirBlock extends AirBlock {

    protected static boolean teleport = true;
    protected static int duration = 40;

    public EnderAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return new EnderAirTile();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(8) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.PORTAL, pos, rand, 2);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {

        if (!teleport || Utils.isClientWorld(worldIn)) {
            return;
        }
        if (entityIn instanceof ItemEntity || entityIn instanceof ExperienceOrbEntity) {
            return;
        }
        BlockPos randPos = pos.add(-128 + worldIn.rand.nextInt(257), worldIn.rand.nextInt(8), -128 + worldIn.rand.nextInt(257));

        if (!worldIn.getBlockState(randPos).getMaterial().isSolid()) {
            if (entityIn instanceof LivingEntity) {
                if (Utils.teleportEntityTo(entityIn, randPos)) {
                    ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(ENDERFERENCE, duration, 0, false, false));
                }
            } else if (worldIn.getGameTime() % duration == 0) {
                entityIn.setPosition(randPos.getX(), randPos.getY(), randPos.getZ());
                entityIn.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

}
