package morgan.lesbois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.entity.CoinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class TossCoinEntityAction extends EntityActionType {
    private final double speed;
    private final double height;

    public static final TypedDataObjectFactory<TossCoinEntityAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("speed", SerializableDataTypes.DOUBLE, 1D)
                    .add("height", SerializableDataTypes.DOUBLE, 0.7D),
            data -> new TossCoinEntityAction(
                    data.get("speed"),
                    data.get("height")),
            (actionType, serializableData) -> serializableData.instance()
                    .set("speed", actionType.speed)
                    .set("height", actionType.height)
    );

    public TossCoinEntityAction(double speed, double height) {
        this.speed = speed;
        this.height = height;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();

        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        Vec3d horizontalVec = entity.getRotationVector().multiply(1, 0, 1).normalize();
        Vec3d offset = entity.getPos().add(0, entity.getEyeHeight(entity.getPose())*0.8, 0).add(horizontalVec);

        CoinEntity coin = new CoinEntity(serverWorld);

        coin.setOwner(entity);
        coin.setPos(offset.x, offset.y, offset.z);
        coin.setVelocity(new Vec3d(horizontalVec.x*this.speed*0.5, this.height, horizontalVec.z*this.speed*0.5));

        serverWorld.spawnEntity(coin);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.TOSS_COIN;
    }
}
