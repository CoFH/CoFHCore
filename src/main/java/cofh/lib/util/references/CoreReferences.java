package cofh.lib.util.references;

import cofh.core.block.entity.EnderAirTile;
import cofh.core.block.entity.GlowAirTile;
import cofh.core.block.entity.LightningAirTile;
import cofh.core.block.entity.SignalAirTile;
import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.core.inventory.container.TileItemFilterContainer;
import cofh.lib.entity.BlackHole;
import cofh.lib.entity.ElectricArc;
import cofh.lib.entity.ElectricField;
import cofh.lib.entity.Knife;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.ObjectHolder;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreIDs.*;

@ObjectHolder (ID_COFH_CORE)
public class CoreReferences {

    private CoreReferences() {

    }

    // region BLOCKS
    @ObjectHolder (ID_GLOSSED_MAGMA)
    public static final Block GLOSSED_MAGMA = null;

    @ObjectHolder (ID_SIGNAL_AIR)
    public static final Block SIGNAL_AIR = null;

    @ObjectHolder (ID_GLOW_AIR)
    public static final Block GLOW_AIR = null;

    @ObjectHolder (ID_ENDER_AIR)
    public static final Block ENDER_AIR = null;

    @ObjectHolder (ID_LIGHTNING_AIR)
    public static final Block LIGHTNING_AIR = null;
    // endregion

    // region CONTAINERS
    //    @ObjectHolder(ID_CONTAINER_HELD_FLUID_FILTER)
    //    public static final ContainerType<HeldFluidFilterContainer> HELD_FLUID_FILTER_CONTAINER = null;
    //
    //    @ObjectHolder(ID_CONTAINER_TILE_FLUID_FILTER)
    //    public static final ContainerType<TileFluidFilterContainer> TILE_FLUID_FILTER_CONTAINER = null;

    @ObjectHolder (ID_CONTAINER_HELD_ITEM_FILTER)
    public static final MenuType<HeldItemFilterContainer> HELD_ITEM_FILTER_CONTAINER = null;

    @ObjectHolder (ID_CONTAINER_TILE_ITEM_FILTER)
    public static final MenuType<TileItemFilterContainer> TILE_ITEM_FILTER_CONTAINER = null;
    // endregion

    // region FLUIDS
    @ObjectHolder (ID_FLUID_HONEY)
    public static final FlowingFluid FLUID_HONEY = null;

    @ObjectHolder (ID_FLUID_MILK)
    public static final FlowingFluid FLUID_MILK = null;

    @ObjectHolder (ID_FLUID_POTION)
    public static final FlowingFluid FLUID_POTION = null;

    @ObjectHolder (ID_FLUID_STEAM)
    public static final FlowingFluid FLUID_STEAM = null;

    @ObjectHolder (ID_FLUID_XP)
    public static final FlowingFluid FLUID_XP = null;
    // endregion

    // region TILES
    @ObjectHolder (ID_SIGNAL_AIR)
    public static final BlockEntityType<SignalAirTile> SIGNAL_AIR_TILE = null;

    @ObjectHolder (ID_GLOW_AIR)
    public static final BlockEntityType<GlowAirTile> GLOW_AIR_TILE = null;

    @ObjectHolder (ID_ENDER_AIR)
    public static final BlockEntityType<EnderAirTile> ENDER_AIR_TILE = null;

    @ObjectHolder (ID_LIGHTNING_AIR)
    public static final BlockEntityType<LightningAirTile> LIGHTNING_AIR_TILE = null;
    // endregion

    // region EFFECTS
    @ObjectHolder (ID_EFFECT_EXPLOSION_RESISTANCE)
    public static final MobEffect EXPLOSION_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_LIGHTNING_RESISTANCE)
    public static final MobEffect LIGHTNING_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_MAGIC_RESISTANCE)
    public static final MobEffect MAGIC_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_AMPLIFICATION)
    public static final MobEffect AMPLIFICATION = null;

    @ObjectHolder (ID_EFFECT_CHILLED)
    public static final MobEffect CHILLED = null;

    @ObjectHolder (ID_EFFECT_CLARITY)
    public static final MobEffect CLARITY = null;

    @ObjectHolder (ID_EFFECT_ENDERFERENCE)
    public static final MobEffect ENDERFERENCE = null;

    @ObjectHolder (ID_EFFECT_LOVE)
    public static final MobEffect LOVE = null;

    @ObjectHolder (ID_EFFECT_PANACEA)
    public static final MobEffect PANACEA = null;

    @ObjectHolder (ID_EFFECT_SHOCKED)
    public static final MobEffect SHOCKED = null;

    @ObjectHolder (ID_EFFECT_SLIMED)
    public static final MobEffect SLIMED = null;

    @ObjectHolder (ID_EFFECT_SUNDERED)
    public static final MobEffect SUNDERED = null;

    @ObjectHolder (ID_EFFECT_SUPERCHARGE)
    public static final MobEffect SUPERCHARGE = null;

    @ObjectHolder (ID_EFFECT_WRENCHED)
    public static final MobEffect WRENCHED = null;
    // endregion

    // region ENCHANTMENTS
    @ObjectHolder (ID_HOLDING)
    public static final Enchantment HOLDING = null;
    // endregion

    // region PARTICLES
    //@ObjectHolder(ID_PARTICLE_SNOW)
    //public static final SimpleParticleType SNOW = null;

    @ObjectHolder (ID_PARTICLE_FROST)
    public static final SimpleParticleType FROST_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_SPARK)
    public static final SimpleParticleType SPARK_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_PLASMA)
    public static final SimpleParticleType PLASMA_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_SHOCKWAVE)
    public static final SimpleParticleType SHOCKWAVE_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_BLAST_WAVE)
    public static final SimpleParticleType BLAST_WAVE_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_VORTEX)
    public static final SimpleParticleType VORTEX_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_SPIRAL)
    public static final SimpleParticleType SPIRAL_PARTICLE = null;
    // endregion

    // region ENTITIES
    @ObjectHolder (ID_KNIFE)
    public static final EntityType<Knife> KNIFE_ENTITY = null;

    @ObjectHolder (ID_ELECTRIC_ARC)
    public static final EntityType<ElectricArc> ELECTRIC_ARC_ENTITY = null;

    @ObjectHolder (ID_ELECTRIC_FIELD)
    public static final EntityType<ElectricField> ELECTRIC_FIELD_ENTITY = null;

    @ObjectHolder (ID_BLACK_HOLE)
    public static final EntityType<BlackHole> BLACK_HOLE_ENTITY = null;
    // endregion

    // region SOUND EVENTS
    //@ObjectHolder (ID_SOUND_ELECTRICITY)
    //public static final SoundEvent ELECTRICITY_SOUND = null;
    // endregion
}
