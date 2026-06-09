package morgan.lesbois.mixin.possession.server.network;

import com.mojang.authlib.GameProfile;
import morgan.lesbois.Lesbois;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "teleportTo", at=@At("HEAD"))
    public void teleportToPossessedEntity(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();

        if (entity != null) {
            Entity newEntity = entity.teleportTo(new TeleportTarget(teleportTarget.world(), teleportTarget.pos(), Vec3d.ZERO, teleportTarget.yaw(), teleportTarget.pitch(), TeleportTarget.NO_OP));

            if (newEntity != entity) {
                ((PossessionInterface) this).lesbois$setPossessedEntity((MobEntity) newEntity);
            }
        }
    }

    @Inject(method = "requestTeleport", at=@At("HEAD"))
    public void requestTeleportPossessedEntity(double destX, double destY, double destZ, CallbackInfo ci) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();

        if (entity != null) {
            Entity newEntity = entity.teleportTo(new TeleportTarget((ServerWorld) this.getWorld(), new Vec3d(destX, destY, destZ), Vec3d.ZERO, 0, 0, TeleportTarget.NO_OP));

            if (newEntity != entity) {
                ((PossessionInterface) this).lesbois$setPossessedEntity((MobEntity) newEntity);
            }
        }
    }

    @Inject(method = "requestTeleportOffset", at=@At("HEAD"))
    public void requestTeleportOffsetPossessedEntity(double offsetX, double offsetY, double offsetZ, CallbackInfo ci) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();

        if (entity != null) {
            Entity newEntity = entity.teleportTo(new TeleportTarget((ServerWorld) this.getWorld(), this.getPos().add(new Vec3d(offsetX, offsetY, offsetZ)), Vec3d.ZERO, 0, 0, TeleportTarget.NO_OP));

            if (newEntity != entity) {
                ((PossessionInterface) this).lesbois$setPossessedEntity((MobEntity) newEntity);
            }
        }
    }


    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at=@At("HEAD"))
    public void teleportPossessedEntity(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();

        if (entity != null) {
            Entity newEntity = entity.teleportTo(new TeleportTarget(targetWorld, new Vec3d(x, y, z), Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP));

            if (newEntity != entity) {
                ((PossessionInterface) this).lesbois$setPossessedEntity((MobEntity) newEntity);
            }
        }
    }


    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at=@At("HEAD"))
    public void teleportPossessedEntity(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();

        if (entity != null) {
            Entity newEntity = entity.teleportTo(new TeleportTarget(world, new Vec3d(destX, destY, destZ), Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP));

            if (newEntity != entity) {
                ((PossessionInterface) this).lesbois$setPossessedEntity((MobEntity) newEntity);
            }
        }
    }


    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writePossessedEntityNbt(NbtCompound nbt, CallbackInfo ci) {
        MobEntity entity = ((PossessionInterface) this).lesbois$getPossessedEntity();
        if (entity != null) {
            NbtCompound entityNbt = new NbtCompound();

            Identifier identifier = EntityType.getId(entity.getType());

            if (identifier == null) return;

            entityNbt.putString("id", identifier.toString());
            entity.writeNbt(entityNbt);

            nbt.put(Lesbois.stringId("possessed_entity"),entityNbt);
        }
    }
}
