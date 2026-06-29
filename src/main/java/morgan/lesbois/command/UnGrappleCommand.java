package morgan.lesbois.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbois.interfaces.Grapple;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class UnGrappleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("ungrapple")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        Grapple player = (Grapple) (Object) source.getPlayerOrThrow();

        if (player.lesbois$unGrapple()) {
            source.sendFeedback(() -> Text.literal("Ungrappled!!!!!"), false);
        }
        else {
            source.sendFeedback(() -> Text.literal("Nothing to ungrapple!!!!!"), false);
        }

        return 1;
    }
}
