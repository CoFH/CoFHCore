package cofh.core.block.entity;

import cofh.lib.block.entity.ICoFHTickableTile;
import cofh.lib.entity.ElectricArc;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static cofh.lib.util.references.CoreReferences.LIGHTNING_AIR_TILE;

public class LightningAirTile extends BlockEntity implements ICoFHTickableTile {

    protected int duration = 100;

    public LightningAirTile(BlockPos pos, BlockState state) {

        super(LIGHTNING_AIR_TILE, pos, state);
        duration = MathHelper.nextInt(MathHelper.RANDOM, 20, duration);
    }

    @Override
    public void tick() {

        if (level == null) {
            return;
        }
        if (--duration <= 0) {

            if (level.canSeeSky(worldPosition) && level instanceof ServerLevel) {
                level.addFreshEntity(new ElectricArc(level, Vec3.atBottomCenterOf(getBlockPos())));

                if (level.canSeeSky(worldPosition) && level instanceof ServerLevel) {
                    Utils.spawnLightningBolt(level, worldPosition, null);
                }
                this.level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                this.level.removeBlockEntity(this.worldPosition);
                this.setRemoved();
            }
        }
    }

    public int getDuration() {

        return duration;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

}
