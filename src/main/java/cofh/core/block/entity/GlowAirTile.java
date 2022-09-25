package cofh.core.block.entity;

import cofh.lib.api.block.entity.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static cofh.lib.util.references.CoreReferences.GLOW_AIR_TILE;

public class GlowAirTile extends BlockEntity implements ITickableTile {

    protected int duration = 200;

    public GlowAirTile(BlockPos pos, BlockState state) {

        super(GLOW_AIR_TILE, pos, state);
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
