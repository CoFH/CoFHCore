package cofh.core.event;

import cofh.core.config.CoreClientConfig;
import cofh.lib.client.renderer.entity.ITranslucentRenderer;
import cofh.lib.util.Utils;
import cofh.lib.util.constants.ModIds;
import cofh.lib.util.raytracer.VoxelShapeBlockHitResult;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cofh.lib.util.Constants.INVIS_STYLE;
import static cofh.lib.util.constants.NBTTags.TAG_STORED_ENCHANTMENTS;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ModIds.ID_COFH_CORE)
public class CoreClientEvents {

    public static int renderTime;
    public static float renderFrame;

    private static final Set<String> NAMESPACES = new ObjectOpenHashSet<>();

    public static boolean addNamespace(String namespace) {

        return NAMESPACES.add(namespace);
    }

    private CoreClientEvents() {

    }

    @SubscribeEvent
    public static void handleItemTooltipEvent(ItemTooltipEvent event) {

        List<Component> tooltip = event.getToolTip();
        if (tooltip.isEmpty()) {
            return;
        }
        ItemStack stack = event.getItemStack();

        if (CoreClientConfig.enableKeywords.get() && NAMESPACES.contains(Utils.getModId(stack.getItem()))) {
            String keywordKey = stack.getDescriptionId() + ".keyword";
            if (canLocalize(keywordKey)) {
                if (tooltip.get(0) instanceof MutableComponent mutable) {
                    mutable.append(getKeywordTextComponent(keywordKey));
                }
            }
        }
        if (CoreClientConfig.enableItemDescriptions.get() && NAMESPACES.contains(Utils.getModId(stack.getItem()))) {
            String infoKey = stack.getDescriptionId() + ".desc";
            if (canLocalize(infoKey)) {
                tooltip.add(1, getInfoTextComponent(infoKey));
            }
        }
        if (CoreClientConfig.enableEnchantmentDescriptions.get()) {
            if (stack.getTag() != null) {
                ListTag list = stack.getTag().getList(TAG_STORED_ENCHANTMENTS, TAG_COMPOUND);
                if (list.size() == 1) {
                    Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(list.getCompound(0).getString("id")));
                    if (ench != null && ForgeRegistries.ENCHANTMENTS.getKey(ench) != null) {
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
        if (CoreClientConfig.enableItemTags.get() && event.getFlags().isAdvanced()) {
            Item item = event.getItemStack().getItem();
            Block block = Block.byItem(item);

            Set<ResourceLocation> blockTags = block == Blocks.AIR ? Collections.emptySet() : Block.byItem(item).builtInRegistryHolder().tags().map(TagKey::location).collect(Collectors.toSet());
            Set<ResourceLocation> itemTags = item.builtInRegistryHolder().tags().map(TagKey::location).collect(Collectors.toSet());

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
    public static void handleRenderTooltipEvent(RenderTooltipEvent.GatherComponents event) {

        if (event.getTooltipElements().isEmpty()) {
            return;
        }
        event.getTooltipElements().get(0).left().ifPresent((text) -> {
            if (text instanceof MutableComponent mutable) {
                mutable.getSiblings().removeIf(string -> string.getStyle().equals(INVIS_STYLE));
            }
        });
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
    public static void renderTranslucentEntities(RenderLevelLastEvent event) {

        ITranslucentRenderer.renderTranslucent(event.getPoseStack(), event.getPartialTick(), event.getLevelRenderer(), event.getProjectionMatrix());
    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void renderSubHitboxes(RenderHighlightEvent.Block event) {

        BlockHitResult hit = event.getTarget();
        if (hit instanceof VoxelShapeBlockHitResult voxelHit) {
            PoseStack stack = event.getPoseStack();
            BlockPos pos = voxelHit.getBlockPos();
            event.setCanceled(true);

            stack.pushPose();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            bufferShapeHitBox(stack, event.getMultiBufferSource(), event.getCamera(), voxelHit.shape);

            stack.popPose();
        }
    }

    // region HELPERS
    private static void bufferShapeHitBox(PoseStack pStack, MultiBufferSource buffers, Camera renderInfo, VoxelShape shape) {

        Vec3 eye = renderInfo.getPosition();
        pStack.translate((float) -eye.x, (float) -eye.y, (float) -eye.z);
        bufferShapeOutline(buffers.getBuffer(RenderType.lines()), pStack.last().pose(), shape, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private static void bufferShapeOutline(VertexConsumer builder, Matrix4f mat, VoxelShape shape, float r, float g, float b, float a) {

        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            builder.vertex(mat, (float) x1, (float) y1, (float) z1).color(r, g, b, a).endVertex();
            builder.vertex(mat, (float) x2, (float) y2, (float) z2).color(r, g, b, a).endVertex();
        });
    }
    // endregion
}
