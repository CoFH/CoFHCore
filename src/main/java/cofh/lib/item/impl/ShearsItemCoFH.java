package cofh.lib.item.impl;

import cofh.lib.item.ICoFHItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.BeehiveDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ShearsItemCoFH extends ShearsItem implements ICoFHItem {

    protected int enchantability = 0;

    public ShearsItemCoFH(Properties builder) {

        super(builder);
        DispenserBlock.registerDispenseBehavior(this, new BeehiveDispenseBehavior());
    }

    public ShearsItemCoFH(IItemTier tier, Properties builder) {

        this(builder);
        setParams(tier);
    }

    public ShearsItemCoFH setParams(IItemTier tier) {

        this.enchantability = tier.getEnchantability();
        return this;
    }

    @Override
    public int getItemEnchantability() {

        return enchantability;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {

        return true;
    }

}
