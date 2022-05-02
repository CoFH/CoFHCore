package cofh.lib.block.impl.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class AttachedStemBlockCoFH extends AttachedStemBlock {

    protected Supplier<Block> cropBlock = () -> Blocks.MELON;

    public AttachedStemBlockCoFH(Supplier<Item> seed, Properties properties) {

        super((StemGrownBlock) Blocks.MELON, seed, properties);
    }

    public AttachedStemBlockCoFH crop(Supplier<Block> crop) {

        this.cropBlock = crop;
        return this;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {

        return facingState.getBlock() != this.cropBlock.get() && facing == stateIn.getValue(FACING)
                ? ((StemGrownBlock) this.cropBlock.get()).getStem().defaultBlockState().setValue(StemBlock.AGE, 7)
                : !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState()
                : stateIn;
    }

}
