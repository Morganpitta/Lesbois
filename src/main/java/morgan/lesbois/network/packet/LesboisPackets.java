package morgan.lesbois.network.packet;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import morgan.lesbois.Lesbois;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.powers.ActionOnKeyReleasePowerType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LesboisPackets {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseKeyReleasePowerTypesC2SPacket.ID, UseKeyReleasePowerTypesC2SPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(PossessionS2CPacket.ID, PossessionS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UnPossessionS2CPacket.ID, UnPossessionS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(MovingSoundS2CPacket.ID, MovingSoundS2CPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpC2SPacket.ID, LesboisPackets::handleDoubleJumpPacket);
        ServerPlayNetworking.registerGlobalReceiver(UseKeyReleasePowerTypesC2SPacket.ID, LesboisPackets::handleUseKeyReleasePowerType);
    }

    public static void handleDoubleJumpPacket(DoubleJumpC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ServerPlayerEntity player = context.player();
            DoubleJumpInterface playerCasted = (DoubleJumpInterface) (Object) player;

            if (playerCasted.lesbois$getDoubleJumps() > 0) {
                playerCasted.lesbois$doubleJump();
            }
        });
    }


    public static void handleUseKeyReleasePowerType(UseKeyReleasePowerTypesC2SPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);

        for (Identifier powerId : payload.powerIds()) {
            PowerType powerType = PowerManager.getOptional(powerId)
                    .map(component::getPowerType)
                    .orElse(null);

            if (powerType instanceof ActionOnKeyReleasePowerType keyReleasePowerType) {

                if (keyReleasePowerType.isActive()) {
                    keyReleasePowerType.onUse();
                }

            }
            else if (powerType != null) {
                Lesbois.LOGGER.warn("Unexpectedly found power \"{}\" (which doesn't have a key release power type) while receiving packet for triggering active power types of player {}!", powerId, player.getName().getString());
            }
            else {
                Lesbois.LOGGER.warn("Found unknown power \"{}\" while receiving packet for triggering key release power types of player {}!", powerId, player.getName().getString());
            }
        }
    }
}
