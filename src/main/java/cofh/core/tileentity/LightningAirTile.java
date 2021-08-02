package cofh.core.tileentity;

import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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

        if (world == null) {
            return;
        }
        if (--duration <= 0) {
            if (world.canSeeSky(pos) && world instanceof ServerWorld) {
                Utils.spawnLightningBolt(world, pos, null);
            }
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            this.world.removeTileEntity(this.pos);
            this.remove();
        }
    }

    public int getDuration() {

        return duration;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

}
