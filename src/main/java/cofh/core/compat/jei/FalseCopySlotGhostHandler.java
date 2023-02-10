package cofh.core.compat.jei;

import cofh.core.client.gui.ContainerScreenCoFH;
import cofh.core.network.packet.server.GhostItemPacket;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.util.helpers.ItemHelper.cloneStack;

@SuppressWarnings ({"rawtypes", "unchecked"})
public class FalseCopySlotGhostHandler implements IGhostIngredientHandler<ContainerScreenCoFH> {

    @Override
    public <I> List<Target<I>> getTargets(ContainerScreenCoFH gui, I ingredient, boolean doStart) {

        List<Target<I>> targets = new ArrayList<>();
        for (int i = 0; i < gui.getMenu().slots.size(); ++i) {
            Slot slot = gui.getMenu().getSlot(i);

            if (slot instanceof SlotFalseCopy && ingredient instanceof ItemStack item && (slot.mayPlace(item))) {
                Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
                targets.add(new Target<>() {

                    @Override
                    public Rect2i getArea() {

                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {

                        ItemStack stack = cloneStack((ItemStack) ingredient);
                        slot.set(stack);
                        GhostItemPacket.sendToServer(slot.slot, stack);
                    }
                });
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {

    }

}
