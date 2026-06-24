package morgan.lesbois.interfaces;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface StatusEffectSourceInterface {
    record StatusEffectSource(UUID attackerUuid, float damage) {
        public static final Codec<StatusEffectSource> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Uuids.CODEC.fieldOf("attacker_uuid").forGetter(StatusEffectSource::attackerUuid),
                        Codec.FLOAT.fieldOf("damage").forGetter(StatusEffectSource::damage)
                ).apply(instance, StatusEffectSource::new)
        );
    }

    @Nullable StatusEffectSource lesbois$getStatusEffectSource(RegistryEntry<StatusEffect> statusEffect);
    void lesbois$setStatusEffectSource(RegistryEntry<StatusEffect> statusEffect, @Nullable StatusEffectSource source);
}
