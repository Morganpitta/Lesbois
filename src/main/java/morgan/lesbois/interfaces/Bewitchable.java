package morgan.lesbois.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.EntityView;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Bewitchable {
    @Nullable
    UUID lesbois$getOwnerUuid();
    void lesbois$setOwnerUuid(@Nullable UUID uuid);

    EntityView getWorld();

    @Nullable
    default PlayerEntity lesbois$getOwner() {
        UUID uuid = this.lesbois$getOwnerUuid();
        return uuid == null ? null : this.getWorld().getPlayerByUuid(uuid);
    }

    default boolean lesbois$isBewitched() {
        return this.lesbois$getOwnerUuid() != null;
    }

    void lesbois$setBewitched(@Nullable PlayerEntity owner);
}
