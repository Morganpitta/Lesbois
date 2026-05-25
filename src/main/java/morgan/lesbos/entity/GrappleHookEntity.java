package morgan.lesbos.entity;

import morgan.lesbos.interfaces.GrappleInterface;
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
    public double unhookDistance;
    public double speed;
    public static final double damping = 0.5;

    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(
            GrappleHookEntity.class, TrackedDataHandlerRegistry.INTEGER
    );

    public GrappleHookEntity(EntityType<GrappleHookEntity> type, World world) {
        super(type, world);
    }

    public GrappleHookEntity(World world, @Nullable PlayerEntity owner, Vec3d position, double unhookDistance, double speed) {
        super(LesbosEntities.GRAPPLE_HOOK, world);
        this.setOwner(owner);
        this.setPosition(position);
        this.unhookDistance = unhookDistance;
        this.speed = speed;
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
        if (!this.getWorld().isClient()) {
            PlayerEntity owner = this.getOwner();
            if (owner == null || owner.isDead()) {
                if (owner != null) {
                    ((GrappleInterface) (Object) owner).lesbos$setGrappleHook(null);
                }

                this.discard();
                return;
            }

            double distanceSq = this.getPos().squaredDistanceTo(owner.getPos());
            if (distanceSq < (unhookDistance * unhookDistance)) {
                ((GrappleInterface) (Object) owner).lesbos$unGrapple();
                this.discard();
                return;
            }

            if (owner instanceof ServerPlayerEntity serverPlayer) {
                Vec3d direction = this.getPos().subtract(owner.getPos()).normalize();
                Vec3d velocity = owner.getVelocity();

                // TODO: Constants, Damping and pull speed

                serverPlayer.setVelocity(
                        velocity.x * damping + direction.x * 0.5 * this.speed,
                        velocity.y * damping + direction.y * 0.5 * this.speed,
                        velocity.z * damping + direction.z * 0.5 * this.speed
                );

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
        }
        super.onTrackedDataSet(data);
    }

    // Entity is non-persistent, no need to save or have any additional data
    @Override
    public boolean shouldSave() {return false;}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
