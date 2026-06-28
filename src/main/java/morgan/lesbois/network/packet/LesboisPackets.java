package morgan.lesbois.network.packet;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import morgan.lesbois.Lesbois;
import morgan.lesbois.block.LesboisBlocks;
import morgan.lesbois.entity.CoinEntity;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.interfaces.WingsInterface;
import morgan.lesbois.powers.ActionOnCoinPowerType;
import morgan.lesbois.powers.ActionOnKeyReleasePowerType;
import morgan.lesbois.powers.FrostGlidingPowerType;
import morgan.lesbois.powers.ShockwavePowerType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LesboisPackets {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseKeyReleasePowerTypesC2SPacket.ID, UseKeyReleasePowerTypesC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CoinHitC2SPacket.ID, CoinHitC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(FrostGlideC2SPacket.ID, FrostGlideC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ShockwavePowerTypesC2SPacket.ID, ShockwavePowerTypesC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(FlyingC2SPacket.ID, FlyingC2SPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(PossessionS2CPacket.ID, PossessionS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UnPossessionS2CPacket.ID, UnPossessionS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(MovingSoundS2CPacket.ID, MovingSoundS2CPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpC2SPacket.ID, LesboisPackets::handleDoubleJumpPacket);
        ServerPlayNetworking.registerGlobalReceiver(UseKeyReleasePowerTypesC2SPacket.ID, LesboisPackets::handleUseKeyReleasePowerType);
        ServerPlayNetworking.registerGlobalReceiver(CoinHitC2SPacket.ID, LesboisPackets::handleUseCoinPowerType);
        ServerPlayNetworking.registerGlobalReceiver(FrostGlideC2SPacket.ID, LesboisPackets::handleFrostGlide);
        ServerPlayNetworking.registerGlobalReceiver(ShockwavePowerTypesC2SPacket.ID, LesboisPackets::handleShockwavePacket);
        ServerPlayNetworking.registerGlobalReceiver(FlyingC2SPacket.ID, LesboisPackets::handleFlyingPacket);
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

    public static void handleUseCoinPowerType(CoinHitC2SPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        Entity entity = player.getWorld().getEntityById(payload.entityId());

        if (entity instanceof CoinEntity coin) {
            if (coin.getOwner() == player) {
                ActionOnCoinPowerType.triggerCoinActions(player);
                coin.discard();
            }
        }
    }

    public static void handleFrostGlide(FrostGlideC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ServerPlayerEntity player = context.player();

            BlockState frostBlockState = LesboisBlocks.FROST_BLOCK.getDefaultState();

            if (player.getWorld().getBlockState(payload.pos()).isOf(Blocks.WATER)) {
                frostBlockState = frostBlockState.with(Properties.WATERLOGGED, true);
            }

            player.getWorld().setBlockState(payload.pos(), frostBlockState, 3);

            FrostGlidingPowerType.triggerActions(player);
        });
    }

    public static void handleShockwavePacket(ShockwavePowerTypesC2SPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
        Vec3d velocity = new Vec3d(payload.velocity());
        Vec3d pos = new Vec3d(payload.pos());

        for (Identifier powerId : payload.powerIds()) {
            PowerType powerType = PowerManager.getOptional(powerId)
                    .map(component::getPowerType)
                    .orElse(null);

            if (powerType instanceof ShockwavePowerType shockwavePowerType) {
                shockwavePowerType.triggerShockwave(player, velocity, pos);
            }
            else if (powerType != null) {
                Lesbois.LOGGER.warn("Unexpectedly found power \"{}\" (which doesn't have a shockwave power type) while receiving packet for triggering active power types of player {}!", powerId, player.getName().getString());
            }
            else {
                Lesbois.LOGGER.warn("Found unknown power \"{}\" while receiving packet for triggering shockwave power types of player {}!", powerId, player.getName().getString());
            }
        }
    }

    public static void handleFlyingPacket(FlyingC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ((WingsInterface) context.player()).lesbois$setFlying(payload.isFlying());
        });
    }
}
