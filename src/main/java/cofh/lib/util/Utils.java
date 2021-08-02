package cofh.lib.util;

import cofh.lib.enchantment.EnchantmentCoFH;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.references.ItemTagsCoFH;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

import static cofh.lib.util.constants.Constants.MAX_CAPACITY;
import static cofh.lib.util.constants.NBTTags.TAG_ENCHANTMENTS;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.enchantment.EnchantmentHelper.getMaxEnchantmentLevel;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;
import static net.minecraftforge.common.util.Constants.NBT.TAG_LIST;

public class Utils {

    private Utils() {

    }

    public static boolean isModLoaded(String modid) {

        return ModList.get().isLoaded(modid);
    }

    public static boolean isClientWorld(World world) {

        return world.isRemote;
    }

    public static boolean isServerWorld(World world) {

        return !world.isRemote;
    }

    public static boolean isFakePlayer(Entity entity) {

        return entity instanceof FakePlayer;
    }

    public static String createPrettyJSON(String jsonString) {

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

    public static boolean spawnLightningBolt(World world, BlockPos pos) {

        return spawnLightningBolt(world, pos, null);
    }

    public static boolean spawnLightningBolt(World world, BlockPos pos, Entity caster) {

        if (Utils.isServerWorld(world)) {
            LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
            bolt.moveForced(Vector3d.copyCenteredHorizontally(pos));
            bolt.setCaster(caster instanceof ServerPlayerEntity ? (ServerPlayerEntity) caster : null);
            world.addEntity(bolt);
        }
        return true;
    }

    public static boolean destroyBlock(World world, BlockPos pos, boolean dropBlock, @Nullable Entity entityIn) {

        BlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos) || state.getBlockHardness(world, pos) < 0 || (entityIn instanceof PlayerEntity && state.getPlayerRelativeBlockHardness((PlayerEntity) entityIn, world, pos) < 0)) {
            return false;
        } else {
            FluidState ifluidstate = world.getFluidState(pos);
            if (dropBlock) {
                TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
                Block.spawnDrops(state, world, pos, tileentity, entityIn, ItemStack.EMPTY);
            }
            return world.setBlockState(pos, ifluidstate.getBlockState(), 3);
        }
    }

    public static boolean isWrench(Item item) {

        return item.isIn(ItemTagsCoFH.TOOLS_WRENCH);
    }

    public static boolean hasBiomeType(World world, BlockPos pos, BiomeDictionary.Type type) {

        Optional<MutableRegistry<Biome>> biomeReg = world.func_241828_r().func_230521_a_(Registry.BIOME_KEY);
        if (biomeReg.isPresent()) {
            Optional<RegistryKey<Biome>> biomeKey = biomeReg.get().getOptionalKey(world.getBiome(pos));
            if (biomeKey.isPresent()) {
                return BiomeDictionary.hasType(biomeKey.get(), type);
            }
        }
        return false;
    }

    // region TIME CHECKS
    public static final int TIME_CONSTANT = 32;
    public static final int TIME_CONSTANT_2X = TIME_CONSTANT * 2;

    public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
    public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;
    public static final int TIME_CONSTANT_EIGHTH = TIME_CONSTANT / 8;

    public static boolean timeCheck(World world) {

        return world.getGameTime() % TIME_CONSTANT == 0;
    }

    public static boolean timeCheckHalf(World world) {

        return world.getGameTime() % TIME_CONSTANT_HALF == 0;
    }

    public static boolean timeCheckQuarter(World world) {

        return world.getGameTime() % TIME_CONSTANT_QUARTER == 0;
    }

    public static boolean timeCheckEighth(World world) {

        return world.getGameTime() % TIME_CONSTANT_EIGHTH == 0;
    }
    // endregion

    // region PARTICLE UTILS
    public static void spawnBlockParticlesClient(World world, IParticleData particle, BlockPos pos, Random rand, int count) {

        for (int i = 0; i < count; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble();
            double d2 = (double) pos.getZ() + rand.nextDouble();
            double d3 = (rand.nextDouble() - 0.5D) * 0.5D;
            double d4 = (rand.nextDouble() - 0.5D) * 0.5D;
            double d5 = (rand.nextDouble() - 0.5D) * 0.5D;
            world.addParticle(particle, d0, d1, d2, d3, d4, d5);
        }
    }

    public static void spawnParticles(World world, IParticleData particle, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {

        if (isServerWorld(world)) {
            ((ServerWorld) world).spawnParticle(particle, posX, posY + 1.0D, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        } else {
            world.addParticle(particle, posX + xOffset, posY + yOffset, posZ + zOffset, 0.0D, 0.0D, 0.0D);
        }
    }
    // endregion

    // region ENTITY UTILS
    public static boolean addToPlayerInventory(PlayerEntity player, ItemStack stack) {

        if (stack.isEmpty() || player == null) {
            return false;
        }
        if (stack.getItem() instanceof ArmorItem) {
            int index = ((ArmorItem) stack.getItem()).getEquipmentSlot().getIndex();
            if (player.inventory.armorInventory.get(index).isEmpty()) {
                player.inventory.armorInventory.set(index, stack);
                return true;
            }
        }
        PlayerInventory inv = player.inventory;
        for (int i = 0; i < inv.mainInventory.size(); ++i) {
            if (inv.mainInventory.get(i).isEmpty()) {
                inv.mainInventory.set(i, stack.copy());
                return true;
            }
        }
        return false;
    }

    public static boolean addPotionEffectNoEvent(LivingEntity entity, EffectInstance effectInstanceIn) {

        if (!isPotionApplicableNoEvent(entity, effectInstanceIn)) {
            return false;
        } else {
            EffectInstance effectinstance = entity.getActivePotionMap().get(effectInstanceIn.getPotion());
            if (effectinstance == null) {
                entity.getActivePotionMap().put(effectInstanceIn.getPotion(), effectInstanceIn);
                entity.onNewPotionEffect(effectInstanceIn);
                return true;
            } else if (effectinstance.combine(effectInstanceIn)) {
                entity.onChangedPotionEffect(effectinstance, true);
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isPotionApplicableNoEvent(LivingEntity entity, EffectInstance potioneffectIn) {

        if (entity.getCreatureAttribute() == CreatureAttribute.UNDEAD) {
            Effect effect = potioneffectIn.getPotion();
            return effect != Effects.REGENERATION && effect != Effects.POISON;
        }
        return true;
    }

    public static boolean dropItemStackIntoWorld(ItemStack stack, World world, Vector3d pos) {

        return dropItemStackIntoWorld(stack, world, pos, false);
    }

    public static boolean dropItemStackIntoWorldWithRandomness(ItemStack stack, World world, BlockPos pos) {

        return dropItemStackIntoWorld(stack, world, Vector3d.copyCentered(pos), true);
    }

    public static boolean dropItemStackIntoWorldWithRandomness(ItemStack stack, World world, Vector3d pos) {

        return dropItemStackIntoWorld(stack, world, pos, true);
    }

    public static boolean dropItemStackIntoWorld(ItemStack stack, World world, Vector3d pos, boolean velocity) {

        if (stack.isEmpty()) {
            return false;
        }
        float x2 = 0.5F;
        float y2 = 0.0F;
        float z2 = 0.5F;

        if (velocity) {
            x2 = world.rand.nextFloat() * 0.8F + 0.1F;
            y2 = world.rand.nextFloat() * 0.8F + 0.1F;
            z2 = world.rand.nextFloat() * 0.8F + 0.1F;
        }
        ItemEntity entity = new ItemEntity(world, pos.x + x2, pos.y + y2, pos.z + z2, stack.copy());

        if (velocity) {
            entity.setMotion(world.rand.nextGaussian() * 0.05F, world.rand.nextGaussian() * 0.05F + 0.2F, world.rand.nextGaussian() * 0.05F);
        } else {
            entity.setMotion(-0.05, 0, 0);
        }
        world.addEntity(entity);

        return true;
    }

    public static boolean dropDismantleStackIntoWorld(ItemStack stack, World world, BlockPos pos) {

        if (stack.isEmpty()) {
            return false;
        }
        float f = 0.3F;
        double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        ItemEntity dropEntity = new ItemEntity(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, stack);
        dropEntity.setPickupDelay(10);
        world.addEntity(dropEntity);

        return true;
    }

    public static boolean teleportEntityTo(Entity entity, BlockPos pos) {

        return teleportEntityTo(entity, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    public static boolean teleportEntityTo(Entity entity, double x, double y, double z) {

        if (entity instanceof LivingEntity) {
            return teleportEntityTo((LivingEntity) entity, x, y, z);
        } else {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
        return true;
    }

    public static boolean teleportEntityTo(LivingEntity entity, double x, double y, double z) {

        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        if (entity instanceof ServerPlayerEntity && !isFakePlayer(entity)) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            if (player.connection.getNetworkManager().isChannelOpen() && !player.isSleeping()) {
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }
                entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                entity.fallDistance = 0.0F;
            }
        } else {
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            entity.fallDistance = 0.0F;
        }
        entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        return true;
    }
    // endregion

    // region ENCHANT UTILS
    public static int getEnchantedCapacity(int amount, int holding) {

        return MathHelper.clamp(amount + amount * holding / 2, 0, MAX_CAPACITY);
    }

    public static int getItemEnchantmentLevel(Enchantment ench, ItemStack stack) {

        if (ench == null || ench instanceof EnchantmentCoFH && !((EnchantmentCoFH) ench).isEnabled()) {
            return 0;
        }
        return getEnchantmentLevel(ench, stack);
    }

    public static int getHeldEnchantmentLevel(LivingEntity living, Enchantment ench) {

        if (ench == null || ench instanceof EnchantmentCoFH && !((EnchantmentCoFH) ench).isEnabled()) {
            return 0;
        }
        return Math.max(getEnchantmentLevel(ench, living.getHeldItemMainhand()), getEnchantmentLevel(ench, living.getHeldItemOffhand()));
    }

    public static int getMaxEquippedEnchantmentLevel(LivingEntity living, Enchantment ench) {

        if (ench == null || ench instanceof EnchantmentCoFH && !((EnchantmentCoFH) ench).isEnabled()) {
            return 0;
        }
        return getMaxEnchantmentLevel(ench, living);
    }

    public static void addEnchantment(ItemStack stack, Enchantment ench, int level) {

        stack.addEnchantment(ench, level);
    }

    public static void removeEnchantment(ItemStack stack, Enchantment ench) {

        if (stack.getTag() == null || !stack.getTag().contains(TAG_ENCHANTMENTS, TAG_LIST)) {
            return;
        }
        ListNBT list = stack.getTag().getList(TAG_ENCHANTMENTS, TAG_COMPOUND);
        String encId = String.valueOf(ForgeRegistries.ENCHANTMENTS.getKey(ench));

        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);
            String id = tag.getString("id");
            if (encId.equals(id)) {
                list.remove(i);
                break;
            }
        }
        if (list.isEmpty()) {
            stack.removeChildTag(TAG_ENCHANTMENTS);
        }
    }
    // endregion

    // region NAMESPACE
    public static String getItemNamespace(Item item) {

        return item.getRegistryName() == null ? "" : item.getRegistryName().getNamespace();
    }

    public static String getItemNamespace(ItemStack stack) {

        return stack.getItem().getRegistryName() == null ? "" : stack.getItem().getRegistryName().getNamespace();
    }

    public static String getFluidNamespace(Fluid fluid) {

        return fluid.getRegistryName() == null ? "" : fluid.getRegistryName().getNamespace();
    }

    public static String getFluidNamespace(FluidStack stack) {

        return stack.getFluid().getRegistryName() == null ? "" : stack.getFluid().getRegistryName().getNamespace();
    }
    // endregion
}
