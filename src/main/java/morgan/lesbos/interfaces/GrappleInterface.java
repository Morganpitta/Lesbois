package morgan.lesbos.interfaces;

import morgan.lesbos.entity.GrappleHookEntity;
import org.jetbrains.annotations.Nullable;

public interface GrappleInterface {
    public GrappleHookEntity lesbos$getGrappleHook();
    public void lesbos$setGrappleHook(@Nullable GrappleHookEntity hook);
    public GrappleHookEntity lesbos$grapple(double maxDistance, double unhookDistance, double speed);
    public void lesbos$unGrapple();
}
