package morgan.lesbos.mixin.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import morgan.lesbos.powers.DragModifierPowerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F)
    )
    public float travelModifyAirDrag(float constant) {
        return DragModifierPowerType.getAirDrag((LivingEntity) (Object) this);
    }


    @Inject(method = "tick",at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbos$getPossessor();

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
        }
    }


    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void redirectHealth(CallbackInfoReturnable<Float> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null && entity.getHealth() > 0) {
                    cir.setReturnValue(entity.getHealth());
                }
            }
        }
    }

    @Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
    private void redirectAttributes(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null && entity.getAttributes().hasAttribute(attribute)) {
                    cir.setReturnValue(entity.getAttributeValue(attribute));
                }
            }
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void redirectDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null) {
                    cir.setReturnValue(entity.getDimensions(entity.getPose()));
                }
            }
        }
    }

    @Inject(method = "canHit", at=@At("HEAD"), cancellable = true)
    public void canHit(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null) {
                    cir.setReturnValue(false);
                }
            }
        }
        else if (this.getWorld().isClient() && (LivingEntity) (Object) this instanceof MobEntity entity) {
            PossessorInterface possessor = (PossessorInterface) entity;
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (possessor.lesbos$getPossessor()==player) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z",
                    ordinal = 1
            ),
            cancellable = true
    )
    public void redirectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if ( entity != null ) {
                    boolean damaged = entity.damage(source, amount);
                    if (!damaged)
                        cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "applyDamage", at= @At("HEAD"), cancellable = true)
    public void redirectApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if ( entity != null ) {
                    ci.cancel();
                }
            }
        }
    }
}
