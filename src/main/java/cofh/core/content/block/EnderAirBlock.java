package cofh.core.content.block;

import cofh.core.content.block.entity.EnderAirTile;
import cofh.core.util.Utils;
import cofh.lib.content.block.entity.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.core.init.CoreMobEffects.ENDERFERENCE;
import static cofh.core.util.references.CoreIDs.ID_ENDER_AIR;

public class EnderAirBlock extends AirBlock implements EntityBlock {

    protected static boolean teleport = true;
    protected static int duration = 40;

    public EnderAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new EnderAirTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ITickableTile.createTicker(level, actualType, TILE_ENTITIES.get(ID_ENDER_AIR), EnderAirTile.class);
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {

        if (rand.nextInt(8) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.PORTAL, pos, rand, 2);
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {

        if (!teleport || Utils.isClientWorld(worldIn)) {
            return;
        }
        if (entityIn instanceof ItemEntity || entityIn instanceof ExperienceOrb) {
            return;
        }
        BlockPos randPos = pos.offset(-128 + worldIn.random.nextInt(257), worldIn.random.nextInt(8), -128 + worldIn.random.nextInt(257));

        if (!worldIn.getBlockState(randPos).getMaterial().isSolid()) {
            if (entityIn instanceof LivingEntity) {
                if (Utils.teleportEntityTo(entityIn, randPos)) {
                    ((LivingEntity) entityIn).addEffect(new MobEffectInstance(ENDERFERENCE.get(), duration, 0, false, false));
                }
            } else if (worldIn.getGameTime() % duration == 0) {
                entityIn.setPos(randPos.getX(), randPos.getY(), randPos.getZ());
                entityIn.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

}
