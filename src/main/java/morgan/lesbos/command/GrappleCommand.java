package morgan.lesbos.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbos.entity.GrappleHookEntity;
import morgan.lesbos.entity.LesbosEntities;
import morgan.lesbos.interfaces.GrappleInterface;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;

import java.util.Collection;

public class GrappleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("grapple")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> execute(context.getSource(),20))
                        .then(
                                CommandManager.argument("maxDistance", DoubleArgumentType.doubleArg(1.0, 100.0))
                                        .executes(context -> execute(context.getSource(), DoubleArgumentType.getDouble(context, "maxDistance")))
                        )
        );
    }

    private static int execute(ServerCommandSource source, double maxDistance) throws CommandSyntaxException {
        GrappleInterface player = (GrappleInterface) (Object) source.getPlayerOrThrow();

        GrappleHookEntity hook = player.lesbos$grapple(maxDistance, 2, 1);

        if (hook != null) {
            source.sendFeedback(() -> Text.literal("Grappled!!!!!"), false);
        }
        else {
            source.sendFeedback(() -> Text.literal("Nothing to grapple!!!!!"), false);
        }

        return 1;
    }
}
