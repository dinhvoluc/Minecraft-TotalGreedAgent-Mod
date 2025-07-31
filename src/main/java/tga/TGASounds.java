package tga;

import net.minecraft.sound.SoundEvent;

public class TGASounds {
    public static SoundEvent GRINDER;
    public static void Load() {
        GRINDER = SoundEvent.of(TotalGreedyAgent.GetID(("grinder")));
    }
}