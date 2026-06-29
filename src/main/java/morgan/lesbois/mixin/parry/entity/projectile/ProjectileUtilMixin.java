package morgan.lesbois.mixin.parry.entity.projectile;

import morgan.lesbois.interfaces.Parry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @ModifyVariable(
            method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static Box expandSearchArea(Box box) {
        return box.expand(1.5);
    }

    @Redirect(method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private static Box scaleParryingPlayerHitbox(Entity instance, World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, float margin) {
        if (instance instanceof PlayerEntity player) {
            if (((Parry) player).lesbois$isParrying()) {
                return instance.getBoundingBox().expand(1.5);
            }
        }

        return instance.getBoundingBox();
    }

    @Redirect(
            method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;raycast(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Optional;"
            )
    )
    private static Optional<Vec3d> fixRaycast(Box instance, Vec3d min, Vec3d max) {
        // Box.raycast() doesn't work if both min and max are contained inside. Just check if min is contained inside the box.
        if (instance.contains(min)) {
            return Optional.of(min);
        }

        return instance.raycast(min, max);
    }


    @Inject(
            method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void appendHitPos(World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, float margin, CallbackInfoReturnable<EntityHitResult> cir) {
        EntityHitResult hit = cir.getReturnValue();

        if (hit != null) {
            Box entityBox = hit.getEntity().getBoundingBox().expand(margin);
            Optional<Vec3d> arrowHit = entityBox.raycast(min, max);
            if (arrowHit.isPresent()) {
                cir.setReturnValue(new EntityHitResult(hit.getEntity(), arrowHit.get()));
            } else {
                // Try snap it onto the hitbox by finding the closest (ish) point.
                Optional<Vec3d> snapPos = entityBox.raycast(max, hit.getEntity().getBoundingBox().getCenter());
                if (snapPos.isPresent()) {
                    cir.setReturnValue(new EntityHitResult(hit.getEntity(), snapPos.get()));
                }
            }
        }
    }
}
