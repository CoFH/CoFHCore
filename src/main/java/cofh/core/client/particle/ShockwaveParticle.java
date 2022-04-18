package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class ShockwaveParticle extends CustomRenderParticle {

    protected float heightScale;
    protected float speed;

    private ShockwaveParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double radius, double heightScale) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, radius, speed, heightScale);
        this.lifetime = MathHelper.ceil((radius + 6) / speed);
        this.speed = (float) speed;
        this.setSize((float) radius * 2, (float) heightScale); //TODO: bbheight?
        this.heightScale = (float) heightScale;

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float partialTicks) {

        VFXHelper.renderShockwave(stack, buffer, level, new BlockPos(x, y, z), (age + partialTicks) * speed, bbWidth * 0.5F, heightScale);
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double radius, double heightScale) {

            return new ShockwaveParticle(world, x, y, z, speed, radius, heightScale);
        }

    }

}
