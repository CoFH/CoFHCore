package cofh.core.util;

import cofh.core.event.CoreClientSetupEvents;
import cofh.lib.api.block.entity.IAreaEffectTile;
import cofh.lib.util.helpers.SoundHelper;
import cofh.lib.util.helpers.StringHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.*;

public class ProxyClient extends Proxy {

    protected static final Map<ResourceLocation, Object> MODEL_MAP = new Object2ObjectOpenHashMap<>();
    protected static final Set<ModelPropertyWrapper> ITEM_PROPERTY_GETTERS = new HashSet<>();
    protected static final Set<IAreaEffectTile> AREA_EFFECT_TILES = Collections.newSetFromMap(new WeakHashMap<>());

    // region HELPERS
    @Override
    public void addIndexedChatMessage(Component chat, int index) {

        if (chat == null) {
            Minecraft.getInstance().gui.getChat().removeById(index);
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(chat, index);
        }
    }

    @Override
    public void playSimpleSound(SoundEvent sound, float volume, float pitch) {

        SoundHelper.playSimpleSound(sound, volume, pitch);
    }

    @Override
    public Player getClientPlayer() {

        return Minecraft.getInstance().player;
    }

    @Override
    public Level getClientWorld() {

        return Minecraft.getInstance().level;
    }

    @Override
    public boolean isClient() {

        return true;
    }

    @Override
    public boolean canLocalize(String key) {

        return StringHelper.canLocalize(key);
    }

    @Override
    protected Object addModel(ResourceLocation loc, Object model) {

        return MODEL_MAP.put(loc, model);
    }

    @Override
    public Object getModel(ResourceLocation loc) {

        return MODEL_MAP.get(loc);
    }

    @Override
    public void addColorable(Item colorable) {

        CoreClientSetupEvents.addColorable(colorable);
    }

    @Override
    public void registerItemModelProperty(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

        ITEM_PROPERTY_GETTERS.add(new ModelPropertyWrapper(item, resourceLoc, propertyGetter));
    }

    @Override
    public void addAreaEffectTile(IAreaEffectTile tile) {

        AREA_EFFECT_TILES.add(tile);
    }

    @Override
    public void removeAreaEffectTile(IAreaEffectTile tile) {

        AREA_EFFECT_TILES.remove(tile);
    }
    // endregion

    public static Set<IAreaEffectTile> getAreaEffectTiles() {

        return AREA_EFFECT_TILES;
    }

    public static void registerItemModelProperties() {

        for (ModelPropertyWrapper wrapper : ITEM_PROPERTY_GETTERS) {
            ItemProperties.register(wrapper.item, wrapper.resourceLoc, wrapper.propertyGetter);
        }
        ITEM_PROPERTY_GETTERS.clear();
    }

    protected static class ModelPropertyWrapper {

        Item item;
        ResourceLocation resourceLoc;
        ItemPropertyFunction propertyGetter;

        ModelPropertyWrapper(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

            this.item = item;
            this.resourceLoc = resourceLoc;
            this.propertyGetter = propertyGetter::call;
        }

    }

}
