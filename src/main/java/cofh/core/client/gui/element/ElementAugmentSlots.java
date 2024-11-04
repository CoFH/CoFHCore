package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.GuiHelper;
import cofh.lib.common.inventory.SlotCoFH;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static cofh.core.util.helpers.GuiHelper.SLOT_SIZE;
import static cofh.lib.util.Constants.*;

public class ElementAugmentSlots extends ElementBase {

    private final IntSupplier numSlots;
    private final List<SlotCoFH> augmentSlots;
    private final List<ElementSlot> slots = new ArrayList<>(MAX_AUGMENTS);

    private final BooleanSupplier hasUpgradeSlot;
    private final BooleanSupplier hasFilterSlot;

    public ElementAugmentSlots(IGuiAccess gui, int posX, int posY, @Nonnull IntSupplier numSlots, @Nonnull List<SlotCoFH> augmentSlots, BooleanSupplier hasUpgradeSlot, BooleanSupplier hasFilterSlot) {

        this(gui, posX, posY, numSlots, augmentSlots, hasUpgradeSlot, hasFilterSlot, null, TRUE);
    }

    public ElementAugmentSlots(IGuiAccess gui, int posX, int posY, @Nonnull IntSupplier numSlots, @Nonnull List<SlotCoFH> augmentSlots, BooleanSupplier hasUpgradeSlot, BooleanSupplier hasFilterSlot, String texture, Supplier<Boolean> drawUnderlay) {

        super(gui, posX, posY);

        this.numSlots = numSlots;
        this.augmentSlots = augmentSlots;
        this.hasUpgradeSlot = hasUpgradeSlot;
        this.hasFilterSlot = hasFilterSlot;

        for (int i = 0; i < augmentSlots.size(); ++i) {
            int slotIndex = i;
            this.augmentSlots.get(i).setEnabled(() -> slotIndex < this.numSlots.getAsInt() && this.visible());
        }
        for (int i = 0; i < MAX_AUGMENTS; ++i) {
            int slotIndex = i;
            ElementSlot slot = GuiHelper.createSlot(gui, 18 * i, 0);
            slot.setVisible(() -> slotIndex < this.numSlots.getAsInt());
            if (texture != null && drawUnderlay != null) {
                slot.setUnderlayTexture(texture, drawUnderlay);
            }
            slots.add(slot);
        }
    }

    @Override
    public void drawBackground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        slots.get(0).clearIconTexture();
        slots.get(1).clearIconTexture();

        if (hasUpgradeSlot.getAsBoolean()) {
            slots.get(0).setIconTexture(PATH_ELEMENTS + "upgrade_underlay_slot.png", hasUpgradeSlot::getAsBoolean);
        }
        if (hasFilterSlot.getAsBoolean()) {
            if (!hasUpgradeSlot.getAsBoolean()) {
                slots.get(0).setIconTexture(PATH_ELEMENTS + "filter_underlay_slot.png", () -> hasFilterSlot.getAsBoolean() && !hasUpgradeSlot.getAsBoolean());
            } else {
                slots.get(1).setIconTexture(PATH_ELEMENTS + "filter_underlay_slot.png", hasFilterSlot::getAsBoolean);
            }
        }
        for (ElementBase slot : slots) {
            if (slot.visible()) {
                slot.drawBackground(pGuiGraphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public void drawForeground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        for (ElementBase slot : slots) {
            if (slot.visible()) {
                slot.drawForeground(pGuiGraphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public void update(int mouseX, int mouseY) {

        int activeSlots = MathHelper.clamp(numSlots.getAsInt(), 0, MAX_AUGMENTS);

        int absX = posX() + offsetX();
        int absY = posY() + offsetY();

        int offset = (hasUpgradeSlot.getAsBoolean() ? 1 : 0) + (hasFilterSlot.getAsBoolean() ? 1 : 0);

        int specialShift = (activeSlots - offset) > 4 ? SLOT_SIZE * 3 / 2 : SLOT_SIZE;
        switch (offset) {
            case 1:
                augmentSlots.get(0).x = absX - specialShift;
                augmentSlots.get(0).y = absY + SLOT_SIZE;
                break;
            case 2:
                augmentSlots.get(0).x = absX - specialShift;
                augmentSlots.get(0).y = absY + SLOT_SIZE / 2;
                augmentSlots.get(1).x = absX - specialShift;
                augmentSlots.get(1).y = absY + SLOT_SIZE * 3 / 2;
                break;
            default:
        }

        switch (activeSlots - offset) {
            case 1:
                augmentSlots.get(offset).x = absX + SLOT_SIZE;
                augmentSlots.get(offset).y = absY + SLOT_SIZE;
                break;
            case 2:
                for (int i = offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * (j % 2);
                    augmentSlots.get(i).y = absY + SLOT_SIZE;
                }
                break;
            case 3:
                for (int i = offset; i < 2 + offset; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * (j % 2);
                    augmentSlots.get(i).y = absY + 9;
                }
                augmentSlots.get(2 + offset).x = absX + SLOT_SIZE;
                augmentSlots.get(2 + offset).y = absY + 9 + SLOT_SIZE;
                break;
            case 4:
                for (int i = offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * (j % 2);
                    augmentSlots.get(i).y = absY + 9 + SLOT_SIZE * (j / 2);
                }
                break;
            case 5:
                for (int i = offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * (j % 3) + 9 * (j / 3);
                    augmentSlots.get(i).y = absY + 9 + SLOT_SIZE * (j / 3);
                }
                break;
            case 6:
                for (int i = offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * (j % 3);
                    augmentSlots.get(i).y = absY + 9 + SLOT_SIZE * (j / 3);
                }
                break;
            case 7:
                for (int i = offset; i < 2 + offset; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * j;
                    augmentSlots.get(i).y = absY;
                }
                for (int i = 2 + offset; i < 5 + offset; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * (j - 2);
                    augmentSlots.get(i).y = absY + SLOT_SIZE;
                }
                for (int i = 5 + offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * (j - 5);
                    augmentSlots.get(i).y = absY + SLOT_SIZE * 2;
                }
                break;
            case 8:
                for (int i = offset; i < 3 + offset; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * j;
                    augmentSlots.get(i).y = absY;
                }
                for (int i = 3 + offset; i < 5 + offset; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + 9 + SLOT_SIZE * (j - 3);
                    augmentSlots.get(i).y = absY + SLOT_SIZE;
                }
                for (int i = 5 + offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * (j - 5);
                    augmentSlots.get(i).y = absY + SLOT_SIZE * 2;
                }
                break;
            case 9:
                for (int i = offset; i < activeSlots; ++i) {
                    int j = i - offset;
                    augmentSlots.get(i).x = absX + SLOT_SIZE * (j % 3);
                    augmentSlots.get(i).y = absY + SLOT_SIZE * (j / 3);
                }
            default:
        }
        for (int i = 0; i < activeSlots; ++i) {
            slots.get(i).setPosition(augmentSlots.get(i).x - 1 - offsetX(), augmentSlots.get(i).y - 1 - offsetY());
        }
    }

}
