package morgan.lesbos.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class LesbosCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GrappleCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UnGrappleCommand.register(dispatcher));
    }
}
