package morgan.lesbois.entity.ai.goal;

import morgan.lesbois.interfaces.Bewitchable;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BewitchedFollowOwnerGoal extends Goal {
    private final MobEntity entity;
    @Nullable
    private PlayerEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public BewitchedFollowOwnerGoal(MobEntity entity, double speed, float minDistance, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (entity.getNavigation() == null) {
            throw new IllegalArgumentException("Entity has no navigation");
        }
    }

    @Override
    public boolean canStart() {
        if (this.entity.getTarget() != null && this.entity.getTarget().isAlive()) {
            return false;
        }

        PlayerEntity owner = ((Bewitchable) this.entity).lesbois$getOwner();
        if (owner == null) {
            return false;
        } else if (this.entity.hasVehicle() || this.entity.mightBeLeashed() || owner.isSpectator()) {
            return false;
        } else if (this.entity.squaredDistanceTo(owner) < this.minDistance * this.minDistance) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.entity.getTarget() != null && this.entity.getTarget().isAlive()) {
            return false;
        }

        if (this.navigation.isIdle()) {
            return false;
        } else {
            return !(this.entity.squaredDistanceTo(this.owner) <= this.maxDistance * this.maxDistance);
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.entity.getPathfindingPenalty(PathNodeType.WATER);
        this.entity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        boolean shouldTeleport = this.owner != null && this.entity.squaredDistanceTo(this.owner) >= 144.0;
        if (!shouldTeleport) {
            this.entity.getLookControl().lookAt(this.owner, 10.0F, this.entity.getMaxLookPitchChange());
        }

        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            if (shouldTeleport) {
                this.tryTeleportToOwner();
            } else {
                this.navigation.startMovingTo(this.owner, this.speed);
            }
        }
    }

    private void tryTeleportToOwner() {
        // AI Generated and not checked because I don't have the time
        if (this.owner == null) return;

        BlockPos basePos = this.owner.getBlockPos();

        for (int i = 0; i < 10; i++) {
            int dx = this.entity.getRandom().nextBetween(-3, 3);
            int dz = this.entity.getRandom().nextBetween(-3, 3);
            if (Math.abs(dx) < 2 && Math.abs(dz) < 2) continue;

            int dy = this.entity.getRandom().nextBetween(-1, 1);
            BlockPos targetPos = new BlockPos(basePos.getX() + dx, basePos.getY() + dy, basePos.getZ() + dz);

            if (LandPathNodeMaker.getLandNodeType(this.entity, targetPos.mutableCopy()) != PathNodeType.WALKABLE) continue;

            BlockPos offset = targetPos.subtract(this.entity.getBlockPos());
            if (!this.entity.getWorld().isSpaceEmpty(this.entity, this.entity.getBoundingBox().offset(offset))) continue;

            this.entity.refreshPositionAndAngles(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, this.entity.getYaw(), this.entity.getPitch());
            this.entity.getNavigation().stop();
            return;
        }
    }
}
