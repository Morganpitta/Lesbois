package morgan.lesbois.mixin.possession.server;

import morgan.lesbois.Lesbois;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @ModifyVariable(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;isPresent()Z",
                    ordinal = 0
            ),
            ordinal = 0
    )
    public Optional<NbtCompound> loadPossessedEntity(Optional<NbtCompound> optional, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        if (optional.isPresent() && optional.get().contains(Lesbois.stringId("possessed_entity"), NbtElement.COMPOUND_TYPE)) {
            NbtCompound entityNbt = optional.get().getCompound(Lesbois.stringId("possessed_entity"));

                Optional<EntityType<?>> entityType = EntityType.fromNbt(entityNbt);
                if ( entityType.isPresent() ) {
                    Entity entity = entityType.get().create(player.getWorld());
                    if (entity instanceof MobEntity) {
                        entity.readNbt(entityNbt);
                        player.getServerWorld().spawnEntity(entity);
                        entity.setPos(player.getX(), player.getY(), player.getZ());
                        ((PossessionInterface) player).lesbois$possess((MobEntity) entity);
                    }
                    else {
                        Lesbois.LOGGER.warn("Exception loading entity: {}", entityNbt.getString("id"));
                    }
                }
                else {
                    Lesbois.LOGGER.warn("Exception loading entity: {}", entityNbt.getString("id"));
                }
        }

        return optional;
    }

    @Inject(method = "remove", at=@At("HEAD"))
    public void removePossessedEntity(ServerPlayerEntity player, CallbackInfo ci) {
        MobEntity entity = ((PossessionInterface) player).lesbois$getPossessedEntity();
        if (entity != null) {
            entity.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }
}
