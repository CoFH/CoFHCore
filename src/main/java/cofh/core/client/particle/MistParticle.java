package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class MistParticle extends TextureSheetParticle {

    private MistParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 40;

        float var = 0.05F;
        xd = xSpeedIn + random.nextFloat(-var, var);
        yd = ySpeedIn + random.nextFloat(-var, var);
        zd = zSpeedIn + random.nextFloat(-var, var);

        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        scale(random.nextFloat(3.0F, 9.0F));
    }

    @Override
    public void render(VertexConsumer consumer, Camera info, float partialTicks) {

        float progress = (age + partialTicks) / lifetime;
        float q = 2 * progress - 1;
        q *= q;
        this.alpha = Math.max(0.2F * (1 - q * q) * MathHelper.cos(0.25F * MathHelper.F_PI * progress), 0);
        this.quadSize = this.bbWidth * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
        super.render(consumer, info, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_TRANSLUCENT_BLEND;
    }

    @Nonnull
    public static ParticleProvider<SimpleParticleType> iceMist(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> {
            MistParticle p = new MistParticle(level, x, y, z, dx, dy, dz);
            p.pickSprite(spriteSet);
            p.rCol = 0.5F - p.random.nextFloat(0.1F);
            p.gCol = 0.8F - p.random.nextFloat(0.1F);
            return p;
        };
    }

}
