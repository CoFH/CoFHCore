package cofh.core.block;

import cofh.core.tileentity.SignalAirTile;
import cofh.lib.tileentity.ICoFHTickableTile;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.BlockGetter;
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

import java.util.Random;

import static cofh.lib.util.references.CoreReferences.SIGNAL_AIR_TILE;

public class SignalAirBlock extends AirBlock implements EntityBlock {

    public SignalAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new SignalAirTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ICoFHTickableTile.createTicker(level, actualType, SIGNAL_AIR_TILE, SignalAirTile.class);
    }

    @Override
    public boolean isSignalSource(BlockState state) {

        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {

        return blockAccess.getBlockEntity(pos) instanceof SignalAirTile tile ? tile.getPower() : 0;
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(8) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, DustParticleOptions.REDSTONE, pos, rand, 2);
        }
    }

}
