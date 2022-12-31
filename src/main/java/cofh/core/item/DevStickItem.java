package cofh.core.item;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.init.CoreParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DevStickItem extends ItemCoFH {

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
        if (level.isClientSide) {
            Vec3 end = pos.add(5, 5, 0);
            //level.addParticle(new BiColorParticleOptions(CoreParticles.BEAM.get(), 1.0F, 1000.0F, rand.nextFloat(2.5F), 0xFF0000FF, 0x0000FFFF), pos.x, pos.y, pos.z, end.x, end.y, end.z);
            //for (int i = 0; i < 10; ++i) {
            //    level.addParticle(new CylindricalParticleOptions(CoreParticles.WIND_SPIRAL.get(), 1.0F, 10.0F, rand.nextFloat(2.5F), 2.0F), pos.x, pos.y, pos.z, 0, 0, 0);
            //}
            level.addParticle(new ColorParticleOptions(CoreParticles.MIST.get(), 1.0F, 60.0F, 20, 0xFF0000FF), pos.x, pos.y, pos.z, 0, 0, 0);

        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int durationRemaining) {

        if (level.isClientSide) {
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int durationRemaining) {

        if (level.isClientSide) {
        }
    }
}
