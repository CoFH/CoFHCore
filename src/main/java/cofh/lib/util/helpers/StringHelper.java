package cofh.lib.util.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static cofh.lib.util.Constants.INVIS_STYLE;

public final class StringHelper {

    private StringHelper() {

    }

    public static final DecimalFormat DF0 = new DecimalFormat("#");
    public static final DecimalFormat DF2 = new DecimalFormat("#.##");

    public static String titleCase(String input) {

        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }

    public static String localize(String key) {

        return I18n.get(key);
    }

    public static String localize(String key, Object... format) {

        return I18n.get(key, format);
    }

    public static boolean canLocalize(String key) {

        return I18n.exists(key);
    }

    public static String format(long number) {

        return StringUtils.normalizeSpace(NumberFormat.getInstance().format(number));
    }

    public static MutableComponent getFluidName(FluidStack stack) {

        Fluid fluid = stack.getFluid();
        MutableComponent name = fluid.getAttributes().getDisplayName(stack).copy();

        switch (fluid.getAttributes().getRarity(stack)) {
            case UNCOMMON -> name.withStyle(ChatFormatting.YELLOW);
            case RARE -> name.withStyle(ChatFormatting.AQUA);
            case EPIC -> name.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return name;
    }

    public static MutableComponent getItemName(ItemStack stack) {

        Item item = stack.getItem();
        MutableComponent name = item.getName(stack).copy();

        switch (item.getRarity(stack)) {
            case UNCOMMON -> name.withStyle(ChatFormatting.YELLOW);
            case RARE -> name.withStyle(ChatFormatting.AQUA);
            case EPIC -> name.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return name;
    }

    public static String getScaledNumber(long number) {

        if (number >= 1_000_000_000) {
            return number / 1_000_000_000 + "." + (number % 1_000_000_000 / 100_000_000) + (number % 100_000_000 / 10_000_000) + "G";
        } else if (number >= 1_000_000) {
            return number / 1_000_000 + "." + (number % 1_000_000 / 100_000) + (number % 100_000 / 10_000) + "M";
        } else if (number >= 1000) {
            return number / 1000 + "." + (number % 1000 / 100) + (number % 100 / 10) + "k";
        } else {
            return String.valueOf(number);
        }
    }

    // region TEXT COMPONENTS
    //    public static ITextComponent getChatComponent(Object object) {
    //
    //        if (object instanceof ITextComponent) {
    //            return (ITextComponent) object;
    //        } else if (object instanceof String) {
    //            return new StringTextComponent((String) object);
    //        } else if (object instanceof ItemStack) {
    //            return ((ItemStack) object).getTextComponent();
    //        } else if (object instanceof Entity) {
    //            return ((Entity) object).getDisplayName();
    //        } else {
    //            return new StringTextComponent(String.valueOf(object));
    //        }
    //    }
    //
    //    public static ITextComponent formChatComponent(Object... chats) {
    //
    //        ITextComponent chat = getChatComponent(chats[0]);
    //        for (int i = 1, chatsLength = chats.length; i < chatsLength; ++i) {
    //            chat.appendSibling(getChatComponent(chats[i]));
    //        }
    //        return chat;
    //    }

    public static String toJSON(Component chatComponent) {

        return Component.Serializer.toJson(chatComponent);
    }

    public static MutableComponent fromJSON(String string) {

        return Component.Serializer.fromJsonLenient(string);
    }

    public static MutableComponent getEmptyLine() {

        return new TextComponent("");
    }

    public static MutableComponent getTextComponent(String key) {

        return canLocalize(key) ? new TranslatableComponent(key) : new TextComponent(key);
    }

    public static MutableComponent getInfoTextComponent(String key) {

        return getTextComponent(key).withStyle(ChatFormatting.GOLD);
    }

    public static MutableComponent getKeywordTextComponent(String key) {

        return getTextComponent(key).withStyle(INVIS_STYLE);
    }
    // endregion

    // region RESOURCE LOCATION
    public static String[] decompose(String resourceLoc, char delimiter) {

        return decompose("minecraft", resourceLoc, delimiter);
    }

    public static String[] decompose(String modid, String resourceLoc, char delimiter) {

        String[] decomposed = new String[]{modid, resourceLoc};
        int delIndex = resourceLoc.indexOf(delimiter);
        if (delIndex >= 0) {
            decomposed[1] = resourceLoc.substring(delIndex + 1);
            if (delIndex >= 1) {
                decomposed[0] = resourceLoc.substring(0, delIndex);
            }
        }
        return decomposed;
    }

    public static String namespace(String resourceLoc) {

        return decompose(resourceLoc, ':')[0];
    }

    public static String path(String resourceLoc) {

        return decompose(resourceLoc, ':')[1];
    }
    // endregion
}
