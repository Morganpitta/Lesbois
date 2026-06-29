package morgan.lesbois.mixin.double_jump.entity;

import morgan.lesbois.cardinalComponents.LesboisEntityComponents;
import morgan.lesbois.interfaces.DoubleJump;
import morgan.lesbois.powers.DoubleJumpPowerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements DoubleJump {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public int lesbois$getMaxDoubleJumps(){
        return DoubleJumpPowerType.getMaxDoubleJumps((PlayerEntity) (Object) this);
    }

    public double lesbois$getDoubleJumpHeight(){
        return DoubleJumpPowerType.getDoubleJumpHeight((PlayerEntity) (Object) this);
    }

    public int lesbois$getDoubleJumps(){
        return LesboisEntityComponents.DOUBLE_JUMP.get(this).getDoubleJumps();
    }

    public void lesbois$setDoubleJumps(int doubleJumps){
        LesboisEntityComponents.DOUBLE_JUMP.get(this).setDoubleJumps(doubleJumps);
    }

    public boolean lesbois$canDoubleJump(){
        return (DoubleJumpPowerType.canDoubleJump((PlayerEntity) (Object)this) && lesbois$getDoubleJumps() > 0 && !this.isOnGround() && !this.isFallFlying());
    }

    @Unique
    public void lesbois$doubleJump() {
        Vec3d vec3d = this.getVelocity();
        float yaw = this.getYaw() * ((float)Math.PI / 180F);
        double horizontalSpeed = vec3d.horizontalLength();

        this.setVelocity(vec3d.x, Math.max(this.getJumpVelocity() * this.lesbois$getDoubleJumpHeight(), vec3d.y), vec3d.z);

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
}
