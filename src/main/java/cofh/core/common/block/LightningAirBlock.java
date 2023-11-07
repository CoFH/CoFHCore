package cofh.core.common.block;

import cofh.core.common.block.entity.LightningAirTile;
import cofh.lib.api.block.entity.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static cofh.core.init.CoreBlockEntities.LIGHTNING_AIR_TILE;

public class LightningAirBlock extends AirBlock implements EntityBlock {

    public LightningAirBlock(Properties properties) {

        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new LightningAirTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ITickableTile.createTicker(level, actualType, LIGHTNING_AIR_TILE.get(), LightningAirTile.class);
    }

    //    
    //    @Override
    //    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
    //
    //        if (rand.nextInt(4) == 0) {
    //            worldIn.addParticle(CoreReferences.SPARK_PARTICLE, pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), 0, 0, 0);
    //        }
    //    }

}
