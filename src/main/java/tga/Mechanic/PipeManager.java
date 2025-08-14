package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.util.math.BlockPos;
import tga.Str.FMTarget;
import tga.TotalGreedyAgent;

import java.util.*;

public class PipeManager {
    public static final long MIN_ACTIVE_PRESSURE_GAP = FluidConstants.BUCKET / 1000;
    public static PipeManager INTANCE;
    public Queue<FMTarget> NeedUpdate = new ArrayDeque<>();
    private Queue<FMTarget> SwapCache = new ArrayDeque<>();

    public int GlobalTick;

    public FMTarget Register(IPipeType target, BlockPos pos) {
        FMTarget rt = new FMTarget(target, pos);
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
}