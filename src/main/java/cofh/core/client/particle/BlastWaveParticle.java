package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BlastWaveParticle extends LevelMatrixStackParticle {

    protected float heightScale;
    protected float speed;

    private BlastWaveParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double radius, double heightScale) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, radius, speed, heightScale);
        this.lifetime = MathHelper.ceil(radius / speed);
        this.speed = (float) speed;
        this.setSize((float) radius * 2, (float) heightScale);
        this.heightScale = (float) heightScale;

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float partialTicks) {

        float time = age + partialTicks;
        float progress = time / lifetime;
        float remaining = 1.0F - progress;
        float scale = 1.0F - remaining * remaining * remaining;
        VFXHelper.renderCyclone(stack, buffer, getLightColor(partialTicks), bbWidth * 0.5F * scale, heightScale * scale, 3, 0.3F, time, 0.5F * (1.0F - progress * progress * progress));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double radius, double heightScale) {

            return new BlastWaveParticle(world, x, y, z, speed, radius, heightScale);
        }
    }

}
