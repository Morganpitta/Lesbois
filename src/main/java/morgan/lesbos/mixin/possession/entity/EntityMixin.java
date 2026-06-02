package morgan.lesbos.mixin.possession.entity;

import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Inject(method = "isConnectedThroughVehicle", at = @At("HEAD"), cancellable = true)
    public void isConnectedThroughVehiclePossession(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if ( (Object) this instanceof PlayerEntity playerEntity ) {
            if (((PossessionInterface) playerEntity).lesbos$getPossessedEntity() == other)
                cir.setReturnValue(true);
        }
        else if ( (Object) this instanceof MobEntity mobEntity ) {
            if (((PossessorInterface) mobEntity).lesbos$getPossessor() == other)
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    private void redirectEyeHeight(EntityPose pose, CallbackInfoReturnable<Float> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            MobEntity entity = ((PossessionInterface) player).lesbos$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(entity.getEyeHeight(entity.getPose()));
            }
        }
    }

    @Inject(
            method = "shouldRender(D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldRenderDisablePossessionRender(double distance, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            MobEntity entity = ((PossessionInterface) player).lesbos$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "teleportTo", at=@At("HEAD"))
    public void teleportToPossessedEntity(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        if (this.getWorld() instanceof ServerWorld && (Entity) (Object) this instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();

            if (entity != null) {
                Entity newEntity = entity.teleportTo(new TeleportTarget(teleportTarget.world(), teleportTarget.pos(), Vec3d.ZERO, teleportTarget.yaw(), teleportTarget.pitch(), TeleportTarget.NO_OP));

                if (newEntity != entity) {
                    ((PossessionInterface) this).lesbos$setPossessedEntity((MobEntity) newEntity);
                }
            }
        }
    }

    @Inject(method = "teleport", at=@At("HEAD"))
    public void teleportPossessedEntity(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();

            if (entity != null) {
                Entity newEntity = entity.teleportTo(new TeleportTarget(world, new Vec3d(destX, destY, destZ), Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP));

                if (newEntity != entity) {
                    ((PossessionInterface) this).lesbos$setPossessedEntity((MobEntity) newEntity);
                }
            }
        }
    }

    @Inject(method = "requestTeleport", at=@At("HEAD"))
    void requestTeleportPossessedEntity(double destX, double destY, double destZ, CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld && (Entity) (Object) this instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();

            if (entity != null) {
                Entity newEntity = entity.teleportTo(new TeleportTarget((ServerWorld) this.getWorld(), new Vec3d(destX, destY, destZ), Vec3d.ZERO, 0, 0, TeleportTarget.NO_OP));

                if (newEntity != entity) {
                    ((PossessionInterface) this).lesbos$setPossessedEntity((MobEntity) newEntity);
                }
            }
        }
    }

    @Inject(method = "canUsePortals", at=@At("HEAD"), cancellable = true)
    void preventPossessedEntityPortals(boolean allowVehicles, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof MobEntity entity) {
            PlayerEntity player = ((PossessorInterface) entity).lesbos$getPossessor();

            if (player != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
