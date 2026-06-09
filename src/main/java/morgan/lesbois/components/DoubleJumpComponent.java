package morgan.lesbois.components;

import morgan.lesbois.Lesbois;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class DoubleJumpComponent implements AutoSyncedComponent {
    private final LivingEntity livingEntity;
    private int doubleJumps = 0;

    public DoubleJumpComponent(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public int getDoubleJumps() {
        return doubleJumps;
    }

    public void setDoubleJumps(int doubleJumps) {
        if (this.doubleJumps != doubleJumps) {
            this.doubleJumps = doubleJumps;
            LesboisEntityComponents.DOUBLE_JUMP.sync(this.livingEntity);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        doubleJumps = tag.getInt(Lesbois.stringId("double_jumps"));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt(Lesbois.stringId("double_jumps"), doubleJumps);
    }
}