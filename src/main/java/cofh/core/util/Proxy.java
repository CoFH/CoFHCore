package cofh.core.util;

import cofh.lib.util.IProxyItemPropertyGetter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Proxy {

    // region HELPERS
    public void addIndexedChatMessage(ITextComponent chat, int index) {

    }

    public void playSimpleSound(SoundEvent sound, float volume, float pitch) {

    }

    public PlayerEntity getClientPlayer() {

        return null;
    }

    public World getClientWorld() {

        return null;
    }

    public boolean isClient() {

        return false;
    }

    public boolean canLocalize(String key) {

        return false;
    }

    public Object addModel(Item item, Object model) {

        return item == null ? null : addModel(item.getRegistryName(), model);
    }

    protected Object addModel(ResourceLocation loc, Object model) {

        return null;
    }

    public Object getModel(ResourceLocation loc) {

        return null;
    }

    public void addColorable(Item colorable) {

    }

    public void registerItemModelProperty(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

    }
    // endregion
}
