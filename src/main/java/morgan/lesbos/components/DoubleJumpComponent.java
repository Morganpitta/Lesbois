package morgan.lesbos.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class DoubleJumpComponent implements Component {
    private int doubleJumps = 0;

    public int getDoubleJumps() {
        return doubleJumps;
    }
    public void setDoubleJumps(int value) {
        this.doubleJumps = value;
    }
    public void addDoubleJumps(int amount) {
        this.doubleJumps += amount;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        doubleJumps = tag.getInt("lesbos:double_jumps");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("lesbos:double_jumps", doubleJumps);
    }
}