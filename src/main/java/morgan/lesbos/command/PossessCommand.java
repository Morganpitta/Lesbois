package morgan.lesbos.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.lesbos.interfaces.PossessionInterface;
import net.minecraft.entity.LivingEntity;
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

        if ( entity != null && ((PossessionInterface) player).lesbos$possess(entity) )
            source.sendFeedback(()->Text.literal("Possessed " + entity.getType() + "!"), false);
        else
            source.sendFeedback(()->Text.literal("Couldn't possess!"), false);

        return 1;
    }
}
