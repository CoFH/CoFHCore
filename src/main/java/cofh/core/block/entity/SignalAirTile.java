package cofh.core.block.entity;

import cofh.lib.api.block.entity.ITickableTile;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static cofh.core.init.CoreTileEntities.SIGNAL_AIR_TILE;

public class SignalAirTile extends BlockEntity implements ITickableTile {

    protected int duration = 200;
    protected int power = 15;

    public SignalAirTile(BlockPos pos, BlockState state) {

        super(SIGNAL_AIR_TILE.get(), pos, state);
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

    public int getPower() {

        return power;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

    public void setPower(int power) {

        this.power = MathHelper.clamp(power, 0, 15);
    }

}
