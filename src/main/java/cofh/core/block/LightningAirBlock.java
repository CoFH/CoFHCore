package cofh.core.block;

import cofh.core.block.entity.LightningAirTile;
import cofh.lib.block.entity.ICoFHTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static cofh.core.init.CoreTileEntities.LIGHTNING_AIR_TILE;

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

        return ICoFHTickableTile.createTicker(level, actualType, LIGHTNING_AIR_TILE.get(), LightningAirTile.class);
    }

    //    @OnlyIn (Dist.CLIENT)
    //    @Override
    //    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
    //
    //        if (rand.nextInt(4) == 0) {
    //            worldIn.addParticle(CoreReferences.SPARK_PARTICLE, pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), 0, 0, 0);
    //        }
    //    }

}
