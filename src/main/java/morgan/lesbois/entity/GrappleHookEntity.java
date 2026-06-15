package morgan.lesbois.entity;

import morgan.lesbois.interfaces.GrappleInterface;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleHookEntity extends Entity implements Ownable {
    @Nullable
    private PlayerEntity owner;
    public double minDistance;
    public double lookAssist;
    public double pullSpeed;
    public double damping;

    // Ideally a 3:2 ratio
    public static final double pullFactorModifier = 0.25;
    public static final double lookAssistModifier = 0.18;

    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(
            GrappleHookEntity.class, TrackedDataHandlerRegistry.INTEGER
    );

    public GrappleHookEntity(EntityType<? extends GrappleHookEntity> type, World world) {
        super(type, world);
    }

    public GrappleHookEntity(World world, @Nullable PlayerEntity owner, Vec3d position, float yaw, float pitch, double minDistance, double pullSpeed, double lookAssist, double damping) {
        super(LesboisEntities.GRAPPLE_HOOK, world);
        this.setOwner(owner);
        this.setPosition(position);
        this.setRotation(yaw, pitch);
        this.minDistance = minDistance;
        this.lookAssist = lookAssist;
        this.pullSpeed = pullSpeed;
        this.damping = damping;
    }

    @Override
    @Nullable
    public PlayerEntity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable PlayerEntity owner) {
        this.owner = owner;
        this.dataTracker.set(OWNER_ID, owner != null ? owner.getId() : -1);
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity owner = this.getOwner();
        if (owner == null || owner.isDead() || owner.isRemoved()) {
            if (owner != null) {
                ((GrappleInterface) (Object) owner).lesbois$setGrappleHook(null);
            }

            this.discard();
            return;
        }

        if (owner instanceof PlayerEntity player) {
            Vec3d hiltOffset = this.getRotationVector().normalize().multiply(-0.55);
            Vec3d hiltPos = this.getPos().add(hiltOffset);
            Vec3d ownerPos = owner.getBoundingBox().getCenter();

            Vec3d direction = hiltPos.subtract(ownerPos).normalize();
            Vec3d playerDirection = player.getRotationVector().normalize();
            Vec3d velocity = owner.getVelocity();

            double distanceSq = hiltPos.squaredDistanceTo(ownerPos);
            double pullSpeed = this.pullSpeed;
            double lookAssist = this.lookAssist;

            double minDistanceSq = this.minDistance * this.minDistance;
            if (distanceSq < (minDistanceSq * 0.25)) {
                pullSpeed = 0;
                lookAssist = 0;
            } else if (distanceSq < minDistanceSq) {
                pullSpeed *= (distanceSq - (minDistanceSq * 0.25)) / (minDistanceSq * 0.75);
                lookAssist = 0;
            }

            player.setVelocity(
                    velocity.multiply(1 - Math.clamp(this.damping, 0, 1))
                            .add(direction.multiply(pullFactorModifier * pullSpeed))
                            .add(playerDirection.multiply(lookAssistModifier * lookAssist))
            );

            if (!this.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.velocityDirty = true;
                serverPlayer.velocityModified = true;

                serverPlayer.networkHandler.sendPacket(
                        new EntityVelocityUpdateS2CPacket(serverPlayer)
                );
            }
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, -1);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (OWNER_ID.equals(data)) {
            int id = this.dataTracker.get(OWNER_ID);
            this.owner = id != -1 ? (PlayerEntity) this.getWorld().getEntityById(id) : null;
            if ( this.owner != null && ((GrappleInterface) this.owner).lesbois$getGrappleHook() == null) ((GrappleInterface) this.owner).lesbois$setGrappleHook(this);
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void onRemoved() {
        if (owner instanceof GrappleInterface grappleOwner && grappleOwner.lesbois$getGrappleHook() == this) {
            grappleOwner.lesbois$setGrappleHook(null);
        }
        super.onRemoved();
    }

    // Entity is non-persistent, no need to save or have any additional data
    @Override
    public boolean shouldSave() {return false;}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
