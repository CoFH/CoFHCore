package cofh.lib.capability;

import cofh.lib.capability.templates.EnchantableItemWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.Collections;

public class CapabilityEnchantableItem {

    private static boolean registered = false;

    @CapabilityInject(IEnchantableItem.class)
    public static Capability<IEnchantableItem> ENCHANTABLE_ITEM_CAPABILITY = null;

    public static void register() {

        if (registered) {
            return;
        }
        registered = true;

        CapabilityManager.INSTANCE.register(IEnchantableItem.class, new DefaultEnchantableHandlerStorage<>(), () -> new EnchantableItemWrapper(ItemStack.EMPTY, Collections.emptyList()));
    }

    private static class DefaultEnchantableHandlerStorage<T extends IEnchantableItem> implements IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {

            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

        }

    }

}
