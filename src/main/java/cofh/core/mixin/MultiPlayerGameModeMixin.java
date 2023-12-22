package cofh.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO 1.21 Remove
@Mixin (MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow
    public Minecraft minecraft;

    @Shadow
    public BlockPos destroyBlockPos;

    @Shadow
    public ItemStack destroyingItem;

    @Inject (
            method = "sameDestroyTarget(Lnet/minecraft/core/BlockPos;)Z",
            at = @At ("HEAD"),
            cancellable = true
    )
    private void sameDestroyTargetFix(BlockPos pPos, CallbackInfoReturnable<Boolean> callback) {

        ItemStack itemstack = this.minecraft.player.getMainHandItem();
        callback.setReturnValue(pPos.equals(this.destroyBlockPos) && !destroyingItem.shouldCauseBlockBreakReset(itemstack));
    }

}
