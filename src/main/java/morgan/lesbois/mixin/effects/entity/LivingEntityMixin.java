package morgan.lesbois.mixin.effects.entity;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import morgan.lesbois.Lesbois;
import morgan.lesbois.entity.effect.LesboisStatusEffects;
import morgan.lesbois.interfaces.StatusEffectSourceInterface;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements StatusEffectSourceInterface {
    @Unique
    private final Map<RegistryEntry<StatusEffect>, StatusEffectSource> statusEffectsSources = Maps.<RegistryEntry<StatusEffect>, StatusEffectSource>newHashMap();

    @Unique
    private static final Codec<Map<RegistryEntry<StatusEffect>, StatusEffectSource>> SOURCES_CODEC =
            Codec.unboundedMap(
                    Registries.STATUS_EFFECT.getEntryCodec(),
                    StatusEffectSource.CODEC
            );

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow
    public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public @Nullable StatusEffectSource lesbois$getStatusEffectSource(RegistryEntry<StatusEffect> statusEffect) {
        return this.statusEffectsSources.get(statusEffect);
    }

    public void lesbois$setStatusEffectSource(RegistryEntry<StatusEffect> statusEffect, @Nullable StatusEffectSource source) {
        if (source == null) {
            this.statusEffectsSources.remove(statusEffect);
        } else {
            this.statusEffectsSources.put(statusEffect, source);
        }
    }

    @Unique
    public void triggerUnstableExplosion(UUID attackerUuid, float damage, int amplifier) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity attacker = serverWorld.getEntity(attackerUuid);
            if (attacker != null) {
                this.damage(this.getDamageSources().explosion(attacker, attacker), damage * (amplifier + 1));

                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 1.5F);

                // Can't be bothered writing own class I'm sorry I have commited sins
                serverWorld.createExplosion(
                        this,
                        this.getDamageSources().explosion(this, attacker),
                        new ExplosionBehavior() {
                            public boolean canDestroyBlocks(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                                return false;
                            }

                            public boolean shouldDamage(Explosion explosion, Entity entity) {
                                return entity != attacker;
                            }
                        },
                        this.getX(), this.getY(), this.getZ(),
                        amplifier + 1,
                        false,
                        World.ExplosionSourceType.MOB
                );

                int particles = 36;
                for (int index = 0; index < particles; index++) {
                    double angle = (index * 2 * Math.PI) / particles;

                    double velocityX = Math.cos(angle) * 0.3;
                    double velocityZ = Math.sin(angle) * 0.3;
                    double velocityY = 0.1;

                    serverWorld.spawnParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            this.getX(), this.getY(), this.getZ(),
                            0,
                            velocityX, velocityY, velocityZ,
                            1.0
                    );
                }
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z")))
    private void applyUnstableOnHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        Entity attacker = source.getAttacker();

        if (attacker instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(LesboisStatusEffects.OVERCHARGED)) {
                StatusEffectInstance effect = livingEntity.getStatusEffect(LesboisStatusEffects.OVERCHARGED);

                StatusEffectInstance unstable = new StatusEffectInstance(LesboisStatusEffects.UNSTABLE, 40, effect.getAmplifier());
                this.lesbois$setStatusEffectSource(LesboisStatusEffects.UNSTABLE, new StatusEffectSource(attacker.getUuid(), amount));
                this.addStatusEffect(unstable);

                // Clear it down to one tick
                if (effect.getDuration() > 1) {
                    livingEntity.removeStatusEffect(LesboisStatusEffects.OVERCHARGED);
                    livingEntity.addStatusEffect(new StatusEffectInstance(
                            LesboisStatusEffects.OVERCHARGED,
                            1,
                            effect.getAmplifier()
                    ));
                }
            }
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    public void clearStatusEffectSource(StatusEffectInstance effect, CallbackInfo ci) {
        StatusEffectSource source = this.lesbois$getStatusEffectSource(effect.getEffectType());
        if (source != null) {
            this.lesbois$setStatusEffectSource(effect.getEffectType(), null);
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("HEAD"))
    public void onUnstableFinished(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect.equals(LesboisStatusEffects.UNSTABLE)) {
            StatusEffectSource source = this.lesbois$getStatusEffectSource(LesboisStatusEffects.UNSTABLE);
            if (source != null && source.attackerUuid() != null) {
                this.triggerUnstableExplosion(source.attackerUuid(), source.damage(), effect.getAmplifier());
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.hasStatusEffect(LesboisStatusEffects.UNSTABLE)) {
            StatusEffectInstance effect = this.getStatusEffect(LesboisStatusEffects.UNSTABLE);
            StatusEffectSource source = this.lesbois$getStatusEffectSource(LesboisStatusEffects.UNSTABLE);

            if (source != null && source.attackerUuid() != null) {
                this.triggerUnstableExplosion(source.attackerUuid(), source.damage(), effect.getAmplifier());
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!this.statusEffectsSources.isEmpty()) {
            String key = Lesbois.stringId("status_effects_sources");

            SOURCES_CODEC.encodeStart(NbtOps.INSTANCE, this.statusEffectsSources).resultOrPartial(Lesbois.LOGGER::error).ifPresent(nbtElement -> nbt.put(key, nbtElement));
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void loadSourceFromNbt(NbtCompound nbt, CallbackInfo ci) {
        String key = Lesbois.stringId("status_effects_sources");

        if (nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
            SOURCES_CODEC.parse(NbtOps.INSTANCE, nbt.get(key)).resultOrPartial(Lesbois.LOGGER::error).ifPresent(this.statusEffectsSources::putAll);
        }
    }
}
