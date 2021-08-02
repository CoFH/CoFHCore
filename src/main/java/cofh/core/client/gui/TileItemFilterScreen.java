package cofh.core.client.gui;

import cofh.core.client.gui.element.ElementButton;
import cofh.core.client.gui.element.ElementTexture;
import cofh.core.client.gui.element.SimpleTooltip;
import cofh.core.inventory.container.TileItemFilterContainer;
import cofh.core.network.packet.server.FilterGuiOpenPacket;
import cofh.lib.util.helpers.FilterHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

import static cofh.core.util.helpers.GuiHelper.*;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.helpers.SoundHelper.playClickSound;

public class TileItemFilterScreen extends ContainerScreenCoFH<TileItemFilterContainer> {

    public static final String TEX_PATH = ID_COFH_CORE + ":textures/gui/generic.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

    public static final String TEX_DENY_LIST = ID_COFH_CORE + ":textures/gui/filters/filter_deny_list.png";
    public static final String TEX_ALLOW_LIST = ID_COFH_CORE + ":textures/gui/filters/filter_allow_list.png";
    public static final String TEX_IGNORE_NBT = ID_COFH_CORE + ":textures/gui/filters/filter_ignore_nbt.png";
    public static final String TEX_USE_NBT = ID_COFH_CORE + ":textures/gui/filters/filter_use_nbt.png";

    public TileItemFilterScreen(TileItemFilterContainer container, PlayerInventory inv, ITextComponent titleIn) {

        super(container, inv, titleIn);

        texture = TEXTURE;
        info = generatePanelInfo("info.cofh_core.item_filter");
    }

    @Override
    public void init() {

        super.init();

        for (int i = 0; i < container.getFilterSize(); ++i) {
            Slot slot = container.inventorySlots.get(i);
            addElement(createSlot(this, slot.xPos, slot.yPos));
        }
        addButtons();

        // Filter Tab
        addElement(new ElementTexture(this, 4, -21)
                .setUV(24, 0)
                .setSize(24, 21)
                .setTexture(TAB_TOP, 48, 32)
                .setVisible(() -> FilterHelper.hasFilter(container.getFilterableTile())));
        addElement(new ElementTexture(this, 8, -17) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                FilterGuiOpenPacket.openTileGui(container.getFilterableTile());
                return true;
            }
        }
                .setSize(16, 16)
                .setTexture(NAV_BACK, 16, 16)
                .setTooltipFactory((element, mouseX, mouseY) -> Collections.singletonList(container.getFilterableTile().getDisplayName()))
                .setVisible(() -> FilterHelper.hasFilter(container.getFilterableTile())));
    }

    // region ELEMENTS
    protected void addButtons() {

        addElement(new ElementButton(this, 132, 22) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                container.setAllowList(true);
                playClickSound(0.7F);
                return true;
            }
        }
                .setSize(20, 20)
                .setTexture(TEX_DENY_LIST, 40, 20)
                .setTooltipFactory(new SimpleTooltip(new TranslationTextComponent("info.cofh.filter.allowlist.0")))
                .setVisible(() -> !container.getAllowList()));

        addElement(new ElementButton(this, 132, 22) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                container.setAllowList(false);
                playClickSound(0.4F);
                return true;
            }
        }
                .setSize(20, 20)
                .setTexture(TEX_ALLOW_LIST, 40, 20)
                .setTooltipFactory(new SimpleTooltip(new TranslationTextComponent("info.cofh.filter.allowlist.1")))
                .setVisible(() -> container.getAllowList()));

        addElement(new ElementButton(this, 132, 44) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                container.setCheckNBT(true);
                playClickSound(0.7F);
                return true;
            }
        }
                .setSize(20, 20)
                .setTexture(TEX_IGNORE_NBT, 40, 20)
                .setTooltipFactory(new SimpleTooltip(new TranslationTextComponent("info.cofh.filter.checkNBT.0")))
                .setVisible(() -> !container.getCheckNBT()));

        addElement(new ElementButton(this, 132, 44) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                container.setCheckNBT(false);
                playClickSound(0.4F);
                return true;
            }
        }
                .setSize(20, 20)
                .setTexture(TEX_USE_NBT, 40, 20)
                .setTooltipFactory(new SimpleTooltip(new TranslationTextComponent("info.cofh.filter.checkNBT.1")))
                .setVisible(() -> container.getCheckNBT()));
    }
    // endregion
}
