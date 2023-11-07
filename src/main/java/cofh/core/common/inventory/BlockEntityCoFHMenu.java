package cofh.core.common.inventory;

import cofh.core.common.block.entity.BlockEntityCoFH;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class BlockEntityCoFHMenu extends ContainerMenuCoFH {

    protected final BlockEntityCoFH baseTile;

    public BlockEntityCoFHMenu(@Nullable MenuType<?> type, int windowId, Level world, BlockPos pos, Inventory inventory, Player player) {

        super(type, windowId, inventory, player);
        BlockEntity tile = world.getBlockEntity(pos);
        baseTile = tile instanceof BlockEntityCoFH ? (BlockEntityCoFH) tile : null;

        if (baseTile != null) {
            baseTile.addPlayerUsing();
        }
    }

    @Override
    protected int getMergeableSlotCount() {

        return baseTile == null ? 0 : baseTile.invSize();
    }

    @Override
    public boolean stillValid(Player player) {

        return baseTile == null || baseTile.playerWithinDistance(player, 64D);
    }

    @Override
    public void broadcastChanges() {

        super.broadcastChanges();
        if (baseTile == null) {
            return;
        }
        baseTile.sendGuiNetworkData(this, player);
    }

    @Override
    public void setData(int i, int j) {

        super.setData(i, j);

        if (baseTile == null) {
            return;
        }
        baseTile.receiveGuiNetworkData(i, j);
    }

    @Override
    public void removed(Player player) {

        if (baseTile != null) {
            baseTile.removePlayerUsing();
        }
        super.removed(player);
    }

}
