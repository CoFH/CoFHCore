package cofh.core.block;

import cofh.core.tileentity.LightningAirTile;
import cofh.lib.tileentity.ICoFHTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static cofh.lib.util.references.CoreReferences.LIGHTNING_AIR_TILE;

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
        return ICoFHTickableTile.createTicker(level, actualType, LIGHTNING_AIR_TILE, LightningAirTile.class);
    }
}
