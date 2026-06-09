package morgan.lesbois.interfaces;

import morgan.lesbois.entity.GrappleHookEntity;
import org.jetbrains.annotations.Nullable;

public interface GrappleInterface {
    public GrappleHookEntity lesbois$getGrappleHook();
    public void lesbois$setGrappleHook(@Nullable GrappleHookEntity hook);
    public GrappleHookEntity lesbois$grapple(double maxDistance, double minDistance, double pullSpeed, double lookAssist, double damping);
    public boolean lesbois$unGrapple();
}
