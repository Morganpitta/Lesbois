package morgan.lesbois.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PossessCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("possess")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        PlayerEntity player = source.getPlayerOrThrow();

        MobEntity entity = player.getWorld().getClosestEntity(
                MobEntity.class,
                TargetPredicate.DEFAULT,
                player,
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getBoundingBox().expand(16)
        );

        if ( entity != null && ((PossessionInterface) player).lesbois$possess(entity) )
            source.sendFeedback(()->Text.literal("Possessed ").append(Text.translatable(entity.getType().getTranslationKey())), false);
        else
            source.sendFeedback(()->Text.literal("Couldn't possess!"), false);

        return 1;
    }
}
