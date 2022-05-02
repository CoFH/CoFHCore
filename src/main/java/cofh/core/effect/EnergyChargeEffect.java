package cofh.core.effect;

import cofh.lib.capability.CapabilityRedstoneFlux;
import cofh.lib.effect.EffectCoFH;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyChargeEffect extends EffectCoFH {

    private final int amount;

    public EnergyChargeEffect(MobEffectCategory typeIn, int liquidColorIn, int amount) {

        super(typeIn, liquidColorIn);
        this.amount = amount;
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        if (entityLivingBaseIn instanceof ServerPlayer player) {

            if (amount <= 0) {
                drainForgeEnergy(player, amount);
                drainRedstoneFlux(player, amount);
            } else {
                chargeForgeEnergy(player, amount);
                chargeRedstoneFlux(player, amount);
            }
        }
    }

    // region HELPERS
    private void chargeForgeEnergy(ServerPlayer player, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void chargeRedstoneFlux(ServerPlayer player, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void drainForgeEnergy(ServerPlayer player, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }

    private void drainRedstoneFlux(ServerPlayer player, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : player.getInventory().armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }
    // endregion
}
