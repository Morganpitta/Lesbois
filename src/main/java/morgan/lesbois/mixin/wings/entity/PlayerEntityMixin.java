package morgan.lesbois.mixin.wings.entity;

import morgan.lesbois.interfaces.Winged;
import morgan.lesbois.powers.WingsPowerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Winged {
    @Unique
    private static final float WING_ANGLE_ACCELERATION = 0.2F;
    @Unique
    private static final float WING_DISTANCE_ACCELERATION = 0.1F;

    @Unique
    private boolean isFlying = false;

    @Unique float wingSpeed = 0.0F;
    @Unique float wingAngle = 0.0F;
    @Unique float prevWingAngle = 0.0F;
    @Unique float wingDistance = 0.0F;
    @Unique float prevWingDistance = 0.0F;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean lesbois$isFlying() {
        return this.isFlying;
    }

    public void lesbois$setFlying(boolean value) {
        this.isFlying = value;
    }

    public float lesbois$getWingAngle() {
        return this.wingAngle;
    }
    public float lesbois$getPrevWingAngle() {
        return this.prevWingAngle;
    }
    public float lesbois$getWingDistance() {
        return this.wingDistance;
    }
    public float lesbois$getPrevWingDistance() {
        return this.prevWingDistance;
    }

    @Inject(method = "tickMovement", at=@At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (WingsPowerType.hasWings((PlayerEntity) (Object) this)) {
            float flapStrength = 0.3F;
            float acceleration = WingsPowerType.getAcceleration((PlayerEntity) (Object) this);
            float maxSpeed = WingsPowerType.getMaxSpeed((PlayerEntity) (Object) this);
            float boost = WingsPowerType.getBoost((PlayerEntity) (Object) this);

            if (this.isFlying){
                float yaw = this.getYaw() * ((float)Math.PI / 180F);

                Vec3d directionNormalised = new Vec3d(
                        (-MathHelper.sin(yaw) * this.forwardSpeed) + (MathHelper.cos(yaw) * this.sidewaysSpeed),
                        0,
                        (MathHelper.cos(yaw) * this.forwardSpeed) + (MathHelper.sin(yaw) * this.sidewaysSpeed)
                ).normalize();

                float clampedAcceleration = 0.0F;
                if (this.getVelocity().y < maxSpeed) {
                    clampedAcceleration = (float) Math.min(maxSpeed - this.getVelocity().y, acceleration);
                }

                this.setVelocity(this.getVelocity().add(directionNormalised.x * boost, clampedAcceleration, directionNormalised.z * boost));

                flapStrength = 1.0F;
                this.fallDistance = 0;
                this.velocityDirty = true;
            }

            if (this.getWorld().isClient()) {
                this.wingSpeed += (flapStrength * flapStrength - this.wingSpeed) * WING_ANGLE_ACCELERATION;

                this.prevWingAngle = this.wingAngle;
                this.wingAngle += this.wingSpeed;

                this.prevWingDistance = this.wingDistance;
                this.wingDistance += (flapStrength - this.wingDistance) * WING_DISTANCE_ACCELERATION;
            }
        }
    }
}
