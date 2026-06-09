package morgan.lesbois.mixin.double_jump.entity;

import morgan.lesbois.components.LesboisEntityComponents;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.network.packet.DoubleJumpC2SPacket;
import morgan.lesbois.powers.DoubleJumpPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DoubleJumpInterface {
    @Shadow
    public float sidewaysSpeed;

    @Shadow
    public float forwardSpeed;

    @Shadow
    protected abstract float getJumpVelocity();

    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract boolean isFallFlying();

    @Unique
    private boolean wasJumping = false;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public int lesbois$getMaxDoubleJumps(){
        return DoubleJumpPowerType.getMaxDoubleJumps((LivingEntity) (Object) this);
    }

    public int lesbois$getDoubleJumps(){
        return LesboisEntityComponents.DOUBLE_JUMP.get(this).getDoubleJumps();
    }

    public void lesbois$setDoubleJumps(int doubleJumps){
        LesboisEntityComponents.DOUBLE_JUMP.get(this).setDoubleJumps(doubleJumps);
    }

    public boolean lesbois$canDoubleJump(){
        return (DoubleJumpPowerType.canDoubleJump((LivingEntity) (Object)this) && lesbois$getDoubleJumps() > 0 && !this.isOnGround() && !this.isFallFlying());
    }

    @Unique
    public void lesbois$doubleJump() {
        Vec3d vec3d = this.getVelocity();
        float yaw = this.getYaw() * ((float)Math.PI / 180F);
        double horizontalSpeed = vec3d.horizontalLength();

        this.setVelocity(vec3d.x, Math.max(this.getJumpVelocity(), vec3d.y), vec3d.z);

        Vec3d directionNormalised = new Vec3d(
                (-MathHelper.sin(yaw) * this.forwardSpeed) + (MathHelper.cos(yaw) * this.sidewaysSpeed),
                0,
                (MathHelper.cos(yaw) * this.forwardSpeed) + (MathHelper.sin(yaw) * this.sidewaysSpeed)
        ).normalize();

        if (directionNormalised.lengthSquared() > 0) {
            this.setVelocity(directionNormalised.x * horizontalSpeed, this.getVelocity().y, directionNormalised.z * horizontalSpeed);
        }

        if (this.isSprinting()) {
            this.setVelocity(this.getVelocity().add(directionNormalised.x * 0.2, 0.0, directionNormalised.z * 0.2));
        }

        this.lesbois$setDoubleJumps(this.lesbois$getDoubleJumps() - 1);

        this.velocityDirty = true;
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    args = "ldc=jump",
                    shift = At.Shift.AFTER
            )
    )
    public void tickMovementDoubleJump(CallbackInfo ci) {
        if ( !((LivingEntity)(Object)this instanceof PlayerEntity) ) return;

        if (this.jumping && !wasJumping) {
            if ( this.lesbois$canDoubleJump() && !(this.isInLava() || this.isTouchingWater()) ) {
                this.lesbois$doubleJump();
                ClientPlayNetworking.send(new DoubleJumpC2SPacket());
            }
        }

        wasJumping = this.jumping;
    }
}
