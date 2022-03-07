package cofh.lib.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
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
    private final SimpleModel.IFactory<BakedModel> factory;

    public SimpleModel(ModelLoaderRegistry.VanillaProxy model, SimpleModel.IFactory<BakedModel> factory) {

        this.model = model;
        this.factory = factory;
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {

        return factory.create(model.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation));
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {

        model.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {

        return model.getTextures(owner, modelGetter, missingTextureErrors);
    }

    public interface IFactory<T extends BakedModel> {

        T create(BakedModel originalModel);

    }

    // region LOADER
    public static class Loader implements IModelLoader<SimpleModel> {

        private final SimpleModel.IFactory<BakedModel> factory;

        public Loader(SimpleModel.IFactory<BakedModel> factory) {

            this.factory = factory;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {

        }

        @Override
        public SimpleModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

            return new SimpleModel(ModelLoaderRegistry.VanillaProxy.Loader.INSTANCE.read(deserializationContext, modelContents), factory);
        }

    }
    // endregion
}
