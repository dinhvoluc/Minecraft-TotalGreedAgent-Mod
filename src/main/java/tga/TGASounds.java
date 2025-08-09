package tga;

import net.minecraft.sound.SoundEvent;

public class TGASounds {
    public static SoundEvent GRINDER;
    public static SoundEvent HAMMER;
    public static void Load() {
        GRINDER = SoundEvent.of(TotalGreedyAgent.GetID(("grinder")));
        HAMMER = SoundEvent.of(TotalGreedyAgent.GetID(("hammer")));
    }
}