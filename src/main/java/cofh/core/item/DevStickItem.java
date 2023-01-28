package cofh.core.item;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.init.CoreParticles;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

public class DevStickItem extends ItemCoFH implements IEntityRayTraceItem, ITrackedItem {

    public DevStickItem(Properties builder) {

        super(builder);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Player player = context.getPlayer();
        Level level = context.getLevel();
        Vec3 pos = context.getClickLocation();
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());

        Random rand = new Random();
        //        if (level.isClientSide) {
        //            Vec3 start = pos;
        //            Vec3 end = pos.add(20, 20, 0);
        //            level.addParticle(new BiColorParticleOptions(CoreParticles.SHARD.get(), 1.0F, 500.0F, 30, 0xFF0000FF, 0x0000FFFF), start.x, start.y, start.z, end.x, end.y, end.z);
        //
        //            //Vec3 end = pos.add(5, 5, 0);
        //            //level.addParticle(new BiColorParticleOptions(CoreParticles.BEAM.get(), 1.0F, 1000.0F, rand.nextFloat(2.5F), 0xFF0000FF, 0x0000FFFF), pos.x, pos.y, pos.z, end.x, end.y, end.z);
        //            //for (int i = 0; i < 10; ++i) {
        //            //    level.addParticle(new CylindricalParticleOptions(CoreParticles.WIND_SPIRAL.get(), 1.0F, 10.0F, rand.nextFloat(2.5F), 2.0F), pos.x, pos.y, pos.z, 0, 0, 0);
        //            //}
        //            //level.addParticle(new ColorParticleOptions(CoreParticles.BLAST.get(), 1.0F, 60.0F, 0, 0xFF0000FF), pos.x, pos.y, pos.z, 0, 0, 0);
        //
        //        }
        //        return InteractionResult.sidedSuccess(level.isClientSide);
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        //player.startUsingItem(hand);
        //if (level.isClientSide) {
        //}
        //return InteractionResultHolder.consume(stack);
        //return InteractionResultHolder.pass(stack);
        //if (!level.isClientSide) {
        //    level.addFreshEntity(new Icicle(level, player, player.getEyePosition(), player.getLookAngle().scale(2.0F)));
        //}
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int durationRemaining) {

        int duration = getUseDuration(stack) - durationRemaining;
        Random rand = new Random();
        if (level.isClientSide) {
            if (duration % 6 == 0) {
                float spread = 0.01F; //MathHelper.clamp(MathHelper.sqrt(Math.max(duration * 0.05F - 0.2F, 0)), 0.01F, 1.5F);
                Vec3 start = living.getEyePosition();
                Vec3 look = living.getLookAngle();
                Vec3 end = start.add(look.scale(16)).add(rand.nextFloat(-spread, spread), rand.nextFloat(-spread, spread), rand.nextFloat(-spread, spread));

                BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                if (blockHit.getType() != HitResult.Type.MISS) {
                    end = blockHit.getLocation();
                }
                Optional<EntityHitResult> closest = ArcheryHelper.findHitEntities(level, null, start, end, 0.0F, EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR))
                        .min(Comparator.comparingDouble(result -> result.getLocation().distanceToSqr(start)));
                if (closest.isPresent()) {
                    end = closest.get().getLocation();
                    if (living instanceof Player player) {
                        sendEntityRayTrace(player, closest.get().getEntity(), start, end);
                    }
                }
                level.playLocalSound(start.x, start.y, start.z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.5F, 0.5F + spread * 0.2F + rand.nextFloat(0.1F), false);

                Vec3 pos = start.add(look.cross(new Vec3(0, 0.3, 0))).add(0, -0.1, 0).add(look.scale(0.35F));
                float time = (float) end.subtract(start).length() * 0.2F;
                int rgba = 0xac3ad0ff;
                level.addParticle(new BiColorParticleOptions(CoreParticles.SHARD.get(), 2.0F, time, 0, rgba, 0x7426a9ff), pos.x, pos.y, pos.z, end.x, end.y, end.z);
                level.addParticle(new ColorParticleOptions(CoreParticles.BLAST.get(), 1.0F, 4 + rand.nextInt(2), time,
                                new Color(rgba).scaleRGB(rand.nextFloat(0.85F, 1.15F)).pack()),
                        end.x, end.y, end.z, 0, 0, 0);

            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int durationRemaining) {

        if (living instanceof Player player) {
            player.getCooldowns().addCooldown(stack.getItem(), 10);
        }
        if (level.isClientSide) {

        }
    }

    @Override
    public void handleEntityRayTrace(Level level, ItemStack stack, Player player, Entity target, Vec3 origin, Vec3 hit) {

        if (target instanceof LivingEntity living) {
            int invuln = living.invulnerableTime;
            living.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addTransientModifier(new AttributeModifier(Constants.UUID_WEAPON_KNOCKBACK, "b", 1.0, AttributeModifier.Operation.ADDITION));
            living.invulnerableTime = 0;
            living.hurt(DamageSource.playerAttack(player), 6F);
            living.invulnerableTime = invuln;
            living.getAttribute(Attributes.KNOCKBACK_RESISTANCE).removeModifier(Constants.UUID_WEAPON_KNOCKBACK);
            //            float time = (float) hit.subtract(origin).length() * 0.25F;
            //            ((ServerLevel) level).sendParticles(new BiColorParticleOptions(CoreParticles.SHARD.get(), 1.0F, time, 0, 0xac3ad0ff, 0x7426a9ff), origin.x, origin.y, origin.z, 0, hit.x, hit.y, hit.z, 1.0F);

        }
    }

    @Override
    public void onSwapFrom(Player player, InteractionHand hand, ItemStack stack, int duration) {

        Item item = stack.getItem();
        if (!player.getCooldowns().isOnCooldown(item) && duration > 0) {
            player.getCooldowns().addCooldown(item, 10);
        }
    }

}
