package cofh.core.potion;

import cofh.lib.capability.CapabilityRedstoneFlux;
import cofh.lib.potion.EffectCoFH;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyChargeEffect extends EffectCoFH {

    private final int amount;

    public EnergyChargeEffect(EffectType typeIn, int liquidColorIn, int amount) {

        super(typeIn, liquidColorIn);
        this.amount = amount;
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        if (entityLivingBaseIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity entity = (ServerPlayerEntity) entityLivingBaseIn;

            if (amount <= 0) {
                drainForgeEnergy(entity, amount);
                drainRedstoneFlux(entity, amount);
            } else {
                chargeForgeEnergy(entity, amount);
                chargeRedstoneFlux(entity, amount);
            }
        }
    }

    // region HELPERS
    private void chargeForgeEnergy(ServerPlayerEntity entity, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : entity.inventory.items) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : entity.inventory.armor) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : entity.inventory.offhand) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void chargeRedstoneFlux(ServerPlayerEntity entity, final int chargeAmount) {

        // Main Inventory
        for (ItemStack stack : entity.inventory.items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : entity.inventory.armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
        // Offhand
        for (ItemStack stack : entity.inventory.offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.receiveEnergy(chargeAmount, false));
        }
    }

    private void drainForgeEnergy(ServerPlayerEntity entity, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : entity.inventory.items) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : entity.inventory.armor) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : entity.inventory.offhand) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }

    private void drainRedstoneFlux(ServerPlayerEntity entity, final int drainAmount) {

        // Main Inventory
        for (ItemStack stack : entity.inventory.items) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Armor Inventory
        for (ItemStack stack : entity.inventory.armor) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
        // Offhand
        for (ItemStack stack : entity.inventory.offhand) {
            stack.getCapability(CapabilityRedstoneFlux.RF_ENERGY, null)
                    .ifPresent(c -> c.extractEnergy(drainAmount, false));
        }
    }
    // endregion
}
