package cofh.lib.client.model;

import cofh.core.util.helpers.FluidHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static cofh.lib.util.Constants.BUCKET_VOLUME;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public final class DynamicFluidContainerModel implements IModelGeometry<DynamicFluidContainerModel> {

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_COVER = 7.496f / 16f;
    private static final float SOUTH_Z_COVER = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    @Nonnull
    private final FluidStack fluidStack;

    private final boolean flipGas;
    private final boolean tint;
    private final boolean coverIsMask;
    private final boolean applyFluidLuminosity;

    public DynamicFluidContainerModel(FluidStack fluidStack, boolean flipGas, boolean tint, boolean coverIsMask, boolean applyFluidLuminosity) {

        this.fluidStack = fluidStack;
        this.flipGas = flipGas;
        this.tint = tint;
        this.coverIsMask = coverIsMask;
        this.applyFluidLuminosity = applyFluidLuminosity;
    }

    public DynamicFluidContainerModel withFluid(FluidStack newFluid) {

        return new DynamicFluidContainerModel(newFluid, flipGas, tint, coverIsMask, applyFluidLuminosity);
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {

        Material particleLocation = owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : null;
        Material baseLocation = owner.isTexturePresent("base") ? owner.resolveTexture("base") : null;
        Material fluidMaskLocation = owner.isTexturePresent("fluid_mask") ? owner.resolveTexture("fluid_mask") : null;
        Material coverLocation = owner.isTexturePresent("cover") ? owner.resolveTexture("cover") : null;

        ModelState transformsFromModel = owner.getCombinedTransform();
        Fluid fluid = fluidStack.getFluid();

        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(fluid.getAttributes().getStillTexture())) : null;
        TextureAtlasSprite coverSprite = (coverLocation != null && (!coverIsMask || baseLocation != null)) ? spriteGetter.apply(coverLocation) : null;

        ImmutableMap<TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(transformsFromModel, modelTransform));
        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

        if (particleSprite == null) {
            particleSprite = fluidSprite;
        }
        if (particleSprite == null && !coverIsMask) {
            particleSprite = coverSprite;
        }
        // if the fluid is lighter than air, will manipulate the initial state to be rotated 180deg to turn it upside down
        if (flipGas && fluid != Fluids.EMPTY && fluid.getAttributes().isLighterThanAir()) {
            modelTransform = new SimpleModelState(
                    modelTransform.getRotation().blockCornerToCenter().compose(
                            new Transformation(null, new Quaternion(0, 0, 1, 0), null, null)).blockCenterToCorner());
        }
        Transformation transform = modelTransform.getRotation();
        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particleSprite, new ContainedFluidOverrideHandler(overrides, bakery, owner, this), transformMap);

        if (baseLocation != null) {
            // build base (insidest)
            builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseLocation), transform, spriteGetter));
        }
        if (fluidMaskLocation != null && fluidSprite != null) {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null) {
                // build liquid layer (inside)
                int luminosity = applyFluidLuminosity ? fluid.getAttributes().getLuminosity(fluidStack) : 0;
                int color = tint ? fluid.getAttributes().getColor(fluidStack) : 0xFFFFFFFF;
                builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, 1, luminosity));
                builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1, luminosity));
            }
        }
        if (coverIsMask) {
            if (coverSprite != null && baseLocation != null) {
                TextureAtlasSprite baseSprite = spriteGetter.apply(baseLocation);
                builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, NORTH_Z_COVER, Direction.NORTH, 0xFFFFFFFF, 2));
                builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, SOUTH_Z_COVER, Direction.SOUTH, 0xFFFFFFFF, 2));
            }
        } else {
            if (coverSprite != null) {
                builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_COVER, coverSprite, Direction.NORTH, 0xFFFFFFFF, 2));
                builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_COVER, coverSprite, Direction.SOUTH, 0xFFFFFFFF, 2));
            }
        }
        builder.setParticle(particleSprite);
        return builder.build();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {

        Set<Material> texs = Sets.newHashSet();

        if (owner.isTexturePresent("particle")) {
            texs.add(owner.resolveTexture("particle"));
        }
        if (owner.isTexturePresent("base")) {
            texs.add(owner.resolveTexture("base"));
        }
        if (owner.isTexturePresent("fluid_mask")) {
            texs.add(owner.resolveTexture("fluid_mask"));
        }
        if (owner.isTexturePresent("cover")) {
            texs.add(owner.resolveTexture("cover"));
        }
        return texs;
    }

    public static class Loader implements IModelLoader<DynamicFluidContainerModel> {

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            // no need to clear cache since we create a new model instance
        }

        @Override
        public DynamicFluidContainerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

            FluidStack stack = FluidStack.EMPTY;
            if (modelContents.has("fluid")) {
                ResourceLocation fluidName = new ResourceLocation(modelContents.get("fluid").getAsString());
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
                if (fluid != null) {
                    stack = new FluidStack(fluid, BUCKET_VOLUME);
                }
            }
            boolean flip = false;
            if (modelContents.has("flipGas")) {
                flip = modelContents.get("flipGas").getAsBoolean();
            }
            boolean tint = true;
            if (modelContents.has("applyTint")) {
                tint = modelContents.get("applyTint").getAsBoolean();
            }
            boolean coverIsMask = true;
            if (modelContents.has("coverIsMask")) {
                coverIsMask = modelContents.get("coverIsMask").getAsBoolean();
            }
            boolean applyFluidLuminosity = true;
            if (modelContents.has("applyFluidLuminosity")) {
                applyFluidLuminosity = modelContents.get("applyFluidLuminosity").getAsBoolean();
            }
            // create new model with correct liquid
            return new DynamicFluidContainerModel(stack, flip, tint, coverIsMask, applyFluidLuminosity);
        }

    }

    private static final class ContainedFluidOverrideHandler extends ItemOverrides {

        private final Int2ObjectMap<BakedModel> cache = new Int2ObjectOpenHashMap<>(); // contains all the baked models since they'll never change
        private final ItemOverrides nested;
        private final ModelBakery bakery;
        private final IModelConfiguration owner;
        private final DynamicFluidContainerModel parent;

        private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBakery bakery, IModelConfiguration owner, DynamicFluidContainerModel parent) {

            this.nested = nested;
            this.bakery = bakery;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {

            BakedModel overrideModel = nested.resolve(originalModel, stack, world, entity, seed);
            if (overrideModel != originalModel) {
                return overrideModel;
            }
            return FluidHelper.getFluidContainedInItem(stack)
                    .map(fluidStack -> {
                        int fluidHash = FluidHelper.fluidHashcode(fluidStack);
                        if (!cache.containsKey(fluidHash)) {
                            DynamicFluidContainerModel unbaked = this.parent.withFluid(fluidStack);
                            BakedModel bakedModel = unbaked.bake(owner, bakery, ForgeModelBakery.defaultTextureGetter(), BlockModelRotation.X0_Y0, this, new ResourceLocation(ID_COFH_CORE, "fluid_container_override"));
                            cache.put(fluidHash, bakedModel);
                            return bakedModel;
                        }
                        return cache.get(fluidHash);
                    })
                    .orElse(originalModel); // empty
        }

    }

}
