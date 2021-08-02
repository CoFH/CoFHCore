package cofh.lib.capability;

import cofh.lib.capability.templates.AreaEffectItemWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityAreaEffect {

    private static boolean registered = false;

    @CapabilityInject(IAreaEffect.class)
    public static Capability<IAreaEffect> AREA_EFFECT_ITEM_CAPABILITY = null;

    public static void register() {

        if (registered) {
            return;
        }
        registered = true;

        CapabilityManager.INSTANCE.register(IAreaEffect.class, new DefaultAreaEffectItemHandlerStorage<>(), () -> new AreaEffectItemWrapper(ItemStack.EMPTY));
    }

    private static class DefaultAreaEffectItemHandlerStorage<T extends IAreaEffect> implements IStorage<T> {

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
