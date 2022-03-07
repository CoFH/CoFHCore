package cofh.lib.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

@Deprecated // This doesn't have much point in existing now? Maybe? Should be removed?
public class SpriteRendererCoFH<T extends Entity & ItemSupplier> extends ThrownItemRenderer<T> {

    public SpriteRendererCoFH(EntityRendererProvider.Context ctx, float scale, boolean fullBright) {

        super(ctx, scale, fullBright);
    }

}
