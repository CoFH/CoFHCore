package cofh.core.block.impl.rails;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TransferRailBlock extends RailBlockCoFH implements EntityBlock {

    public TransferRailBlock(Properties builder) {

        super(builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return null;
    }

}
