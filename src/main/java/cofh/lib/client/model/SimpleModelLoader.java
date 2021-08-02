package cofh.lib.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class SimpleModelLoader implements IModelLoader<SimpleModelGeometry> {

    private final SimpleModelGeometry.IFactory<IBakedModel> factory;

    public SimpleModelLoader(SimpleModelGeometry.IFactory<IBakedModel> factory) {

        this.factory = factory;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public SimpleModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

        return new SimpleModelGeometry(ModelLoaderRegistry.VanillaProxy.Loader.INSTANCE.read(deserializationContext, modelContents), factory);
    }

}
