package cofh.core.content.block;

import cofh.core.content.block.entity.GlowAirTile;
import cofh.lib.api.block.entity.ITickableTile;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
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

import static cofh.core.init.CoreTileEntities.GLOW_AIR_TILE;

public class GlowAirBlock extends AirBlock implements EntityBlock {

    public GlowAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new GlowAirTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ITickableTile.createTicker(level, actualType, GLOW_AIR_TILE.get(), GlowAirTile.class);
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {

        if (rand.nextInt(16) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.INSTANT_EFFECT, pos, rand, 2);
        }
    }

}
