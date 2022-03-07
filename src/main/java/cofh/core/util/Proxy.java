package cofh.core.util;

import cofh.lib.tileentity.IAreaEffectTile;
import cofh.lib.util.IProxyItemPropertyGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class Proxy {

    // region HELPERS
    public void addIndexedChatMessage(Component chat, int index) {

    }

    public void playSimpleSound(SoundEvent sound, float volume, float pitch) {

    }

    public Player getClientPlayer() {

        return null;
    }

    public Level getClientWorld() {

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

    public void addAreaEffectTile(IAreaEffectTile tile) {

    }

    public void removeAreaEffectTile(IAreaEffectTile tile) {

    }
    // endregion
}
