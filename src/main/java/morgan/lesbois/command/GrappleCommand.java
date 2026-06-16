package morgan.lesbois.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbois.entity.GrappleHookEntity;
import morgan.lesbois.interfaces.GrappleInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GrappleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // TODO: Add more config properties?
        dispatcher.register(
                CommandManager.literal("grapple")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> execute(context.getSource(),20))
                        .then(
                                CommandManager.argument("maxDistance", FloatArgumentType.floatArg(1.0F, 100.0F))
                                        .executes(context -> execute(context.getSource(), FloatArgumentType.getFloat(context, "maxDistance")))
                        )
        );
    }

    private static int execute(ServerCommandSource source, float maxDistance) throws CommandSyntaxException {
        GrappleInterface player = (GrappleInterface) (Object) source.getPlayerOrThrow();

        GrappleHookEntity hook = player.lesbois$grapple(maxDistance, 2, false, 1, 1, 0.92F);

        if (hook != null) {
            source.sendFeedback(() -> Text.literal("Grappled!!!!!"), false);
        }
        else {
            source.sendFeedback(() -> Text.literal("Nothing to grapple!!!!!"), false);
        }

        return 1;
    }
}
