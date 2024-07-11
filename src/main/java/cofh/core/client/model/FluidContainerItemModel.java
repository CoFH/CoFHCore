package cofh.core.client.model;

import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.RenderHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.*;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

import static cofh.lib.util.Constants.BUCKET_VOLUME;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public final class FluidContainerItemModel implements IUnbakedGeometry<FluidContainerItemModel> {

    // Depth offsets to prevent Z-fighting
    private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.004f), new Quaternionf());
    private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.008f), new Quaternionf());
    // Transformer to set quads to max brightness
    private static final IQuadTransformer MAX_LIGHTMAP_TRANSFORMER = QuadTransformers.applyingLightmap(RenderHelper.FULL_BRIGHT);

    @Nonnull
    private final FluidStack fluidStack;

    public FluidContainerItemModel(FluidStack fluidStack) {

        this.fluidStack = fluidStack;
    }

    public FluidContainerItemModel withProperties(FluidStack newFluid) {

        return new FluidContainerItemModel(newFluid);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {

        Material particleLocation = context.hasMaterial("particle") ? context.getMaterial("particle") : null;
        Material fluidMaskLocation = context.hasMaterial("fluid_mask") ? context.getMaterial("fluid_mask") : null;
        Material baseLocation = context.hasMaterial("layer0") ? context.getMaterial("layer0") : null;

        Fluid fluid = fluidStack.getFluid();

        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(ClientHooks.getBlockMaterial(IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack))) : null;
        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;
        if (particleSprite == null) {
            particleSprite = fluidSprite != null ? fluidSprite : spriteGetter.apply(baseLocation);
        }
        var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);
        var modelBuilder = CompositeModel.Baked.builder(itemContext, particleSprite, new ContainedFluidOverrideHandler(baker, itemContext, this), context.getTransforms());
        var normalRenderTypes = getLayerRenderTypes();

        if (baseLocation != null) {
            var baseSprite = spriteGetter.apply(baseLocation);
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelState, modelLocation);
            modelBuilder.addQuads(normalRenderTypes, quads);
        }

        if (fluidMaskLocation != null && fluidSprite != null) {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null) {
                // Fluid layer
                var transformedState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
                var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite); // Use template as mask
                var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState, modelLocation); // Bake with fluid texture

                var unlit = fluid.getFluidType().getLightLevel() > 0;
                var renderTypes = getLayerRenderTypes();
                if (unlit) {
                    MAX_LIGHTMAP_TRANSFORMER.processInPlace(quads);
                }
                modelBuilder.addQuads(renderTypes, quads);
            }
        }
        modelBuilder.setParticle(particleSprite);
        return modelBuilder.build();
    }

    public static RenderTypeGroup getLayerRenderTypes() {

        return new RenderTypeGroup(RenderType.cutout(), NeoForgeRenderTypes.ITEM_LAYERED_CUTOUT.get());
    }

    public static class Loader implements IGeometryLoader<FluidContainerItemModel> {

        @Override
        public FluidContainerItemModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {

            FluidStack stack = FluidStack.EMPTY;
            if (jsonObject.has("fluid")) {
                ResourceLocation fluidName = new ResourceLocation(jsonObject.get("fluid").getAsString());
                Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);
                if (fluid != null) {
                    stack = new FluidStack(fluid, BUCKET_VOLUME);
                }
            }
            // create new model with correct liquid
            return new FluidContainerItemModel(stack);
        }

    }

    private static final class ContainedFluidOverrideHandler extends ItemOverrides {

        private final Map<Integer, BakedModel> cache = new Int2ObjectOpenHashMap<>(); // contains all the baked models since they'll never change
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;
        private final FluidContainerItemModel parent;

        private ContainedFluidOverrideHandler(ModelBaker baker, IGeometryBakingContext owner, FluidContainerItemModel parent) {

            this.baker = baker;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {

            FluidStack fluidStack = FluidHelper.getFluidContainedInItem(stack);
            int fluidHash = FluidHelper.fluidHashcode(fluidStack);
            if (!cache.containsKey(fluidHash)) {
                FluidContainerItemModel unbaked = this.parent.withProperties(fluidStack);
                BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this, new ResourceLocation(ID_COFH_CORE, "fluid_container_override"));
                cache.put(fluidHash, bakedModel);
                return bakedModel;
            }
            return cache.get(fluidHash);
        }

    }

}
