package tga.TicksMng;

import tga.Mechanic.IPipeType;
import tga.TotalGreedyAgent;

public class FMTarget {
    public int DirtyTick;
    public IPipeType Entity;

    public FMTarget(IPipeType entity) {
        Entity = entity;
    }

    public void MarkDirty(){
        if (DirtyTick == TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK) return;
        DirtyTick = TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK;
        PipeManager.INTANCE.NeedUpdate.add(this);
    }
}