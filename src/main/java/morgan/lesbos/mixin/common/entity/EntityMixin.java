package morgan.lesbos.mixin.common.entity;

import morgan.lesbos.powers.DragModifierPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isOnGround();

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
}
