package cofh.core.block;

import cofh.core.tileentity.LightningAirTile;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class LightningAirBlock extends AirBlock {

    public LightningAirBlock(Properties properties) {

        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return new LightningAirTile();
    }

    //@OnlyIn(Dist.CLIENT)
    //@Override
    //public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    //
    //    if (rand.nextInt(4) == 0) {
    //        worldIn.addParticle(CoreReferences.SPARK_PARTICLE, pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), 0, 0, 0);
    //    }
    //}

}
