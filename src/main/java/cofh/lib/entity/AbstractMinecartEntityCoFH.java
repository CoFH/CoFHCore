package cofh.lib.entity;

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

import static cofh.lib.util.constants.NBTTags.TAG_ENCHANTMENTS;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public abstract class AbstractMinecartEntityCoFH extends AbstractMinecartEntity {

    protected ListNBT enchantments = new ListNBT();

    protected AbstractMinecartEntityCoFH(EntityType<?> type, World worldIn) {

        super(type, worldIn);
    }

    protected AbstractMinecartEntityCoFH(EntityType<?> type, World worldIn, double posX, double posY, double posZ) {

        super(type, worldIn, posX, posY, posZ);
    }

    public AbstractMinecartEntityCoFH setEnchantments(ListNBT enchantments) {

        this.enchantments = enchantments;
        return this;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {

        super.readAdditional(compound);

        enchantments = compound.getList(TAG_ENCHANTMENTS, TAG_COMPOUND);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {

        super.writeAdditional(compound);

        compound.put(TAG_ENCHANTMENTS, enchantments);
    }

    @Override
    public void killMinecart(DamageSource source) {

        this.remove();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack stack = getCartItem();
            if (this.hasCustomName()) {
                stack.setDisplayName(this.getCustomName());
            }
            if (!this.enchantments.isEmpty()) {
                stack.setTagInfo(TAG_ENCHANTMENTS, enchantments);
            }
            this.entityDropItem(stack);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
