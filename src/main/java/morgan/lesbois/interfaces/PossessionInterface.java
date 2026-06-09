package morgan.lesbois.interfaces;

import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessionInterface {
    public boolean lesbois$isPossessing();
    public @Nullable MobEntity lesbois$getPossessedEntity();
    public void lesbois$setPossessedEntity(@Nullable MobEntity entity);

    public boolean lesbois$canPossess(MobEntity entity);
    public boolean lesbois$possess(MobEntity entity);
    public void lesbois$unPossess();
}
