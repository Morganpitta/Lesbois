package morgan.lesbois.mixin.possession.entity;

import morgan.lesbois.interfaces.PossessionInterface;
import morgan.lesbois.interfaces.PossessorInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public float prevBodyYaw;

    @Shadow
    public float prevHeadYaw;

    @Shadow
    public float lastHandSwingProgress;

    @Shadow
    public float handSwingProgress;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick",at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbois$getPossessor();

            if (player == null) return;

            this.updatePositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            this.setHeadYaw(player.headYaw);
            this.setBodyYaw(player.bodyYaw);

            this.fallDistance = 0;
            this.setVelocity(player.getVelocity());
            this.velocityDirty = true;

            this.lastRenderX = player.lastRenderX;
            this.lastRenderY = player.lastRenderY;
            this.lastRenderZ = player.lastRenderZ;

            this.prevX = player.prevX;
            this.prevY = player.prevY;
            this.prevZ = player.prevZ;
            this.prevYaw = player.prevYaw;
            this.prevPitch = player.prevPitch;
            this.prevBodyYaw = player.prevBodyYaw;
            this.prevHeadYaw = player.prevHeadYaw;

            this.lastHandSwingProgress = player.lastHandSwingProgress;
            this.handSwingProgress = player.handSwingProgress;

            this.setOnFire(player.isOnFire());
            this.setFireTicks(player.getFireTicks());
        }
    }


    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void redirectHealth(CallbackInfoReturnable<Float> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;
            MobEntity entity = possession.lesbois$getPossessedEntity();

            if (entity != null && entity.getHealth() > 0) {
                cir.setReturnValue(entity.getHealth());
            }
        }
    }

    @Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
    private void redirectAttributes(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;
            MobEntity entity = possession.lesbois$getPossessedEntity();

            if (entity != null && entity.getAttributes().hasAttribute(attribute)) {
                cir.setReturnValue(entity.getAttributeValue(attribute));
            }
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void redirectDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;
            MobEntity entity = possession.lesbois$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(entity.getDimensions(entity.getPose()));
            }
        }
    }

    @Inject(method = "canHit", at=@At("HEAD"), cancellable = true)
    public void canHit(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;
            MobEntity entity = possession.lesbois$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(false);
            }
        }
        else if (this.getWorld().isClient() && (LivingEntity) (Object) this instanceof MobEntity entity) {
            PossessorInterface possessor = (PossessorInterface) entity;
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (possessor.lesbois$getPossessor()==player) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/LivingEntity;limbAnimator:Lnet/minecraft/entity/LimbAnimator;",
                    opcode = Opcodes.GETFIELD
            ),
            cancellable = true
    )
    public void redirectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;
            MobEntity entity = possession.lesbois$getPossessedEntity();

            if ( entity != null ) {
                boolean damaged = entity.damage(source, amount);
                if (!damaged)
                    cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    public void redirectIsUsingItem(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbois$getPossessor();

            if ( player != null ) {
                cir.setReturnValue(player.isUsingItem());
            }
        }
    }


    @Inject(method = "getItemUseTime", at = @At("HEAD"), cancellable = true)
    public void redirectGetItemUseTime(CallbackInfoReturnable<Integer> cir) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbois$getPossessor();

            if ( player != null ) {
                cir.setReturnValue(player.getItemUseTime());
            }
        }
    }

    @Inject(method = "setSprinting", at=@At("TAIL"))
    public void setSprinting(boolean sprinting, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            MobEntity entity = ((PossessionInterface) player).lesbois$getPossessedEntity();

            if ( entity != null ) {
                entity.setSprinting(sprinting);
            }
        }
    }

    @Inject(method = "takeKnockback", at=@At("HEAD"))
    public void takeKnockBack(double strength, double x, double z, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbois$getPossessor();

            if ( player != null ) {
                player.takeKnockback(strength, x, z);
            }
        }
    }

    @Inject(method = "tiltScreen", at=@At("HEAD"))
    public void tiltScreen(double deltaX, double deltaZ, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbois$getPossessor();

            if ( player != null ) {
                player.tiltScreen(deltaX, deltaZ);
            }
        }
    }
}