package cofh.lib.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;

public class SpriteRendererCoFH<T extends Entity & IRendersAsItem> extends SpriteRenderer<T> {

    public SpriteRendererCoFH(EntityRendererManager renderManagerIn) {

        super(renderManagerIn, Minecraft.getInstance().getItemRenderer());
    }

}
