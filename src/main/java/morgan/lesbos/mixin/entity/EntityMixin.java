package morgan.lesbos.mixin.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import morgan.lesbos.powers.DragModifierPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract boolean isTouchingWater();

    @Shadow
    public abstract boolean isInLava();

    @Shadow
    public abstract World getWorld();

    @Inject(method = "onLanding", at = @At("HEAD"))
    public void onLandingResetDoubleJumps(CallbackInfo ci) {
        if (!((Entity) (Object) this instanceof PlayerEntity)) return;
        DoubleJumpInterface doubleJumps = (DoubleJumpInterface) this;

        if (this.isOnGround() && doubleJumps.lesbos$getDoubleJumps() < doubleJumps.lesbos$getMaxDoubleJumps()) {
            doubleJumps.lesbos$setDoubleJumps(doubleJumps.lesbos$getMaxDoubleJumps());
        } else if ((this.isInLava() || this.isTouchingWater()) && doubleJumps.lesbos$getDoubleJumps() <= doubleJumps.lesbos$getMaxDoubleJumps()) {
            doubleJumps.lesbos$setDoubleJumps(doubleJumps.lesbos$getMaxDoubleJumps() + 1);
        }
    }

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isOnGround()Z"
            )
    )
    private boolean isOnGroundEnableGroundSlide(Entity entity) {
        if (!(entity instanceof LivingEntity)) return this.isOnGround();

        return this.isOnGround() || DragModifierPowerType.hasSlideMode((LivingEntity) entity);
    }

    @Inject(method = "isConnectedThroughVehicle", at = @At("HEAD"), cancellable = true)
    public void isConnectedThroughVehiclePossession(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if ( (Object) this instanceof PlayerEntity playerEntity ) {
            if (((PossessionInterface) playerEntity).lesbos$getPossessedEntity() == other)
                cir.setReturnValue(true);
        }
        else if ( (Object) this instanceof MobEntity mobEntity ) {
            if (((PossessorInterface) mobEntity).lesbos$getPossessor() == other)
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    private void redirectEyeHeight(EntityPose pose, CallbackInfoReturnable<Float> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity puppet = possession.lesbos$getPossessedEntity();

                if (puppet != null) {
                    cir.setReturnValue(puppet.getEyeHeight(puppet.getPose()));
                }
            }
        }
    }
}
