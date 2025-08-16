package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import tga.Str.FMTarget;
import tga.TotalGreedyAgent;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeManager {
    public static final float ACTIVE_PRESSURE_GAP = 0.001f;
    public static final long ACTIVE_FLUID_VOL = FluidConstants.BUCKET / 4000;
    public static PipeManager INTANCE;
    public Queue<FMTarget> NeedUpdate = new ArrayDeque<>();
    private Queue<FMTarget> SwapCache = new ArrayDeque<>();

    public int GlobalTick;

    public FMTarget Register(IPipeType target) {
        FMTarget rt = new FMTarget(target);
        NeedUpdate.add(rt);
        return rt;
    }

    public void ServerTick() {
        int tt = 0;
        if (GlobalTick > 0xfffffff) GlobalTick = 1;
        else GlobalTick++;
        //Reset Queue
        Queue<FMTarget> tmp = NeedUpdate;
        NeedUpdate = SwapCache;
        SwapCache = tmp;
        while (!tmp.isEmpty()) {
            tt++;
            FMTarget target = tmp.poll();
            target.Entity.FluidManagerUpdate();
        }
        if (tt > 0)
            TotalGreedyAgent.broadcastDebugMessage(String.format("Pipe Manager updated=%s [TK=%s]", tt, GlobalTick), true);
    }

    public void ClearQueue() {
        NeedUpdate.clear();
        SwapCache.clear();
    }
}