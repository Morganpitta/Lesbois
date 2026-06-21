package morgan.lesbois.mixin.common.power.type;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.type.Active;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import morgan.lesbois.network.packet.ShockwavePowerTypesC2SPacket;
import morgan.lesbois.network.packet.UseKeyReleasePowerTypesC2SPacket;
import morgan.lesbois.powers.ActionOnKeyReleasePowerType;
import morgan.lesbois.powers.ShockwavePowerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mixin(Active.class)
public interface ActiveMixin {
    @Environment(EnvType.CLIENT)
    @Inject(
            method = "integrateCallback",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;putAll(Ljava/util/Map;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private static void integrateCallback(MinecraftClient client, CallbackInfo ci) {
        if (client.player == null) {
            return;
        }

        List<PowerType> powerTypes = PowerHolderComponent.getOptional(client.player).orElseThrow().getPowerTypes();
        List<ActionOnKeyReleasePowerType> triggeredKeyReleasePowerTypes = new LinkedList<>();
        List<ShockwavePowerType> triggeredShockwavePowerTypes = new LinkedList<>();

        Map<String, Boolean> currentKeybindingStates = new HashMap<>();
        for (PowerType powerType : powerTypes) {
            if (powerType instanceof ActionOnKeyReleasePowerType keyReleasePowerType) {
                KeyBindingReference keyBindingReference = keyReleasePowerType.getKey();
                TriState keyPressed = keyBindingReference.asKeyBinding()
                        .map(KeyBinding::isPressed)
                        .map(TriState::of)
                        .orElse(TriState.DEFAULT);

                if (!currentKeybindingStates.computeIfAbsent(keyBindingReference.id(), k -> keyPressed.get()) && ApoliClient.lastKeyBindingStates.getOrDefault(keyBindingReference.id(), false)) {
                    triggeredKeyReleasePowerTypes.add(keyReleasePowerType);
                }
            }
            else if (powerType instanceof ShockwavePowerType shockwavePowerType) {
                KeyBindingReference keyBindingReference = shockwavePowerType.getKey();
                TriState keyPressed = keyBindingReference.asKeyBinding()
                        .map(KeyBinding::isPressed)
                        .map(TriState::of)
                        .orElse(TriState.DEFAULT);

                if (!currentKeybindingStates.computeIfAbsent(keyBindingReference.id(), k -> keyPressed.get()) && ApoliClient.lastKeyBindingStates.getOrDefault(keyBindingReference.id(), false)) {
                    triggeredShockwavePowerTypes.add(shockwavePowerType);
                }
            }

        }

        ApoliClient.lastKeyBindingStates.putAll(currentKeybindingStates);

        List<Identifier> keyReleasePowerTypeIds = triggeredKeyReleasePowerTypes.stream()
                .peek(powerType -> {if (powerType.isActive()) {powerType.onUse();}})
                .map(PowerType::getPower)
                .map(Power::getId)
                .toList();

        if (!keyReleasePowerTypeIds.isEmpty()) {
            ClientPlayNetworking.send(new UseKeyReleasePowerTypesC2SPacket(keyReleasePowerTypeIds));
        }

        Vec3d velocity = client.player.getVelocity();
        Vec3d pos = client.player.getPos();
        List<Identifier> shockwavePowerTypePackets = triggeredShockwavePowerTypes.stream()
                .filter(PowerType::isActive)
                .peek(ShockwavePowerType::onUse)
                .map(PowerType::getPower)
                .map(Power::getId)
                .toList();

        if (!shockwavePowerTypePackets.isEmpty()) {
            ClientPlayNetworking.send(new ShockwavePowerTypesC2SPacket(shockwavePowerTypePackets, velocity, pos));
        }
    }
}
