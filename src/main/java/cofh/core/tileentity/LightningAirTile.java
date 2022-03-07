package cofh.core.tileentity;

<<<<<<< HEAD
import cofh.lib.entity.ElectricArcEntity;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
=======
import cofh.lib.tileentity.ICoFHTickableTile;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

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
<<<<<<< HEAD
            if (level.canSeeSky(worldPosition) && level instanceof ServerWorld) {
                level.addFreshEntity(new ElectricArcEntity(level, Vector3d.atBottomCenterOf(getBlockPos())));
=======
            if (level.canSeeSky(worldPosition) && level instanceof ServerLevel) {
                Utils.spawnLightningBolt(level, worldPosition, null);
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
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
