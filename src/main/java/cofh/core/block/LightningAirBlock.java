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

}
