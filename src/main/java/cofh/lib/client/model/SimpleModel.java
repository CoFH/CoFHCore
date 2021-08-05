package cofh.lib.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class SimpleModel implements ISimpleModelGeometry<SimpleModel> {

    private final ModelLoaderRegistry.VanillaProxy model;
    private final SimpleModel.IFactory<IBakedModel> factory;

    public SimpleModel(ModelLoaderRegistry.VanillaProxy model, SimpleModel.IFactory<IBakedModel> factory) {

        this.model = model;
        this.factory = factory;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {

        return factory.create(model.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation));
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {

        model.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {

        return model.getTextures(owner, modelGetter, missingTextureErrors);
    }

    public interface IFactory<T extends IBakedModel> {

        T create(IBakedModel originalModel);

    }

    // region LOADER
    public static class Loader implements IModelLoader<SimpleModel> {

        private final SimpleModel.IFactory<IBakedModel> factory;

        public Loader(SimpleModel.IFactory<IBakedModel> factory) {

            this.factory = factory;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {

        }

        @Override
        public SimpleModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

            return new SimpleModel(ModelLoaderRegistry.VanillaProxy.Loader.INSTANCE.read(deserializationContext, modelContents), factory);
        }

    }
    // endregion
}
