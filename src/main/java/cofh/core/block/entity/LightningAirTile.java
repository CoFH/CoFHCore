package cofh.core.block.entity;

import cofh.core.entity.ElectricArc;
import cofh.core.util.Utils;
import cofh.lib.content.block.entity.ITickableTile;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static cofh.core.CoFHCore.TILE_ENTITIES;
import static cofh.core.util.references.CoreIDs.ID_LIGHTNING_AIR;

public class LightningAirTile extends BlockEntity implements ITickableTile {

    protected int duration = 100;

    public LightningAirTile(BlockPos pos, BlockState state) {

        super(TILE_ENTITIES.get(ID_LIGHTNING_AIR), pos, state);
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
