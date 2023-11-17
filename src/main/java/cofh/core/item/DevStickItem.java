package cofh.core.item;

import cofh.lib.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

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
        if (level.isClientSide) {
        } else {
            System.out.println("biome modifiers");
            final RegistryAccess registries = level.getServer().registryAccess();
            // The order of holders() is the order modifiers were loaded in.
            registries.registryOrThrow(ForgeRegistries.Keys.BIOME_MODIFIERS)
                    .holders()
                    .forEach(holder -> holder.unwrapKey().ifPresent(key -> System.out.println(key.location())));
        }
        //if (level.isClientSide()) {
        //    Vec3 pos = player.getEyePosition();
        //    Vec3 to = player.getEyePosition().add(10, 0, 0);
        //    level.addParticle(new BiColorParticleOptions(STREAM.get(), 6, 200, 0, 0xFFFFFFFF, 0xFFFFFFFF), pos.x, pos.y, pos.z, to.x, to.y, to.z);
        //}
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
    public void handleEntityRayTrace(Level level, Player player, InteractionHand hand, ItemStack stack, Vec3 origin, Entity target, Vec3 hit) {

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

    //@Override
    //public void onSwapFrom(Player player, InteractionHand hand, ItemStack stack, int duration) {
    //
    //    Item item = stack.getItem();
    //    if (!player.getCooldowns().isOnCooldown(item) && duration > 0) {
    //        player.getCooldowns().addCooldown(item, 10);
    //    }
    //}

}
