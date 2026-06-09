package morgan.lesbois.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class LesboisCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GrappleCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UnGrappleCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PossessCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UnPossessCommand.register(dispatcher));
    }
}
