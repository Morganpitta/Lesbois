package morgan.lesbois.entity;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CoinEntity extends ThrownItemEntity {
    public CoinEntity(EntityType<? extends CoinEntity> entityType, World world) {
        super(entityType, world);
    }

    public CoinEntity(World world) {
        super(LesboisEntities.COIN, world);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.NETHER_STAR;
    }

    // Stops jitter by disabling server sync
    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
    }

    protected double getGravity() {
        return 0.1;
    }

    public boolean isActive() {
        return this.age > 5;
    }

    @Override
    public void tick() {
        this.baseTick();

        if (this.getWorld().isClient() && this.isActive()) {
            Random random = this.getRandom();
            this.getWorld().addParticle(
                    ParticleTypes.END_ROD,
                    this.getX() + random.nextGaussian() * 0.1,
                    this.getY() + random.nextGaussian() * 0.1,
                    this.getZ() + random.nextGaussian() * 0.1,
                    0, 0, 0
            );
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.hitOrDeflect(hitResult);
        }

        this.checkBlockCollision();

        this.updateRotation();
        this.setPosition(this.getPos().add(this.getVelocity()));
        this.applyGravity();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }
}
