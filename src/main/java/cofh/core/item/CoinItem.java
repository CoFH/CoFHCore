package cofh.core.item;

import cofh.core.util.helpers.ChatHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static cofh.lib.util.helpers.StringHelper.localize;

public class CoinItem extends CountedItem {

    public CoinItem(Properties builder) {

        super(builder);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        int count = stack.getCount();
        int heads = 0;

        if (worldIn.isClientSide) {
            if (count == 1) {
                if (worldIn.getRandom().nextBoolean()) {
                    ChatHelper.sendIndexedChatMessageToPlayer(playerIn, new TranslationTextComponent("info.cofh.heads"));
                } else {
                    ChatHelper.sendIndexedChatMessageToPlayer(playerIn, new TranslationTextComponent("info.cofh.tails"));
                }
            } else {
                for (int i = 0; i < count; ++i) {
                    heads += worldIn.getRandom().nextInt(2);
                }
                ChatHelper.sendIndexedChatMessageToPlayer(playerIn, new TranslationTextComponent(localize("info.cofh.heads") + ": " + heads + " " + localize("info.cofh.tails") + ": " + (count - heads)));
            }
        }
        playerIn.getCooldowns().addCooldown(this, 40);
        return ActionResult.pass(playerIn.getItemInHand(handIn));
    }

}
