package morgan.lesbois.interfaces;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EffectSource {
    record Source(UUID attackerUuid, float damage) {
        public static final Codec<Source> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Uuids.CODEC.fieldOf("attacker_uuid").forGetter(Source::attackerUuid),
                        Codec.FLOAT.fieldOf("damage").forGetter(Source::damage)
                ).apply(instance, Source::new)
        );
    }

    @Nullable EffectSource.Source lesbois$getStatusEffectSource(RegistryEntry<StatusEffect> statusEffect);
    void lesbois$setStatusEffectSource(RegistryEntry<StatusEffect> statusEffect, @Nullable EffectSource.Source source);
}
