package morgan.lesbois.entity;

import morgan.lesbois.interfaces.GrappleInterface;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleHookEntity extends Entity implements Ownable {
    @Nullable
    private PlayerEntity owner;
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(GrappleHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public boolean disableFallDamage;
    private static final TrackedData<Float> MIN_DISTANCE = DataTracker.registerData(GrappleHookEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> LOOK_ASSIST = DataTracker.registerData(GrappleHookEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> PULL_SPEED = DataTracker.registerData(GrappleHookEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DAMPING = DataTracker.registerData(GrappleHookEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final double pullFactorModifier = 0.25;
    public static final double lookAssistModifier = 0.25;

    public GrappleHookEntity(EntityType<? extends GrappleHookEntity> type, World world) {
        super(type, world);
    }

    public GrappleHookEntity(World world, @Nullable PlayerEntity owner, Vec3d position, float yaw, float pitch, boolean disableFallDamage, float minDistance, float pullSpeed, float lookAssist, float damping) {
        super(LesboisEntities.GRAPPLE_HOOK, world);
        this.setOwner(owner);
        this.setPosition(position);
        this.setRotation(yaw, pitch);
        this.disableFallDamage = disableFallDamage;
        this.setMinDistance(minDistance);
        this.setLookAssist(lookAssist);
        this.setPullSpeed(pullSpeed);
        this.setDamping(damping);
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

    public float getMinDistance() {
        return this.dataTracker.get(MIN_DISTANCE);
    }

    public void setMinDistance(float minDistance) {
        this.dataTracker.set(MIN_DISTANCE, minDistance);
    }

    public float getLookAssist() {
        return this.dataTracker.get(LOOK_ASSIST);
    }

    public void setLookAssist(float lookAssist) {
        this.dataTracker.set(LOOK_ASSIST, lookAssist);
    }

    public float getPullSpeed() {
        return this.dataTracker.get(PULL_SPEED);
    }

    public void setPullSpeed(float pullSpeed) {
        this.dataTracker.set(PULL_SPEED, pullSpeed);
    }

    public float getDamping() {
        return this.dataTracker.get(DAMPING);
    }

    public void setDamping(float damping) {
        this.dataTracker.set(DAMPING, damping);
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
            double pullSpeed = this.getPullSpeed();
            double lookAssist = this.getLookAssist();
            double damping = this.getDamping();

            double minDistanceSq = this.getMinDistance() * this.getMinDistance();
            if (distanceSq < minDistanceSq) {
                lookAssist = 0;
                pullSpeed *= distanceSq/minDistanceSq;

                double speedSq = velocity.lengthSquared();
                double minSpeedSq = 0.5 * 0.5;
                if (speedSq < minSpeedSq) {
                    double distanceDamping = 1 - (distanceSq / minDistanceSq);
                    double speedDamping = 1 - (speedSq / minSpeedSq);
                    damping = Math.max(damping, speedDamping * distanceDamping * 0.8);
                }
            }

            player.setVelocity(
                    velocity.multiply(1 - Math.clamp(damping, 0, 1))
                            .add(direction.multiply(pullFactorModifier * pullSpeed))
                            .add(playerDirection.multiply(lookAssistModifier * lookAssist))
            );

            if (this.disableFallDamage)
                player.fallDistance = 0;
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, -1);
        builder.add(MIN_DISTANCE, 0.0F);
        builder.add(LOOK_ASSIST, 0.0F);
        builder.add(PULL_SPEED, 0.0F);
        builder.add(DAMPING, 0.0F);
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
