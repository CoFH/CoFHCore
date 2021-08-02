package cofh.core.util;

import cofh.core.CoFHCore;
import cofh.lib.util.IProxyItemPropertyGetter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ProxyUtils {

    private ProxyUtils() {

    }

    public static void addIndexedChatMessage(ITextComponent chat, int index) {

        CoFHCore.PROXY.addIndexedChatMessage(chat, index);
    }

    public static void playSimpleSound(SoundEvent sound, float volume, float pitch) {

        CoFHCore.PROXY.playSimpleSound(sound, volume, pitch);
    }

    public static PlayerEntity getClientPlayer() {

        return CoFHCore.PROXY.getClientPlayer();
    }

    public static World getClientWorld() {

        return CoFHCore.PROXY.getClientWorld();
    }

    public static boolean isClient() {

        return CoFHCore.PROXY.isClient();
    }

    public static boolean canLocalize(String key) {

        return CoFHCore.PROXY.canLocalize(key);
    }

    public static synchronized Object addModel(Item item, Object model) {

        return CoFHCore.PROXY.addModel(item, model);
    }

    public static Object getModel(ResourceLocation loc) {

        return CoFHCore.PROXY.getModel(loc);
    }

    public static void registerColorable(Item colorable) {

        CoFHCore.PROXY.addColorable(colorable);
    }

    public static void registerItemModelProperty(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

        CoFHCore.PROXY.registerItemModelProperty(item, resourceLoc, propertyGetter);
    }

}
