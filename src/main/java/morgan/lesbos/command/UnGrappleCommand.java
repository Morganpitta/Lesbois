package morgan.lesbos.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbos.entity.GrappleHookEntity;
import morgan.lesbos.interfaces.GrappleInterface;
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
        GrappleInterface player = (GrappleInterface) (Object) source.getPlayerOrThrow();

        if (player.lesbos$unGrapple()) {
            source.sendFeedback(() -> Text.literal("Ungrappled!!!!!"), false);
        }
        else {
            source.sendFeedback(() -> Text.literal("Nothing to ungrapple!!!!!"), false);
        }

        return 1;
    }
}
