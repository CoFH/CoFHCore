package cofh.core.tileentity;

import cofh.lib.entity.ElectricArcEntity;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import static cofh.lib.util.references.CoreReferences.LIGHTNING_AIR_TILE;

public class LightningAirTile extends TileEntity implements ITickableTileEntity {

    protected int duration = 100;

    public LightningAirTile() {

        super(LIGHTNING_AIR_TILE);
        duration = MathHelper.nextInt(MathHelper.RANDOM, 20, duration);
    }

    @Override
    public void tick() {

        if (level == null) {
            return;
        }
        if (--duration <= 0) {
            if (level.canSeeSky(worldPosition) && level instanceof ServerWorld) {
                level.addFreshEntity(new ElectricArcEntity(level, Vector3d.atBottomCenterOf(getBlockPos())));
            }
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
