package tga.Str;

import net.minecraft.util.math.BlockPos;
import tga.Mechanic.IPipeType;
import tga.Mechanic.PipeManager;

public class FMTarget {
    public int DirtyTick;
    public IPipeType Entity;

    public FMTarget(IPipeType entity) {
        Entity = entity;
    }

    public void MarkDirty(){
        if (DirtyTick == PipeManager.INTANCE.GlobalTick) return;
        DirtyTick = PipeManager.INTANCE.GlobalTick;
        PipeManager.INTANCE.NeedUpdate.add(this);
    }
}