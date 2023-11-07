package cofh.core.common.block.entity;

import cofh.lib.api.block.entity.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static cofh.core.init.CoreBlockEntities.ENDER_AIR_TILE;

public class EnderAirTile extends BlockEntity implements ITickableTile {

    protected int duration = 200;

    public EnderAirTile(BlockPos pos, BlockState state) {

        super(ENDER_AIR_TILE.get(), pos, state);
    }

    @Override
    public void tick() {

        if (level == null) {
            return;
        }
        if (--duration <= 0) {
            this.level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
        }
    }

    public int getDuration() {

        return duration;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

}
