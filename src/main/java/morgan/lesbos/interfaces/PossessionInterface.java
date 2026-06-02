package morgan.lesbos.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessionInterface {
    public boolean lesbos$isPossessing();
    public @Nullable MobEntity lesbos$getPossessedEntity();
    public void lesbos$setPossessedEntity(@Nullable MobEntity entity);

    public boolean lesbos$canPossess(MobEntity entity);
    public boolean lesbos$possess(MobEntity entity);
    public void lesbos$unPossess();
}
