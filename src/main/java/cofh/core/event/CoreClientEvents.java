package cofh.core.event;

import cofh.core.init.CoreConfig;
import cofh.lib.client.renderer.entity.ITranslucentRenderer;
import cofh.lib.util.Utils;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.constants.NBTTags.TAG_STORED_ENCHANTMENTS;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE)
public class CoreClientEvents {

    public static int renderTime;
    public static float renderFrame;
    public static MatrixStack levelStack = new MatrixStack();

    private static final Set<String> NAMESPACES = new ObjectOpenHashSet<>();

    static {
        NAMESPACES.add(ID_COFH_CORE);
        NAMESPACES.add(ID_ARCHERS_PARADOX);
        NAMESPACES.add(ID_ENSORCELLATION);
        NAMESPACES.add(ID_REDSTONE_ARSENAL);
        NAMESPACES.add(ID_THERMAL);
    }

    private CoreClientEvents() {

    }

    @SubscribeEvent
    public static void handleItemTooltipEvent(ItemTooltipEvent event) {

        List<ITextComponent> tooltip = event.getToolTip();
        if (tooltip.isEmpty()) {
            return;
        }
        ItemStack stack = event.getItemStack();

        if (CoreConfig.enableKeywords && NAMESPACES.contains(Utils.getItemNamespace(stack.getItem()))) {
            String keywordKey = stack.getDescriptionId() + ".keyword";
            if (canLocalize(keywordKey)) {
                if (tooltip.get(0) instanceof IFormattableTextComponent) {
                    IFormattableTextComponent formatted = (IFormattableTextComponent) tooltip.get(0);
                    formatted.append(getKeywordTextComponent(keywordKey));
                }
            }
        }
        if (CoreConfig.enableItemDescriptions && NAMESPACES.contains(Utils.getItemNamespace(stack.getItem()))) {
            String infoKey = stack.getDescriptionId() + ".desc";
            if (canLocalize(infoKey)) {
                tooltip.add(1, getInfoTextComponent(infoKey));
            }
        }
        if (CoreConfig.enableEnchantmentDescriptions) {
            if (stack.getTag() != null) {
                ListNBT list = stack.getTag().getList(TAG_STORED_ENCHANTMENTS, TAG_COMPOUND);
                if (list.size() == 1) {
                    Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(list.getCompound(0).getString("id")));
                    if (ench != null && ench.getRegistryName() != null) {
                        String enchKey = ench.getDescriptionId() + ".desc";
                        if (canLocalize(enchKey)) {
                            tooltip.add(getInfoTextComponent(enchKey));
                        }
                    }
                }
            }
        }
        //        if (CoreConfig.enableFoodDescriptions) {
        //            if (stack.isEdible()) {
        //
        //            }
        //        }
        if (CoreConfig.enableItemTags && event.getFlags().isAdvanced()) {
            Item item = event.getItemStack().getItem();

            Set<ResourceLocation> blockTags = Block.byItem(item).getTags();
            Set<ResourceLocation> itemTags = item.getTags();

            if (!blockTags.isEmpty() || !itemTags.isEmpty()) {
                if (Screen.hasControlDown()) {
                    if (!blockTags.isEmpty()) {
                        tooltip.add(getTextComponent("info.cofh.block_tags").withStyle(GRAY));
                        blockTags.stream()
                                .map(Object::toString)
                                .map(s -> "  " + s)
                                .map(t -> getTextComponent(t).withStyle(DARK_GRAY))
                                .forEach(tooltip::add);
                    }

                    if (!itemTags.isEmpty()) {
                        tooltip.add(getTextComponent("info.cofh.item_tags").withStyle(GRAY));
                        itemTags.stream()
                                .map(Object::toString)
                                .map(s -> "  " + s)
                                .map(t -> getTextComponent(t).withStyle(DARK_GRAY))
                                .forEach(tooltip::add);
                    }
                } else {
                    tooltip.add(getTextComponent("info.cofh.hold_ctrl_for_tags").withStyle(GRAY));
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleRenderTooltipEvent(RenderTooltipEvent.Pre event) {

        if (event.getLines().isEmpty()) {
            return;
        }
        if (event.getLines().get(0) instanceof IFormattableTextComponent) {
            IFormattableTextComponent formatted = (IFormattableTextComponent) event.getLines().get(0);
            formatted.getSiblings().removeIf(string -> string.getStyle().equals(INVIS_STYLE));
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            renderFrame = event.renderTickTime;
        }
    }

    @SubscribeEvent
    public static void renderTranslucentEntities(RenderWorldLastEvent event) {

        ITranslucentRenderer.renderTranslucent(event.getMatrixStack(), event.getPartialTicks(), event.getContext(), event.getProjectionMatrix());
    }

}
