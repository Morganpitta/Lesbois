package morgan.lesbos.mixin.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.network.packet.DoubleJumpC2SPacket;
import morgan.lesbos.powers.DoubleJumpPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
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
    private int jumpingCooldown;

    @Shadow
    public abstract boolean isFallFlying();

    @Unique
    private boolean wasJumping = false;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public int lesbos$getMaxDoubleJumps(){
        return DoubleJumpPowerType.getMaxDoubleJumps((LivingEntity) (Object) this);
    }

    @Unique
    public int lesbos$getDoubleJumps(){
        return LesbosComponents.DOUBLE_JUMP.get(this).getDoubleJumps();
    }

    @Unique
    public void lesbos$setDoubleJumps(int doubleJumps){
        if ( lesbos$getDoubleJumps() != doubleJumps ) {
            LesbosComponents.DOUBLE_JUMP.get(this).setDoubleJumps(doubleJumps);
            LesbosComponents.DOUBLE_JUMP.sync(this);
        }
    }

    @Unique
    public boolean lesbos$canDoubleJump(){
        return (DoubleJumpPowerType.canDoubleJump((LivingEntity) (Object)this) && lesbos$getDoubleJumps() > 0 && !this.isOnGround() && !this.isFallFlying());
    }

    @Unique
    public void lesbos$doubleJump() {
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

        this.lesbos$setDoubleJumps(this.lesbos$getDoubleJumps() - 1);

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
            if ( this.lesbos$canDoubleJump() && !(this.isInLava() || this.isTouchingWater()) ) {
                this.lesbos$doubleJump();
                ClientPlayNetworking.send(new DoubleJumpC2SPacket());
            }
        }

        wasJumping = this.jumping;
    }
}
