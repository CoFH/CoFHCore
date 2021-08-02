package cofh.lib.block.impl.crops;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.function.Supplier;

public class AttachedStemBlockCoFH extends AttachedStemBlock {

    protected Supplier<Block> cropBlock = () -> Blocks.MELON;
    protected Supplier<Item> seed = () -> Items.MELON_SEEDS;

    public AttachedStemBlockCoFH(Properties properties) {

        super((StemGrownBlock) Blocks.MELON, properties);
    }

    public AttachedStemBlockCoFH crop(Supplier<Block> crop) {

        this.cropBlock = crop;
        return this;
    }

    public AttachedStemBlockCoFH seed(Supplier<Item> seed) {

        this.seed = seed;
        return this;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

        return facingState.getBlock() != this.cropBlock.get() && facing == stateIn.get(FACING)
                ? ((StemGrownBlock) this.cropBlock.get()).getStem().getDefaultState().with(StemBlock.AGE, 7)
                : !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState()
                : stateIn;
    }

    @Override
    protected Item getSeeds() {

        return seed.get();
    }

}
