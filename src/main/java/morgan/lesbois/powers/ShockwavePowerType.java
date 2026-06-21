package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbois.common.Util;
import morgan.lesbois.sound.LesboisSounds;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ShockwavePowerType extends PowerType {
    public static final TypedDataObjectFactory<ShockwavePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, KeyBindingReference.NONE)
                    .add("min_speed", SerializableDataTypes.FLOAT, 1F)
                    .add("distance", SerializableDataTypes.FLOAT, 10F)
                    .add("damage", SerializableDataTypes.FLOAT, 0.5F)
                    .add("knockback", SerializableDataTypes.FLOAT, 1F),
            (data, condition) -> new ShockwavePowerType(
                    data.get("key"),
                    data.get("min_speed"),
                    data.get("distance"),
                    data.get("damage"),
                    data.get("knockback"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("key", powerType.getKey())
                    .set("min_speed", powerType.minSpeed)
                    .set("distance", powerType.distance)
                    .set("damage", powerType.damage)
                    .set("knockback", powerType.knockback)
    );

    private final KeyBindingReference key;
    private final float minSpeed;
    private final float distance;
    private final float damage;
    private final float knockback;

    ShockwavePowerType(KeyBindingReference key, float minSpeed, float distance, float damage, float knockback, Optional<EntityCondition> condition) {
        super(condition);
        this.key = key;
        this.minSpeed = minSpeed;
        this.distance = distance;
        this.damage = damage;
        this.knockback = knockback;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.SHOCKWAVE;
    }

    public void onUse() {
        getHolder().setVelocity(Vec3d.ZERO);
        getHolder().velocityDirty = true;
    }

    public boolean isActive() {
        return super.isActive() && this.getHolder().getVelocity().length() >= this.minSpeed;
    }

    public KeyBindingReference getKey() {
        return key;
    }

    public void triggerShockwave(ServerPlayerEntity player, Vec3d velocity, Vec3d pos) {
        ServerWorld world = player.getServerWorld();
        double playerSpeed = velocity.length();

        if (playerSpeed + 0.1 < this.minSpeed) return;

        Vec3d velocityNormalised = velocity.normalize();

        List<Entity> entities = Util.getEntitiesInCone(player.getServerWorld(), player, pos.subtract(velocityNormalised.multiply(2)), velocityNormalised, this.distance + 2, 90);

        player.setVelocity(Vec3d.ZERO);
        player.velocityDirty = true;
        player.velocityModified = true;
        player.fallDistance = 0;

        for (double j = 2; j < distance*2; j+=2) {
            Vec3d particlePos = pos.add(velocityNormalised.multiply(j-1));
            Util.spawnParticles(player.getServerWorld(), ParticleTypes.EXPLOSION, particlePos.x, particlePos.y, particlePos.z, (int) Math.ceil((j/4)*(j/4)), j/5, j/5, j/5, 0.0, true);
        }

        world.playSound(null, pos.x, pos.y, pos.z, LesboisSounds.SONIC_BOOM, SoundCategory.PLAYERS, 3.0F, 1.0F);

        entities.forEach(entity -> {
            if ( entity instanceof LivingEntity) {
                if (world.getRandom().nextFloat() < 0.05F) {
                    Util.sendMovingSound(LesboisSounds.PATRICK_SCREAM, 2.3F, 1.0F, entity, world.getRandom().nextLong());
                }
            }

            Vec3d vectorToEntity = entity.getPos().subtract(pos).normalize();

            entity.damage(entity.getDamageSources().playerAttack(player), (float) (this.damage * playerSpeed * player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));

            Vec3d knockback = new Vec3d(vectorToEntity.x, 1, vectorToEntity.z)
                    .multiply(this.knockback * playerSpeed);
            entity.setVelocity(entity.getVelocity().add(knockback));
        });
    }
}
