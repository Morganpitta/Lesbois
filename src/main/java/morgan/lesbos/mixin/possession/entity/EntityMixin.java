package morgan.lesbos.mixin.possession.entity;

import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    public abstract DataTracker getDataTracker();

    @Unique
    private Entity getRootEntity(Entity entity) {
        if (entity == null) return null;

        entity = entity.getVehicle() != null ? entity.getRootVehicle() : entity;

        if (entity instanceof PossessorInterface possessed) {
            PlayerEntity possessor = possessed.lesbos$getPossessor();
            if (possessor != null) {
                return possessor.getVehicle() != null ? possessor.getRootVehicle() : possessor;
            }
        }

        return entity;
    }

    @Inject(method = "isConnectedThroughVehicle", at = @At("HEAD"), cancellable = true)
    public void isConnectedThroughVehiclePossession(Entity other, CallbackInfoReturnable<Boolean> cir) {
        Entity entity1 = getRootEntity((Entity) (Object) this);
        Entity entity2 = getRootEntity(other);

        if (entity1 != null && entity1 == entity2) {
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
            if (((PossessionInterface) player).lesbos$isPossessing()) {
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

    @Inject(method = "canStartRiding", at=@At("HEAD"), cancellable = true)
    void preventRiding(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof MobEntity mobEntity) {
            PlayerEntity player = ((PossessorInterface) mobEntity).lesbos$getPossessor();

            if (player != null) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getPose", at=@At("HEAD"), cancellable = true)
    public void getPose(CallbackInfoReturnable<EntityPose> cir) {
        if ((Entity) (Object) this instanceof MobEntity entity) {
            PlayerEntity player = ((PossessorInterface) entity).lesbos$getPossessor();

            if (player != null) {
                cir.setReturnValue(player.getPose());
            }
        }
    }

    @Inject(method = "getAir", at=@At("HEAD"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(entity.getAir());
            }
        }
    }

    @Inject(method = "getMaxAir", at=@At("HEAD"), cancellable = true)
    public void getMaxAir(CallbackInfoReturnable<Integer> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity) {
            if (this.getDataTracker() == null) return;

            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(entity.getMaxAir());
            }
        }
    }
}
