package cofh.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Map;
import java.util.function.Supplier;

import static cofh.core.util.references.CoreIDs.ID_HOLDING;
import static cofh.lib.util.Utils.getEnchantment;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.constants.NBTTags.TAG_ENCHANTMENTS;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

public class BoatCoFH extends Boat implements IOnPlaced {

    protected ListTag enchantments = new ListTag();

    public BoatCoFH(EntityType<? extends Boat> type, Level worldIn) {

        super(type, worldIn);
    }

    public BoatCoFH(Supplier<EntityType<? extends Boat>> type, Level worldIn, double posX, double posY, double posZ) {

        this(type.get(), worldIn);
        this.setPos(posX, posY, posZ);
        this.xo = posX;
        this.yo = posY;
        this.zo = posZ;
    }

    public BoatCoFH onPlaced(ItemStack stack) {

        this.enchantments = stack.getEnchantmentTags();
        return this;
    }

    protected float getHoldingMod(Map<Enchantment, Integer> enchantmentMap) {

        int holding = enchantmentMap.getOrDefault(getEnchantment(ID_COFH_CORE, ID_HOLDING), 0);
        return 1 + holding / 2F;
    }

    public ItemStack createItemStackTag(ItemStack stack) {

        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        if (!this.enchantments.isEmpty()) {
            stack.addTagElement(TAG_ENCHANTMENTS, enchantments);
        }
        return stack;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {

        super.readAdditionalSaveData(compound);

        enchantments = compound.getList(TAG_ENCHANTMENTS, TAG_COMPOUND);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {

        super.addAdditionalSaveData(compound);

        compound.put(TAG_ENCHANTMENTS, enchantments);
    }

    @Override
    public void destroy(DamageSource source) {

        this.remove(Entity.RemovalReason.KILLED);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = createItemStackTag(getPickResult());
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
