package tga.Mechanic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface ITGAManpoweredBlock {
    boolean IsFullCharge();
    void JinrikiGo(int power, ServerPlayerEntity player, World world);
}
