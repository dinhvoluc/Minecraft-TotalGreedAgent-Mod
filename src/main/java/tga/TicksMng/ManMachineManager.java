package tga.TicksMng;

import tga.TotalGreedyAgent;

import java.util.ArrayDeque;
import java.util.Queue;

public class ManMachineManager {
    public static ManMachineManager SERVER_INTANCE;
    public static ManMachineManager CLIENT_INTANCE;
    public Queue<IMMMTarget> NeedUpdate = new ArrayDeque<>();
    public Queue<IMMMTarget> SwapCache = new ArrayDeque<>();

    public void Ticking() {

        int tt = 0;

        //Reset Queue
        Queue<IMMMTarget> tmp = NeedUpdate;
        NeedUpdate = SwapCache;
        SwapCache = tmp;
        while (!tmp.isEmpty()) {
            tt++;
            IMMMTarget target = tmp.poll();
            target.MachineUpdate(this);
        }
        if (this == SERVER_INTANCE && tt > 0)
            TotalGreedyAgent.broadcastDebugMessage("ManManager=" + tt + "(" + TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK + ")", true);
    }

    public void ClearQueue() {
        NeedUpdate.clear();
        SwapCache.clear();
    }
}