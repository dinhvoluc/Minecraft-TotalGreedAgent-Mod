package tga.TicksMng;

import java.util.ArrayDeque;
import java.util.Queue;

public class ManMachineManager {
    public static ManMachineManager SERVER_INTANCE;
    public static ManMachineManager CLIENT_INTANCE;
    public Queue<IMMMTarget> NeedUpdate = new ArrayDeque<>();
    public Queue<IMMMTarget> SwapCache = new ArrayDeque<>();

    public int TurnUpdated;

    public void Ticking() {
        TurnUpdated = 0;
        //Reset Queue
        Queue<IMMMTarget> tmp = NeedUpdate;
        NeedUpdate = SwapCache;
        SwapCache = tmp;
        while (!tmp.isEmpty()) {
            TurnUpdated++;
            IMMMTarget target = tmp.poll();
            target.MachineUpdate(this);
        }
    }

    public void ClearQueue() {
        NeedUpdate.clear();
        SwapCache.clear();
    }
}