package cofh.lib.entity;

<<<<<<< HEAD
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
=======
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

import java.util.Map;

import static cofh.lib.util.constants.NBTTags.TAG_ENCHANTMENTS;
<<<<<<< HEAD
import static cofh.lib.util.references.CoreReferences.HOLDING;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;
=======
import static net.minecraft.nbt.Tag.TAG_COMPOUND;
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

public abstract class AbstractMinecartEntityCoFH extends AbstractMinecart {

    protected ListTag enchantments = new ListTag();

    protected AbstractMinecartEntityCoFH(EntityType<?> type, Level worldIn) {

        super(type, worldIn);
    }

    protected AbstractMinecartEntityCoFH(EntityType<?> type, Level worldIn, double posX, double posY, double posZ) {

        super(type, worldIn, posX, posY, posZ);
    }

<<<<<<< HEAD
    public AbstractMinecartEntityCoFH onPlaced(ItemStack stack) {
=======
    public AbstractMinecartEntityCoFH setEnchantments(ListTag enchantments) {
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

        this.enchantments = stack.getEnchantmentTags();
        return this;
    }

    protected float getHoldingMod(Map<Enchantment, Integer> enchantmentMap) {

        int holding = enchantmentMap.getOrDefault(HOLDING, 0);
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
<<<<<<< HEAD
            ItemStack stack = createItemStackTag(getCartItem());
=======
            ItemStack stack = getPickResult();
            if (this.hasCustomName()) {
                stack.setHoverName(this.getCustomName());
            }
            if (!this.enchantments.isEmpty()) {
                stack.addTagElement(TAG_ENCHANTMENTS, enchantments);
            }
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
