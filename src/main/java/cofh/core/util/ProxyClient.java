package cofh.core.util;

import cofh.core.event.CoreClientSetupEvents;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProxyClient extends Proxy {

    protected static Map<ResourceLocation, Object> modelMap = new Object2ObjectOpenHashMap<>();
    protected static Set<ModelPropertyWrapper> itemPropertyGetters = new HashSet<>();

    // region HELPERS
    @Override
    public void addIndexedChatMessage(ITextComponent chat, int index) {

        if (chat == null) {
            Minecraft.getInstance().ingameGUI.getChatGUI().deleteChatLine(index);
        } else {
            Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chat, index);
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

        return Minecraft.getInstance().world;
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

        return modelMap.put(loc, model);
    }

    @Override
    public Object getModel(ResourceLocation loc) {

        return modelMap.get(loc);
    }

    @Override
    public void addColorable(Item colorable) {

        CoreClientSetupEvents.addColorable(colorable);
    }

    @Override
    public void registerItemModelProperty(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

        itemPropertyGetters.add(new ModelPropertyWrapper(item, resourceLoc, propertyGetter));
    }
    // endregion

    public static void registerItemModelProperties() {

        for (ModelPropertyWrapper wrapper : itemPropertyGetters) {
            ItemModelsProperties.registerProperty(wrapper.item, wrapper.resourceLoc, wrapper.propertyGetter);
        }
        itemPropertyGetters.clear();
    }

    static class ModelPropertyWrapper {

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
