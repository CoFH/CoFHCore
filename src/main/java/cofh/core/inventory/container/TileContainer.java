package cofh.core.inventory.container;

import cofh.core.tileentity.TileCoFH;
import cofh.lib.inventory.container.ContainerCoFH;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class TileContainer extends ContainerCoFH {

    protected final TileCoFH baseTile;

    public TileContainer(@Nullable MenuType<?> type, int windowId, Level world, BlockPos pos, Inventory inventory, Player player) {

        super(type, windowId, inventory, player);
        BlockEntity tile = world.getBlockEntity(pos);
        baseTile = tile instanceof TileCoFH ? (TileCoFH) tile : null;

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
        for (ContainerListener listener : this.containerListeners) {
            baseTile.sendGuiNetworkData(this, listener);
        }
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
