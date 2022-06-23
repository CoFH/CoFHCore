package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class FishingRodItemCoFH extends FishingRodItem implements ICoFHItem {

    protected static Random random = new Random();
    protected int enchantability = 1;
    protected int luckModifier;
    protected int speedModifier;

    public FishingRodItemCoFH(Properties builder) {

        super(builder);
    }

    public FishingRodItemCoFH(Tier tier, Properties builder) {

        super(builder);
        setParams(tier);
    }

    public FishingRodItemCoFH setParams(Tier tier) {

        enchantability = tier.getEnchantmentValue();
        luckModifier = tier.getLevel() / 2;
        speedModifier = (int) tier.getSpeed() / 3;
        return this;
    }

    public FishingRodItemCoFH setParams(int enchantability, int luckModifier, int speedModifier) {

        this.enchantability = enchantability;
        this.luckModifier = luckModifier;
        this.speedModifier = speedModifier;
        return this;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        if (playerIn.fishing != null) {
            if (!worldIn.isClientSide) {
                int i = playerIn.fishing.retrieve(stack);
                stack.hurtAndBreak(i, playerIn, (entity) -> {
                    entity.broadcastBreakEvent(handIn);
                });
            }
            playerIn.swing(handIn);
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        } else {
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isClientSide) {
                int luck = EnchantmentHelper.getFishingLuckBonus(stack) + luckModifier;
                int speed = EnchantmentHelper.getFishingSpeedBonus(stack) + speedModifier;
                worldIn.addFreshEntity(new FishingHook(playerIn, worldIn, luck, speed));
            }

            playerIn.swing(handIn);
            playerIn.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getEnchantmentValue() {

        return enchantability;
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected BooleanSupplier showInGroups = TRUE;
    protected String modId = "";

    @Override
    public ICoFHItem setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public ICoFHItem setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public ICoFHItem setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean() || displayGroup != null && displayGroup.get() != null && displayGroup.get() != group) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {

        return displayGroup != null && displayGroup.get() != null ? Collections.singletonList(displayGroup.get()) : super.getCreativeTabs();
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
