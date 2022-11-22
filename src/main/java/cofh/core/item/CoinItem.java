package cofh.core.item;

import cofh.core.util.ProxyUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static cofh.lib.util.helpers.StringHelper.localize;

public class CoinItem extends CountedItem {

    public CoinItem(Properties builder) {

        super(builder);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        int count = stack.getCount();
        int heads = 0;

        if (worldIn.isClientSide) {
            if (count == 1) {
                if (worldIn.getRandom().nextBoolean()) {
                    ProxyUtils.setOverlayMessage(playerIn, Component.translatable("info.cofh.heads"));
                } else {
                    ProxyUtils.setOverlayMessage(playerIn, Component.translatable("info.cofh.tails"));
                }
            } else {
                for (int i = 0; i < count; ++i) {
                    heads += worldIn.getRandom().nextInt(2);
                }
                ProxyUtils.setOverlayMessage(playerIn, Component.translatable(localize("info.cofh.heads") + ": " + heads + " " + localize("info.cofh.tails") + ": " + (count - heads)));
            }
        }
        playerIn.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

}
