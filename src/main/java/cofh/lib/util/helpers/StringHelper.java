package cofh.lib.util.helpers;

import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class StringHelper {

    private StringHelper() {

    }

    public static final DecimalFormat DF0 = new DecimalFormat("#");
    public static final DecimalFormat DF2 = new DecimalFormat("#.##");

    public static String titleCase(String input) {

        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }

    public static String localize(String key) {

        return I18n.format(key);
    }

    public static String localize(String key, Object... format) {

        return I18n.format(key, format);
    }

    public static boolean canLocalize(String key) {

        return I18n.hasKey(key);
    }

    public static String format(long number) {

        return StringUtils.normalizeSpace(NumberFormat.getInstance().format(number));
    }

    public static IFormattableTextComponent getFluidName(FluidStack stack) {

        Fluid fluid = stack.getFluid();
        IFormattableTextComponent name = fluid.getAttributes().getDisplayName(stack).deepCopy();

        switch (fluid.getAttributes().getRarity(stack)) {
            case UNCOMMON:
                name.mergeStyle(TextFormatting.YELLOW);
                break;
            case RARE:
                name.mergeStyle(TextFormatting.AQUA);
                break;
            case EPIC:
                name.mergeStyle(TextFormatting.LIGHT_PURPLE);
                break;
        }
        return name;
    }

    public static ITextComponent getItemName(ItemStack stack) {

        Item item = stack.getItem();
        IFormattableTextComponent name = item.getDisplayName(stack).deepCopy();

        switch (item.getRarity(stack)) {
            case UNCOMMON:
                name.mergeStyle(TextFormatting.YELLOW);
                break;
            case RARE:
                name.mergeStyle(TextFormatting.AQUA);
                break;
            case EPIC:
                name.mergeStyle(TextFormatting.LIGHT_PURPLE);
                break;
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

    public static String toJSON(ITextComponent chatComponent) {

        return ITextComponent.Serializer.toJson(chatComponent);
    }

    public static ITextComponent fromJSON(String string) {

        return ITextComponent.Serializer.getComponentFromJsonLenient(string);
    }

    public static IFormattableTextComponent getEmptyLine() {

        return new StringTextComponent("");
    }

    public static IFormattableTextComponent getTextComponent(String key) {

        return canLocalize(key) ? new TranslationTextComponent(key) : new StringTextComponent(key);
    }

    public static IFormattableTextComponent getInfoTextComponent(String key) {

        return getTextComponent(key).mergeStyle(TextFormatting.GOLD);
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
