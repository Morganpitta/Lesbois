package morgan.lesbois.mixin.possession.entity.projectile;

import morgan.lesbois.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {
    @Shadow
    public abstract @Nullable Entity getOwner();

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(
            method = "shouldLeaveOwner",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getRootVehicle()Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity getRootVehicle(Entity entity) {
        if (entity instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) entity).lesbois$getPossessor();

            if ( player != null ) {
                return player.getRootVehicle();
            }
        }

        return entity.getRootVehicle();
    }
}
