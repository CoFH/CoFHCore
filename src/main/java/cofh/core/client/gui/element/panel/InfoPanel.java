package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.IGuiAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static cofh.core.util.helpers.GuiHelper.ICON_INFORMATION;

public class InfoPanel extends PanelScrolledText {

    public static int defaultSide = LEFT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0xf0f0f0;
    public static int defaultBackgroundColor = 0x555555;

    public InfoPanel(IGuiAccess gui, String info) {

        this(gui, defaultSide, info);
    }

    protected InfoPanel(IGuiAccess gui, int sideIn, String info) {

        super(gui, sideIn, info);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        this.setVisible(() -> !info.isEmpty());
    }

    @Override
    public ResourceLocation getIcon() {

        return ICON_INFORMATION;
    }

    @Override
    public Component getTitle() {

        return Component.translatable("info.cofh.information");
    }

}
