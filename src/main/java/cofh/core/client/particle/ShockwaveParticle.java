package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class ShockwaveParticle extends CustomRenderParticle {

    protected float heightScale;
    protected float speed;

    private ShockwaveParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double radius, double heightScale) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, radius, speed, heightScale);
        this.lifetime = MathHelper.ceil((radius + 6) / speed);
        this.speed = (float) speed;
        this.setSize((float) radius * 2, (float) heightScale); //TODO: bbheight?
        this.heightScale = (float) heightScale;

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float partialTicks) {

        VFXHelper.renderShockwave(stack, buffer, level, new BlockPos(x, y, z), (age + partialTicks) * speed, bbWidth * 0.5F, heightScale);
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double speed, double radius, double heightScale) {

            return new ShockwaveParticle(level, x, y, z, speed, radius, heightScale);
        }

    }

}
