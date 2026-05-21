package morgan.lesbos.mixin.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.components.DoubleJumpComponent;
import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.powers.DoubleJumpPower;
import morgan.lesbos.powers.LesbosPowers;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
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
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public float sidewaysSpeed;

    @Shadow
    public float forwardSpeed;

    @Shadow
    protected abstract float getJumpVelocity();

    @Shadow
    public abstract float getJumpBoostVelocityModifier();

    @Shadow
    protected boolean jumping;

    @Shadow
    protected abstract boolean shouldSwimInFluids();

    @Shadow
    private int jumpingCooldown;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public int getMaxDoubleJumps(){
        return DoubleJumpPower.getMaxDoubleJumps((LivingEntity) (Object) this);
    }

    @Unique
    public int getDoubleJumps(){
        return LesbosComponents.DOUBLE_JUMP.get(this).getDoubleJumps();
    }

    @Unique
    public void setDoubleJumps(int doubleJumps){
        LesbosComponents.DOUBLE_JUMP.get(this).setDoubleJumps(doubleJumps);
    }

    @Unique
    public boolean canDoubleJump(){
        return (DoubleJumpPower.canDoubleJump((LivingEntity) (Object)this) && getDoubleJumps() > 0 && !this.isOnGround());
    }

    @Unique
    protected void doubleJump() {
        Vec3d vec3d = this.getVelocity();
        float yaw = this.getYaw() * ((float)Math.PI / 180F);
        double horizontalSpeed = vec3d.horizontalLength();

        // Add jump velocity
        this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);

        // Change direction of movement
        Vec3d directionNormalised = new Vec3d(
                (-MathHelper.sin(yaw) * this.forwardSpeed) + (MathHelper.cos(yaw) * this.sidewaysSpeed),
                0,
                (MathHelper.cos(yaw) * this.forwardSpeed) + (MathHelper.sin(yaw) * this.sidewaysSpeed)
        ).normalize();

        if (directionNormalised.lengthSquared() > 0) {
            this.setVelocity(directionNormalised.x * horizontalSpeed, this.getVelocity().y, directionNormalised.z * horizontalSpeed);
        }

        // Add extra sprint boost
        if (this.isSprinting()) {
            this.setVelocity(this.getVelocity().add(directionNormalised.x * 0.2, 0.0, directionNormalised.z * 0.2));
        }

        this.velocityDirty = true;
    }

    @Inject(method = "tickMovement",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;shouldSwimInFluids()Z",shift = At.Shift.BEFORE))
    public void tickMovementDoubleJump(CallbackInfo ci) {
        if (this.isOnGround() && getDoubleJumps() < getMaxDoubleJumps()) {
            this.setDoubleJumps(getMaxDoubleJumps());
        } else if (this.isTouchingWater() && getDoubleJumps() <= getMaxDoubleJumps()) {
            this.setDoubleJumps(getMaxDoubleJumps() + 1);
        }

        if (this.jumping && this.shouldSwimInFluids()) {
            double k = this.isInLava() ? this.getFluidHeight(FluidTags.LAVA) : this.getFluidHeight(FluidTags.WATER);
            boolean bl = this.isTouchingWater() && k > 0.0;
            double l = this.getSwimHeight();
            if (!(this.isInLava() && (!this.isOnGround() || k > l)) && !(bl && (!this.isOnGround() || k > l)) && ((canDoubleJump()) && this.jumpingCooldown == 0)) {
                this.doubleJump();
                this.setDoubleJumps(getDoubleJumps() - 1);
                this.jumpingCooldown = 10;
            }
        }
    }
}
