package morgan.lesbois.mixin.parry.entity.projectile;

import morgan.lesbois.interfaces.Parry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {
    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(
            method = "deflect(Lnet/minecraft/entity/ProjectileDeflection;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ProjectileDeflection;deflect(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/random/Random;)V"
            )
    )
    private void deflectRedirectProjectile(ProjectileDeflection instance, ProjectileEntity projectile, Entity hitEntity, Random random) {
        if (hitEntity instanceof PlayerEntity && instance == ProjectileDeflection.SIMPLE) {
            if (((Parry) hitEntity).lesbois$shouldRedirectProjectile()) {
                ((Parry) hitEntity).lesbois$setRedirectProjectile(false);

                ProjectileDeflection.REDIRECTED.deflect(projectile, hitEntity, random);
                projectile.setVelocity(projectile.getVelocity().multiply(5)); // I hate that I have to include this but this is already way too much effort for something so simple
                return;
            }
        }

        instance.deflect(projectile, hitEntity, random);
    }
}
