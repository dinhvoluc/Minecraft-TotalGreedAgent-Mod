package tga.Mechanic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface ITGAManpoweredBlock {
    float GetJinrikiMul();
    void JinrikiGo(int power, ServerPlayerEntity player, World world);
}
