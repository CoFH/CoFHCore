package cofh.core.client;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PostEffect implements ResourceManagerReloadListener {

    private static final Collection<PostEffect> EFFECTS = new ArrayList<>();

    protected final ResourceLocation shader;
    protected PostChain chain;
    protected boolean loaded;

    public PostEffect(ResourceLocation shader) {

        this.shader = new ResourceLocation(shader.getNamespace(), "shaders/post/" + shader.getPath() + ".json");
        EFFECTS.add(this);
    }

    public PostChain getPostChain() {

        return chain;
    }

    public boolean isEnabled() {

        return loaded;
    }

    public void begin(float partialTick) {

    }

    public void end(float partialTick) {

        if (isEnabled()) {
            chain.process(partialTick);
        }
    }

    public void apply(Window window) {

    }

    public void resize(int width, int height) {

        if (chain != null) {
            chain.resize(width, height);
        }
    }

    protected void onChainLoad() {

    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

        if (chain != null) {
            chain.close();
        }
        loaded = false;
        try {
            Minecraft mc = Minecraft.getInstance();
            chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), shader);
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            loaded = true;
            onChainLoad();
        } catch (IOException e) {
            throw new RuntimeException(e); //CoFHCore.LOG.warn("Failed to load shader: {}", shader, e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e); //CoFHCore.LOG.warn("Failed to parse shader: {}", shader, e);
        }
    }

    public static Collection<PostEffect> getAllEffects() {

        return Collections.unmodifiableCollection(EFFECTS);
    }

}
