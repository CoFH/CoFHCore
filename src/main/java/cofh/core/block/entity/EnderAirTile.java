package cofh.core.block.entity;

import cofh.lib.block.entity.ICoFHTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.lib.util.references.CoreIDs.ID_ENDER_AIR;

public class EnderAirTile extends BlockEntity implements ICoFHTickableTile {

    protected int duration = 200;

    public EnderAirTile(BlockPos pos, BlockState state) {

        super(TILE_ENTITIES.get(ID_ENDER_AIR), pos, state);
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
