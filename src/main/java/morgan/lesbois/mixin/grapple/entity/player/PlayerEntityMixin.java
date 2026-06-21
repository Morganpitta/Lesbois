package morgan.lesbois.mixin.grapple.entity.player;

import morgan.lesbois.entity.GrappleHookEntity;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.interfaces.GrappleInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements GrappleInterface {
    @Unique
    @Nullable
    GrappleHookEntity grappleHook;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public GrappleHookEntity lesbois$getGrappleHook() {
        return grappleHook;
    }

    @Unique
    public void lesbois$setGrappleHook(@Nullable GrappleHookEntity hook) {
        this.grappleHook = hook;
    }


    @Unique
    @Nullable
    public GrappleHookEntity lesbois$grapple(float maxDistance, float minDistance, boolean disableFallDamage, float pullSpeed, float lookAssist, float damping) {
        if (this.grappleHook != null) {
            this.grappleHook.discard();
            this.grappleHook = null;
        }

        RaycastContext raycastContext = new RaycastContext(
                this.getEyePos(),
                this.getEyePos().add(this.getRotationVec(1.0F).multiply(maxDistance)),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        );

        BlockHitResult hit = this.getWorld().raycast(raycastContext);

        if (hit.getType() == HitResult.Type.MISS) return null;

        Vec3d offset = new Vec3d(hit.getSide().getUnitVector()).multiply(0.1);

        GrappleHookEntity hook = new GrappleHookEntity(this.getWorld(), (PlayerEntity) (Object) this, hit.getPos().add(offset), this.getYaw(), this.getPitch(), disableFallDamage, minDistance, pullSpeed, lookAssist, damping);
        this.getWorld().spawnEntity(hook);
        this.grappleHook = hook;

        DoubleJumpInterface doubleJumps = (DoubleJumpInterface) (Object) this;
        doubleJumps.lesbois$setDoubleJumps(doubleJumps.lesbois$getMaxDoubleJumps());

        return hook;
    }

    @Unique
    public boolean lesbois$unGrapple() {
        if (this.grappleHook == null) return false;

        this.grappleHook.discard();
        this.grappleHook = null;
        return true;
    }

    @Redirect(
            method = "getBlockBreakingSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"
            )
    )
    public boolean getBlockBreakingSpeedIncreaseMiningSpeedWhenGrappled(PlayerEntity player) {
        return player.isOnGround() || ((GrappleInterface) player).lesbois$getGrappleHook() != null;
    }

    @Inject(method = "tickMovement", at=@At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        GrappleHookEntity entity = this.lesbois$getGrappleHook();

        if (entity != null) {
            Vec3d hiltPos = entity.getHiltPos();
            Vec3d ownerPos = this.getBoundingBox().getCenter();

            Vec3d direction = hiltPos.subtract(ownerPos).normalize();
            Vec3d playerDirection = this.getRotationVector().normalize();
            Vec3d velocity = this.getVelocity();

            double distanceSq = hiltPos.squaredDistanceTo(ownerPos);
            double pullSpeed = entity.getPullSpeed();
            double lookAssist = entity.getLookAssist();
            double damping = entity.getDamping();

            double minDistanceSq = entity.getMinDistance() * entity.getMinDistance();
            if (distanceSq < minDistanceSq) {
                lookAssist = 0;
                pullSpeed *= distanceSq/minDistanceSq;

                double speedSq = velocity.lengthSquared();
                double minSpeedSq = 0.5 * 0.5;
                if (speedSq < minSpeedSq) {
                    double distanceDamping = 1 - (distanceSq / minDistanceSq);
                    double speedDamping = 1 - (speedSq / minSpeedSq);
                    damping = Math.max(damping, speedDamping * distanceDamping * 0.8);
                }
            }

            this.setVelocity(
                    velocity.multiply(1 - Math.clamp(damping, 0, 1))
                            .add(direction.multiply(GrappleHookEntity.pullFactorModifier * pullSpeed))
                            .add(playerDirection.multiply(GrappleHookEntity.lookAssistModifier * lookAssist))
            );

            if (entity.disableFallDamage)
                this.fallDistance = 0;
        }
    }
}