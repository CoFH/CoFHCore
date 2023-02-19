package cofh.core.client.particle.impl;

import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class FireParticle extends SpriteParticle {

    protected int baseAlpha;

    private FireParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        gravity = -0.3F;
        friction = 0.9F;
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        baseAlpha = data.rgba0 & 0xFF;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        float easeCos = MathHelper.cos(progress * 0.5F * MathHelper.F_PI);
        //int rgba = VFXHelper.mix(1.0F - easeCos, 0xe9fa50ff, 0xf2461bff, 0xcc0f02ff, 0x363534ff);
        //this.rCol = ((rgba >> 24) & 0xFF) * 0.0039215686F;
        //this.gCol = ((rgba >> 16) & 0xFF) * 0.0039215686F;
        //this.bCol = ((rgba >> 8) & 0xFF) * 0.0039215686F;
        setColor0(Color.fromRGBA(c0.r, c0.g, c0.b, (int) (baseAlpha * easeCub)));

        //Only set render size based off BB size
        this.size = this.bbWidth * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
        super.render(stack, buffer, consumer, packedLight, time, pTicks);
    }

    @Override
    public void render(VertexConsumer consumer, Camera cam, float partialTicks) {

        super.render(consumer, cam, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_ADDITIVE_MUTLIPLY;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return 0x00F000F0;
    }

    @Override
    public void tick() {

        if (this.age > this.delay) {
            int max = MathHelper.ceil(this.duration * 128);
            int time = MathHelper.clamp(MathHelper.floor((this.age - this.delay) * 128), 0, max);
            this.sprite = sprites.get(time, max);
            super.tick();
        } else {
            ++age;
        }
    }

    @Override
    public void move(double dx, double dy, double dz) {

        if (dx == 0.0D && dy == 0.0D && dz == 0.0D) {
            return;
        }
        Vec3 start = new Vec3(x, y, z);
        Vec3 velocity = new Vec3(dx, dy, dz);
        Vec3 end = start.add(dx, dy, dz);
        if (hasPhysics) {
            BlockHitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
            if (!level.getFluidState(result.getBlockPos()).isEmpty()) {
                this.remove();
                return;
            }
            if (result.getType() != HitResult.Type.MISS) {
                Vec3 loc = result.getLocation();
                Vec3 remaining = end.subtract(loc).scale(0.2F);
                Direction face = result.getDirection();
                Direction.Axis axis = face.getAxis();
                velocity = velocity.with(axis, 0).scale(0.3F);
                end = loc.add(remaining.with(axis, face.getAxisDirection().getStep() * 0.05F));
            }
        }
        end = end.subtract(start);
        if (!end.equals(Vec3.ZERO)) {
            this.setBoundingBox(this.getBoundingBox().move(end));
            this.setLocationFromBoundingbox();
        }
        this.xd = velocity.x;
        this.yd = velocity.y;
        this.zd = velocity.z;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new FireParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
