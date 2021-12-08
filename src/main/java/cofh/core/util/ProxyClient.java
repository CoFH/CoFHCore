package cofh.core.util;

import cofh.core.event.CoreClientSetupEvents;
import cofh.lib.tileentity.IAreaEffectTile;
import cofh.lib.util.IProxyItemPropertyGetter;
import cofh.lib.util.helpers.SoundHelper;
import cofh.lib.util.helpers.StringHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.*;

public class ProxyClient extends Proxy {

    protected static final Map<ResourceLocation, Object> MODEL_MAP = new Object2ObjectOpenHashMap<>();
    protected static final Set<ModelPropertyWrapper> ITEM_PROPERTY_GETTERS = new HashSet<>();
    protected static final Set<IAreaEffectTile> AREA_EFFECT_TILES = Collections.newSetFromMap(new WeakHashMap<>());

    // region HELPERS
    @Override
    public void addIndexedChatMessage(ITextComponent chat, int index) {

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
    public PlayerEntity getClientPlayer() {

        return Minecraft.getInstance().player;
    }

    @Override
    public World getClientWorld() {

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
            ItemModelsProperties.register(wrapper.item, wrapper.resourceLoc, wrapper.propertyGetter);
        }
        ITEM_PROPERTY_GETTERS.clear();
    }

    protected static class ModelPropertyWrapper {

        Item item;
        ResourceLocation resourceLoc;
        IItemPropertyGetter propertyGetter;

        ModelPropertyWrapper(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

            this.item = item;
            this.resourceLoc = resourceLoc;
            this.propertyGetter = propertyGetter::call;
        }

    }

}
