package tga.ClUpdate;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import tga.NetEvents.JinrikiGogo;
import tga.TicksMng.IMMMTarget;
import tga.TicksMng.ManMachineManager;
import tga.TotalGreedyAgent;

public class JinrikiWork<T extends BlockEntity> implements IMMMTarget {
    public static BlockEntity PLAYER_WORKING_TARGET;

    public T TARGET;
    public int Updated = -1;
    public int Queuered = -2;
    public static int WorkTick;

    public JinrikiWork(T target){
        TARGET = target;
    }

    @Override
    public void MachineUpdate(ManMachineManager mng) {
        if (Updated == TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK) return;
        Updated = TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK;
        if (PLAYER_WORKING_TARGET != TARGET) {
            WorkTick = 0;
            return;
        }
        QueQueNext(mng);
        if (WorkTick < 20) {
            WorkTick++;
            return;
        }
        WorkTick = 0;
        ClientPlayNetworking.send(new JinrikiGogo(TARGET.getPos()));
    }

    @Override
    public void QueQueNext(ManMachineManager mng) {
        if (Queuered == Updated) return;
        Queuered = Updated;
        mng.NeedUpdate.add(this);
    }
}